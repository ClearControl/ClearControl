package clearcontrol.microscope.lightsheet.timelapse;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.processor.MetaDataFusion;
import clearcontrol.microscope.lightsheet.stacks.MetaDataView;
import clearcontrol.microscope.lightsheet.state.AcquisitionType;
import clearcontrol.microscope.lightsheet.state.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.stacks.metadata.MetaDataAcquisitionType;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.timelapse.TimelapseBase;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import clearcontrol.stack.metadata.MetaDataChannel;
import clearcontrol.stack.metadata.MetaDataOrdinals;
import clearcontrol.stack.metadata.StackMetaData;

/**
 * Standard Timelapse implementation
 *
 * @author royer
 */
public class LightSheetTimelapse extends TimelapseBase implements
                                 TimelapseInterface,
                                 LoggingInterface
{

  private static final long cTimeOut = 1000;
  private static final int cMinimumNumberOfAvailableStacks = 16;
  private static final int cMaximumNumberOfAvailableStacks = 16;
  private static final int cMaximumNumberOfLiveStacks = 16;

  private final LightSheetMicroscope mLightSheetMicroscope;

  private final Variable<Boolean> mFuseStacksVariable =
                                                      new Variable<Boolean>("FuseStacks",
                                                                            true);

  private final Variable<Boolean> mFuseStacksPerCameraVariable =
                                                               new Variable<Boolean>("FuseStacksPerCamera",
                                                                                     true);

  private final Variable<Boolean> mInterleavedAcquisitionVariable =
                                                                  new Variable<Boolean>("InterleavedAcquisition",
                                                                                        false);


  /**
   * @param pLightSheetMicroscope
   *          microscope
   */
  public LightSheetTimelapse(LightSheetMicroscope pLightSheetMicroscope)
  {
    super(pLightSheetMicroscope);
    mLightSheetMicroscope = pLightSheetMicroscope;

    /*
    boolean lFuseStacks = getFuseStacksVariable().get()
                          && n.getMetaData()
                              .hasValue(MetaDataFusion.Fused);
    
    
    /**/

  }

  @Override
  public void acquire()
  {
    try
    {
      info("acquiring timepoint: "
           + getTimePointCounterVariable().get());

      mLightSheetMicroscope.useRecycler("3DTimelapse",
                                        cMinimumNumberOfAvailableStacks,
                                        cMaximumNumberOfAvailableStacks,
                                        cMaximumNumberOfLiveStacks);

      @SuppressWarnings("unchecked")
      AcquisitionStateManager<LightSheetAcquisitionStateInterface<?>> lAcquisitionStateManager =
                                                                                               mLightSheetMicroscope.getDevice(AcquisitionStateManager.class,
                                                                                                                               0);

      LightSheetAcquisitionStateInterface<?> lCurrentState =
                                                           lAcquisitionStateManager.getCurrentState();

      if (getInterleavedAcquisitionVariable().get())
        interleavedAcquisition(lCurrentState);
      else
        sequentialAcquisition(lCurrentState);

    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

  }

  private void interleavedAcquisition(LightSheetAcquisitionStateInterface<?> pCurrentState)
  {
    // TODO not supported for now

  }

  private void sequentialAcquisition(LightSheetAcquisitionStateInterface<?> pCurrentState) throws InterruptedException,
                                                                                           ExecutionException,
                                                                                           TimeoutException
  {

    int lNumberOfDetectionArms =
                               mLightSheetMicroscope.getNumberOfDetectionArms();

    int lNumberOfLightSheets =
                             mLightSheetMicroscope.getNumberOfLightSheets();

    HashMap<Integer, LightSheetMicroscopeQueue> lViewToQueueMap =
                                                                new HashMap<>();

    // preparing queues:
    for (int l = 0; l < lNumberOfLightSheets; l++)
      if (pCurrentState.getLightSheetOnOffVariable(l).get())
      {
        LightSheetMicroscopeQueue lQueueForView =
                                                getQueueForSingleLightSheet(pCurrentState,
                                                                            l);

        lViewToQueueMap.put(l, lQueueForView);
      }

    // playing the queues in sequence:

    for (int l = 0; l < lNumberOfLightSheets; l++)
      if (pCurrentState.getLightSheetOnOffVariable(l).get())
      {
        LightSheetMicroscopeQueue lQueueForView =
                                                lViewToQueueMap.get(l);

        for (int c = 0; c < lNumberOfDetectionArms; c++)
          if (pCurrentState.getCameraOnOffVariable(c).get())
          {

            StackMetaData lMetaData =
                                    lQueueForView.getCameraDeviceQueue(c)
                                                 .getMetaDataVariable()
                                                 .get();

            lMetaData.addEntry(MetaDataAcquisitionType.AcquisitionType,
                               AcquisitionType.TimeLapse);
            lMetaData.addEntry(MetaDataView.Camera, c);
            lMetaData.addEntry(MetaDataView.LightSheet, l);

            if (getFuseStacksVariable().get())
            {
              if (getFuseStacksPerCameraVariable().get())
                lMetaData.addEntry(MetaDataFusion.RequestPerCameraFusion,
                                   true);
              else
                lMetaData.addEntry(MetaDataFusion.RequestFullFusion,
                                   true);

            }
            else
            {
              String lCxLyString =
                                 MetaDataView.getCxLyString(lMetaData);
              lMetaData.addEntry(MetaDataChannel.Channel,
                                 lCxLyString);
            }
          }

        mLightSheetMicroscope.playQueueAndWait(lQueueForView,
                                               cTimeOut,
                                               TimeUnit.SECONDS);

      }

  }

  protected LightSheetMicroscopeQueue getQueueForSingleLightSheet(LightSheetAcquisitionStateInterface<?> pCurrentState,
                                                                  int pLightSheetIndex)
  {
    int lNumberOfDetectionArms =
                               mLightSheetMicroscope.getNumberOfDetectionArms();

    @SuppressWarnings("unused")
    int lNumberOfLightSheets =
                             mLightSheetMicroscope.getNumberOfLightSheets();

    int lNumberOfLaserLines =
                            mLightSheetMicroscope.getNumberOfLaserLines();

    LightSheetMicroscopeQueue lQueue =
                                     pCurrentState.getQueue(0,
                                                            lNumberOfDetectionArms,
                                                            pLightSheetIndex,
                                                            pLightSheetIndex + 1,
                                                            0,
                                                            lNumberOfLaserLines);
    lQueue.addMetaDataEntry(MetaDataOrdinals.TimePoint,
                            getTimePointCounterVariable().get());

    return lQueue;
  }

  /**
   * Returns the variable holding the flag interleaved-acquisition
   * 
   * @return variable holding the flag interleaved-acquisition
   */
  public Variable<Boolean> getInterleavedAcquisitionVariable()
  {
    return mInterleavedAcquisitionVariable;
  }

  /**
   * Returns the variable holding the boolean flag that decides whether stacks
   * should or should not be fused.
   * 
   * @return fuse stacks variable
   */
  public Variable<Boolean> getFuseStacksVariable()
  {
    return mFuseStacksVariable;
  }

  /**
   * Returns the variable holding the boolean flag that decides whether stacks
   * should or should not be fused.
   * 
   * @return fuse stacks variable
   */
  public Variable<Boolean> getFuseStacksPerCameraVariable()
  {
    return mFuseStacksPerCameraVariable;
  }



}
