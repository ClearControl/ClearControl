package clearcontrol.microscope.lightsheet.signalgen;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.queue.StateQueueDeviceInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.signalgen.movement.Movement;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.ConstantStave;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.signalgen.staves.LightSheetOpticalSwitchStaves;
import clearcontrol.microscope.lightsheet.signalgen.staves.LightSheetStaves;

/**
 * This device knows how to generate the signals for a light sheet microscope
 * (both detection and illumination signals)
 *
 * @author royer
 */
public class LightSheetSignalGeneratorDevice extends VirtualDevice
                                             implements
                                             SignalGeneratorInterface,
                                             StateQueueDeviceInterface,
                                             LoggingInterface
{

  private final SignalGeneratorInterface mDelegatedSignalGenerator;

  private final ConcurrentHashMap<DetectionArm, ConstantStave> mDetectionArmToStaveZMap =
                                                                                        new ConcurrentHashMap<>();
  private final ConcurrentHashMap<DetectionArm, Integer> mDetectionArmToStaveIndexMap =
                                                                                      new ConcurrentHashMap<>();
  private final ConcurrentHashMap<LightSheet, LightSheetStaves> mLightSheetToStavesMap =
                                                                                       new ConcurrentHashMap<>();
  private ConcurrentHashMap<LightSheetOpticalSwitch, LightSheetOpticalSwitchStaves> mOpticalSwitchToStavesMap =
                                                                                                              new ConcurrentHashMap<>();

  private Movement mBeforeExposureMovement;

  private Movement mExposureMovement;

  /**
   * Wraps a signal generator with a lightsheet signal generation. This
   * lightsheet signal generator simply adds a layer that translate detection
   * arm and lightsheet paraeters to actual signals.
   * 
   * @param pSignalGeneratorInterface
   *          delegated signal generator
   * @return lightsheet signal generator
   */
  public static LightSheetSignalGeneratorDevice wrap(SignalGeneratorInterface pSignalGeneratorInterface)
  {
    return new LightSheetSignalGeneratorDevice(pSignalGeneratorInterface);
  }

  /**
   * Instanciates a lightsheet signal generator that delegates to another signal
   * generator for the actual signal generation. This signal generator simply
   * adds a layer that translate detection arm and lightsheet paraeters to
   * actual signals.
   * 
   * @param pSignalGeneratorInterface
   *          delegated signal generator
   */
  public LightSheetSignalGeneratorDevice(SignalGeneratorInterface pSignalGeneratorInterface)
  {
    super("LightSheet" + pSignalGeneratorInterface.getName());
    mDelegatedSignalGenerator = pSignalGeneratorInterface;

    setupStagingScore();

  }

  /**
   * Setting up the two movements that are nescessary for one image acquisition
   * and corresponding lightshet scanning.
   */
  private void setupStagingScore()
  {
    mBeforeExposureMovement = new Movement("BeforeExposure");
    mExposureMovement = new Movement("Exposure");

    ScoreInterface lStagingScore =
                                 mDelegatedSignalGenerator.getStagingScore();

    lStagingScore.addMovement(mBeforeExposureMovement);
    lStagingScore.addMovement(mExposureMovement);
  }

  /**
   * Adds a detection arm.
   * 
   * @param pDetectionArm
   *          detection arm
   */
  public void addDetectionArm(DetectionArm pDetectionArm)
  {
    ConstantStave lDetectionZStave = new ConstantStave("detection.z",
                                                       0);
    mDetectionArmToStaveZMap.put(pDetectionArm, lDetectionZStave);

    int lStaveIndex =
                    MachineConfiguration.getCurrentMachineConfiguration()
                                        .getIntegerProperty("device.lsm.detection."
                                                            + pDetectionArm.getName()
                                                            + ".z.index",
                                                            0);

    mDetectionArmToStaveIndexMap.put(pDetectionArm, lStaveIndex);

    // Analog outputs before exposure:
    mBeforeExposureMovement.setStave(lStaveIndex, lDetectionZStave);

    // Analog outputs at exposure:
    mExposureMovement.setStave(lStaveIndex, lDetectionZStave);

    pDetectionArm.getZVariable().addSetListener((o, n) -> {
      if (!o.equals(n))
        update();
    });

  }

  /**
   * Adds a light sheet
   * 
   * @param pLightSheet
   *          light sheet
   */
  public void addLightSheet(LightSheet pLightSheet)
  {
    LightSheetStaves lLightSheetStaves =
                                       new LightSheetStaves(pLightSheet);

    mLightSheetToStavesMap.put(pLightSheet, lLightSheetStaves);

    lLightSheetStaves.ensureStavesAddedToBeforeExposureMovement(mBeforeExposureMovement);
    lLightSheetStaves.ensureStavesAddedToExposureMovement(mExposureMovement);

  }

  /**
   * Adds light sheet optical switch
   * 
   * @param pLightSheetOpticalSwitch
   *          optical switch
   */
  public void addOpticalSwitch(LightSheetOpticalSwitch pLightSheetOpticalSwitch)
  {
    LightSheetOpticalSwitchStaves lLightSheetOpticalSwitchStaves =
                                                                 new LightSheetOpticalSwitchStaves(pLightSheetOpticalSwitch,
                                                                                                   0);

    mOpticalSwitchToStavesMap.put(pLightSheetOpticalSwitch,
                                  lLightSheetOpticalSwitchStaves);

    lLightSheetOpticalSwitchStaves.addStavesToMovements(mBeforeExposureMovement,
                                                        mExposureMovement);

  }

  /**
   * Updates underlying signal generation staves and stack camera configuration.
   */
  private void update()
  {
    synchronized (this)
    {
      info("Updating: " + getName());

      for (Map.Entry<DetectionArm, ConstantStave> lEntry : mDetectionArmToStaveZMap.entrySet())
      {
        DetectionArm lDetectionArm = lEntry.getKey();
        ConstantStave lDetectionZStave = lEntry.getValue();

        BoundedVariable<Number> lZVariable =
                                           lDetectionArm.getZVariable();
        Variable<UnivariateAffineFunction> lZFunction =
                                                      lDetectionArm.getZFunction();

        double lZFocus = lZVariable.get().doubleValue();
        float lZFocusTransformed = (float) lZFunction.get()
                                                     .value(lZFocus);
        lDetectionZStave.setValue(lZFocusTransformed);
      }

      for (Map.Entry<LightSheet, LightSheetStaves> lEntry : mLightSheetToStavesMap.entrySet())
      {
        LightSheetStaves lLightSheetStaves = lEntry.getValue();
        lLightSheetStaves.update(mBeforeExposureMovement,
                                 mExposureMovement);
      }

      for (Entry<LightSheetOpticalSwitch, LightSheetOpticalSwitchStaves> lEntry : mOpticalSwitchToStavesMap.entrySet())
      {
        LightSheetOpticalSwitchStaves lLightSheetOpticalSwitchStaves =
                                                                     lEntry.getValue();
        lLightSheetOpticalSwitchStaves.update();
      }

    }
  }

  @Override
  public void clearQueue()
  {
    mDelegatedSignalGenerator.clearQueue();
  }

  @Override
  public void addCurrentStateToQueue()
  {
    // first we make sure that the staging score is up-to-date given all the
    // detection and illumination parameters.f
    update();
    // then add teh current state to the queue which corresponds to adding teh
    // staging score to the actual movement that represents the queue.
    mDelegatedSignalGenerator.addCurrentStateToQueue();
  }

  @Override
  public void finalizeQueue()
  {
    mDelegatedSignalGenerator.finalizeQueue();
  }

  @Override
  public int getQueueLength()
  {
    return mDelegatedSignalGenerator.getQueueLength();
  }

  @Override
  public Future<Boolean> playQueue()
  {
    return mDelegatedSignalGenerator.playQueue();
  }

  @Override
  public double getTemporalGranularityInMicroseconds()
  {
    return mDelegatedSignalGenerator.getTemporalGranularityInMicroseconds();
  }

  @Override
  public boolean playScore(ScoreInterface pScore)
  {
    return mDelegatedSignalGenerator.playScore(pScore);
  }

  @Override
  public ScoreInterface getStagingScore()
  {
    return mDelegatedSignalGenerator.getStagingScore();
  }

  @Override
  public ScoreInterface getQueuedScore()
  {
    return mDelegatedSignalGenerator.getQueuedScore();
  }

  @Override
  public Variable<Boolean> getTriggerVariable()
  {
    return mDelegatedSignalGenerator.getTriggerVariable();
  }

  @Override
  public boolean isPlaying()
  {
    return mDelegatedSignalGenerator.isPlaying();
  }

  @Override
  public long estimatePlayTime(TimeUnit pTimeUnit)
  {
    return mDelegatedSignalGenerator.estimatePlayTime(pTimeUnit);
  }

}
