package clearcontrol.microscope.lightsheet.signalgen;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.devices.signalgen.SignalGeneratorQueue;
import clearcontrol.devices.signalgen.movement.Movement;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetQueue;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitchQueue;
import clearcontrol.microscope.lightsheet.signalgen.staves.DetectionArmStaves;
import clearcontrol.microscope.lightsheet.signalgen.staves.LightSheetOpticalSwitchStaves;
import clearcontrol.microscope.lightsheet.signalgen.staves.LightSheetStaves;

/**
 * Light sheet signal generator queue
 *
 * @author royer
 */
public class LightSheetSignalGeneratorQueue extends
                                            SignalGeneratorQueue
                                            implements
                                            QueueInterface,
                                            LoggingInterface

{

  private SignalGeneratorQueue mDelegatedQueue;
  private LightSheetSignalGeneratorDevice mLightSheetSignalGeneratorDevice;

  private Movement mBeforeExposureMovement, mExposureMovement;

  final ConcurrentHashMap<DetectionArm, DetectionArmStaves> mDetectionArmToStavesMap =
                                                                                     new ConcurrentHashMap<>();

  final ConcurrentHashMap<LightSheet, LightSheetStaves> mLightSheetToStavesMap =
                                                                               new ConcurrentHashMap<>();
  final ConcurrentHashMap<LightSheetOpticalSwitch, LightSheetOpticalSwitchStaves> mOpticalSwitchToStavesMap =
                                                                                                            new ConcurrentHashMap<>();

  /**
   * Instantiates a lightsheet signal generator queue device
   * 
   * @param pLightSheetSignalGeneratorDevice
   *          lightsheet signal generator parent
   * @param pDelegatedQueue
   *          delegated signal generator queue
   */
  public LightSheetSignalGeneratorQueue(LightSheetSignalGeneratorDevice pLightSheetSignalGeneratorDevice,
                                        SignalGeneratorQueue pDelegatedQueue)
  {
    mLightSheetSignalGeneratorDevice =
                                     pLightSheetSignalGeneratorDevice;
    mDelegatedQueue = pDelegatedQueue;

    setupStagingScore();
  }

  /**
   * Returns the delegated queue
   * 
   * @return delegated queue
   */
  public SignalGeneratorQueue getDelegatedQueue()
  {
    return mDelegatedQueue;
  }

  /**
   * Returns the lightsheet optical switch parent
   * 
   * @return optical switch parent
   */
  public LightSheetSignalGeneratorDevice getLightSheetSignalGeneratorDevice()
  {
    return mLightSheetSignalGeneratorDevice;
  }

  /**
   * Setting up the two movements that are necessary for one image acquisition
   * and corresponding lightsheet scanning.
   */
  private void setupStagingScore()
  {
    mBeforeExposureMovement = new Movement("BeforeExposure");
    mExposureMovement = new Movement("Exposure");

    ScoreInterface lStagingScore = mDelegatedQueue.getStagingScore();

    lStagingScore.addMovement(mBeforeExposureMovement);
    lStagingScore.addMovement(mExposureMovement);
  }

  /**
   * Adds a detection arm.
   * 
   * @param pDetectionArmQueue
   *          detection arm queue
   */
  public void addDetectionArmQueue(DetectionArmQueue pDetectionArmQueue)
  {
    DetectionArmStaves lDetectionArmStaves =
                                           new DetectionArmStaves(pDetectionArmQueue);

    mDetectionArmToStavesMap.put(pDetectionArmQueue.getDetectionArm(),
                                 lDetectionArmStaves);

    lDetectionArmStaves.addStavesToMovements(mBeforeExposureMovement,
                                             mExposureMovement);

    /*
    pDetectionArm.getZVariable().addSetListener((o, n) -> {
      if (!o.equals(n))
        update();
    });/**/

  }

  /**
   * Adds a light sheet
   * 
   * @param pLightSheetQueue
   *          light sheet queue
   */
  public void addLightSheetQueue(LightSheetQueue pLightSheetQueue)
  {
    LightSheetStaves lLightSheetStaves =
                                       new LightSheetStaves(pLightSheetQueue);

    mLightSheetToStavesMap.put(pLightSheetQueue.getLightSheet(),
                               lLightSheetStaves);

    lLightSheetStaves.addStavesToMovements(mBeforeExposureMovement,
                                           mExposureMovement);

  }

  /**
   * Adds light sheet optical switch
   * 
   * @param pLightSheetOpticalSwitchQueue
   *          optical switch
   */
  public void addOpticalSwitchQueue(LightSheetOpticalSwitchQueue pLightSheetOpticalSwitchQueue)
  {
    LightSheetOpticalSwitchStaves lLightSheetOpticalSwitchStaves =
                                                                 new LightSheetOpticalSwitchStaves(pLightSheetOpticalSwitchQueue,
                                                                                                   0);

    mOpticalSwitchToStavesMap.put(pLightSheetOpticalSwitchQueue.getLightSheetOpticalSwitch(),
                                  lLightSheetOpticalSwitchStaves);

    lLightSheetOpticalSwitchStaves.addStavesToMovements(mBeforeExposureMovement,
                                                        mExposureMovement);

  }

  @Override
  public void clearQueue()
  {
    mDelegatedQueue.clearQueue();
  }

  @Override
  public void addCurrentStateToQueue()
  {
    // first we make sure that the staging score is up-to-date given all the
    // detection and illumination parameters.f
    update();
    // then add the current state to the queue which corresponds to adding the
    // staging score to the actual movement that represents the queue.
    mDelegatedQueue.addCurrentStateToQueue();
  }

  /**
   * Updates underlying signal generation staves and stack camera configuration.
   */
  private void update()
  {
    synchronized (this)
    {
      // info("Updating: " + mLightSheetSignalGeneratorDevice.getName());

      for (Map.Entry<DetectionArm, DetectionArmStaves> lEntry : mDetectionArmToStavesMap.entrySet())
      {
        DetectionArmStaves lDetectionStaves = lEntry.getValue();

        lDetectionStaves.update(mBeforeExposureMovement,
                                mExposureMovement);
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
        lLightSheetOpticalSwitchStaves.update(mBeforeExposureMovement,
                                              mExposureMovement);
      }

    }
  }

  @Override
  public void finalizeQueue()
  {
    mDelegatedQueue.finalizeQueue();
  }

  @Override
  public int getQueueLength()
  {
    return mDelegatedQueue.getQueueLength();
  }

}
