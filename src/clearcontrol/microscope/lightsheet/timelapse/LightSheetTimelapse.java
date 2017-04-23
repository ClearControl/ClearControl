package clearcontrol.microscope.lightsheet.timelapse;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.acquisition.AcquisitionType;
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.stacks.metadata.MetaDataAcquisitionType;
import clearcontrol.microscope.stacks.metadata.MetaDataView;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.timelapse.TimelapseBase;
import clearcontrol.microscope.timelapse.TimelapseInterface;
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

  private final Variable<Long> mTimePointIndex =
                                               new Variable<Long>("TimePointIndex",
                                                                  0L);

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

      mTimePointIndex.increment();

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
    {
      LightSheetMicroscopeQueue lQueueForView =
                                              getQueueForSingleLightSheet(pCurrentState,
                                                                          l);

      lViewToQueueMap.put(l, lQueueForView);
    }

    // playing the queues in sequence:

    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      LightSheetMicroscopeQueue lQueueForView =
                                              lViewToQueueMap.get(l);

      for (int c = 0; c < lNumberOfDetectionArms; c++)
      {

        StackMetaData lMetaData =
                                lQueueForView.getCameraDeviceQueue(c)
                                             .getMetaDataVariable()
                                             .get();

        lMetaData.addEntry(MetaDataAcquisitionType.AcquisitionType,
                           AcquisitionType.TimeLapse);
        lMetaData.addEntry(MetaDataView.Camera, c);
        lMetaData.addEntry(MetaDataView.LightSheet, l);
      }

      mLightSheetMicroscope.playQueueAndWait(lQueueForView,
                                             cTimeOut,
                                             TimeUnit.SECONDS);

    }

  }

  protected LightSheetMicroscopeQueue getQueueForSingleLightSheet(LightSheetAcquisitionStateInterface<?> pCurrentState,
                                                                  int pLightSheetIndex)
  {
    int lNumberOfLightSheets =
                             mLightSheetMicroscope.getNumberOfLightSheets();

    @SuppressWarnings("unused")
    int lNumberOfDetectionArms =
                               mLightSheetMicroscope.getNumberOfDetectionArms();

    for (int i = 0; i < lNumberOfLightSheets; i++)
      pCurrentState.getLightSheetOnOffVariable(i)
                   .set(i == pLightSheetIndex);

    pCurrentState.updateQueue(mLightSheetMicroscope);
    LightSheetMicroscopeQueue lQueue = pCurrentState.getQueue();
    lQueue.addMetaDataEntry(MetaDataOrdinals.TimePoint,
                            mTimePointIndex.get());

    return lQueue;
  }

  /**
   * Returns the variable holding the flag ointerleaved-acquisition
   * 
   * @return variable holding the flag ointerleaved-acquisition
   */
  public Variable<Boolean> getInterleavedAcquisitionVariable()
  {
    return mInterleavedAcquisitionVariable;
  }

}
