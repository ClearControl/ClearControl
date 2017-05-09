package clearcontrol.microscope.lightsheet.signalgen;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
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
public class LightSheetSignalGeneratorQueue implements
                                            QueueInterface,
                                            LoggingInterface

{

  private SignalGeneratorQueue mDelegatedQueue;
  private LightSheetSignalGeneratorDevice mLightSheetSignalGeneratorDevice;

  private Variable<Integer> mSelectedLightSheetIndexVariable =
                                                             new Variable<Integer>("SelectedLightSheetIndex",
                                                                                   0);

  private Movement mBeforeExposureMovement, mExposureMovement,
      mFinalMovement;

  final ConcurrentHashMap<DetectionArm, DetectionArmStaves> mDetectionArmToStavesMap =
                                                                                     new ConcurrentHashMap<>();

  final ArrayList<LightSheet> mLightSheetList =
                                              new ArrayList<LightSheet>();

  final ConcurrentHashMap<LightSheet, LightSheetStaves> mLightSheetToStavesMap =
                                                                               new ConcurrentHashMap<>();
  final ConcurrentHashMap<LightSheetOpticalSwitch, LightSheetOpticalSwitchStaves> mOpticalSwitchToStavesMap =
                                                                                                            new ConcurrentHashMap<>();

  /**
   * Instantiates a lightsheet signal generator queue device
   * 
   * @param pLightSheetSignalGeneratorDevice
   *          lightsheet signal generator parent
   */
  public LightSheetSignalGeneratorQueue(LightSheetSignalGeneratorDevice pLightSheetSignalGeneratorDevice)
  {
    mLightSheetSignalGeneratorDevice =
                                     pLightSheetSignalGeneratorDevice;
    mDelegatedQueue =
                    pLightSheetSignalGeneratorDevice.getDelegatedSignalGenerator()
                                                    .requestQueue();

    setupStagingAndFinalizsationScores();
  }

  /**
   * Copy constructors that instantiates a lightsheet signal generator queue
   * from a template.
   * 
   * @param pTemplateQueue
   *          queue template
   */
  public LightSheetSignalGeneratorQueue(LightSheetSignalGeneratorQueue pTemplateQueue)
  {
    mLightSheetSignalGeneratorDevice =
                                     pTemplateQueue.getLightSheetSignalGeneratorDevice();
    mDelegatedQueue =
                    getLightSheetSignalGeneratorDevice().getDelegatedSignalGenerator()
                                                        .requestQueue();

    getSelectedLightSheetIndexVariable().set(pTemplateQueue.getSelectedLightSheetIndexVariable()
                                                           .get());

    setupStagingAndFinalizsationScores();
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
  private void setupStagingAndFinalizsationScores()
  {
    mBeforeExposureMovement = new Movement("BeforeExposure");
    mExposureMovement = new Movement("Exposure");
    mFinalMovement = new Movement("Final");

    ScoreInterface lStagingScore = mDelegatedQueue.getStagingScore();

    lStagingScore.addMovement(mBeforeExposureMovement);
    lStagingScore.addMovement(mExposureMovement);

    ScoreInterface lFinalisationScore =
                                      mDelegatedQueue.getFinalizationScore();

    lFinalisationScore.addMovement(mFinalMovement);
  }

  /**
   * Returns the staging score
   * 
   * @return staging score
   */
  public ScoreInterface getStagingScore()
  {
    return mDelegatedQueue.getStagingScore();
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
                                             mExposureMovement,
                                             mFinalMovement);

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

    mLightSheetList.add(pLightSheetQueue.getLightSheet());

    mLightSheetToStavesMap.put(pLightSheetQueue.getLightSheet(),
                               lLightSheetStaves);

    lLightSheetStaves.addStavesToMovements(mBeforeExposureMovement,
                                           mExposureMovement,
                                           mFinalMovement);

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
                                                        mExposureMovement,
                                                        mFinalMovement);

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

    /* ScoreVisualizerJFrame.visualize("StagingScore",
                                    mDelegatedQueue.getStagingScore());/**/

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

      Variable<Boolean> lIsSharedLightSheetControl =
                                                   getLightSheetSignalGeneratorDevice().getIsSharedLightSheetControlVariable();

      if (lIsSharedLightSheetControl.get())
      {
        int lSelectedLightSheetIndex =
                                     getSelectedLightSheetIndexVariable().get();

        if (lSelectedLightSheetIndex < 0
            || lSelectedLightSheetIndex >= mLightSheetList.size())
        {
          warning("Selected lightsheet is not valid: %d, using 0 instead",
                  lSelectedLightSheetIndex);
          lSelectedLightSheetIndex = 0;
        }

        LightSheet lSelectedLightSheet =
                                       mLightSheetList.get(lSelectedLightSheetIndex);

        LightSheetStaves lLightSheetStaves =
                                           mLightSheetToStavesMap.get(lSelectedLightSheet);

        lLightSheetStaves.update(mBeforeExposureMovement,
                                 mExposureMovement,
                                 mFinalMovement);

      }
      else
        for (Map.Entry<LightSheet, LightSheetStaves> lEntry : mLightSheetToStavesMap.entrySet())
        {
          LightSheetStaves lLightSheetStaves = lEntry.getValue();
          lLightSheetStaves.update(mBeforeExposureMovement,
                                   mExposureMovement,
                                   mFinalMovement);
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
    update();
    mDelegatedQueue.finalizeQueue();
  }

  @Override
  public int getQueueLength()
  {
    return mDelegatedQueue.getQueueLength();
  }

  /**
   * In the case that we are in a shared lightsheet control situation, this
   * variable holds the index of the lightsheet to use to generate the control
   * signals.
   * 
   * @return selected lightsheet variable
   */
  public Variable<Integer> getSelectedLightSheetIndexVariable()
  {
    return mSelectedLightSheetIndexVariable;
  }

}
