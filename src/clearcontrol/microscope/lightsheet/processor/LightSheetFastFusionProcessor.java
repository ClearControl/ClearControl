package clearcontrol.microscope.lightsheet.processor;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Triple;

import clearcl.ClearCLContext;
import clearcl.ClearCLImage;
import clearcl.util.ElapsedTime;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.stacks.MetaDataView;
import clearcontrol.microscope.lightsheet.stacks.MetaDataViewFlags;
import clearcontrol.microscope.lightsheet.state.AcquisitionType;
import clearcontrol.microscope.stacks.metadata.MetaDataAcquisitionType;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.metadata.MetaDataChannel;
import clearcontrol.stack.metadata.MetaDataOrdinals;
import clearcontrol.stack.metadata.StackMetaData;
import clearcontrol.stack.processor.StackProcessorInterface;
import clearcontrol.stack.processor.clearcl.ClearCLStackProcessorBase;
import coremem.recycling.RecyclerInterface;

/**
 *
 *
 * @author royer
 */
public class LightSheetFastFusionProcessor extends
                                           ClearCLStackProcessorBase
                                           implements
                                           StackProcessorInterface,
                                           LoggingInterface
{
  private final LightSheetMicroscope mLightSheetMicroscope;
  private LightSheetFastFusionEngine mEngine;

  ConcurrentLinkedQueue<Triple<Integer, StackMetaData, ClearCLImage>> mFusedStackQueue =
                                                                                       new ConcurrentLinkedQueue<>();

  private volatile StackInterface mFusedStack;

  /**
   * Instantiates a lightsheet stack processor
   * 
   * @param pProcessorName
   *          processor name
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   * @param pContext
   *          ClearCL context to use
   */
  public LightSheetFastFusionProcessor(String pProcessorName,
                                       LightSheetMicroscope pLightSheetMicroscope,
                                       ClearCLContext pContext)
  {
    super(pProcessorName, pContext);
    mLightSheetMicroscope = pLightSheetMicroscope;

  }

  @Override
  public StackInterface process(StackInterface pStack,
                                RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
  {

    if (mEngine == null)
      mEngine =
              new LightSheetFastFusionEngine(getContext(),
                                             mLightSheetMicroscope.getNumberOfLightSheets(),
                                             mLightSheetMicroscope.getNumberOfDetectionArms());

    // info("received stack for processing: %s", pStack);

    if (isPassThrough(pStack))
    {
      // info("pass-through mode on, passing stack untouched: %s",
      // pStack);
      return pStack;
    }

    if (mEngine.isDownscale())
    {
      double lVoxelDimX = pStack.getMetaData().getVoxelDimX();
      double lVoxelDimY = pStack.getMetaData().getVoxelDimY();

      pStack.getMetaData().setVoxelDimX(2 * lVoxelDimX);
      pStack.getMetaData().setVoxelDimY(2 * lVoxelDimY);
    }

    mEngine.passStack(true, pStack);

    // if (mEngine.isReady())
    {
      ElapsedTime.measureForceOutput("FastFuseTaskExecution", () -> {
        int lNumberOfTasksExecuted = mEngine.executeAllTasks();
        info("executed %d fusion tasks", lNumberOfTasksExecuted);
      });
    }

    if (pStack.getMetaData()
              .hasEntry(MetaDataFusion.RequestPerCameraFusion))
    {
      int lNumberOfDetectionArms =
                                 mLightSheetMicroscope.getNumberOfDetectionArms();
      for (int c = 0; c < lNumberOfDetectionArms; c++)
      {
        String lKey = "C" + c;
        ClearCLImage lImage = mEngine.getImage(lKey);
        if (lImage != null)
          mFusedStackQueue.add(Triple.of(c,
                                         mEngine.getFusedMetaData()
                                                .clone(),
                                         lImage));
      }

      Triple<Integer, StackMetaData, ClearCLImage> lImageFromQueue =
                                                                   mFusedStackQueue.poll();

      if (lImageFromQueue != null)
      {
        StackInterface lStack =
                              copyFusedStack(pStackRecycler,
                                             lImageFromQueue.getRight(),
                                             lImageFromQueue.getMiddle(),
                                             "C" + lImageFromQueue.getLeft());
        lStack.getMetaData().addEntry(MetaDataView.Camera,
                                      lImageFromQueue.getLeft());
        return lStack;
      }

    }
    else if (mEngine.isDone())
    {
      ClearCLImage lFusedImage = mEngine.getImage("fused");

      return copyFusedStack(pStackRecycler,
                            lFusedImage,
                            mEngine.getFusedMetaData(),
                            null);
    }

    return null;
  }

  protected StackInterface copyFusedStack(RecyclerInterface<StackInterface, StackRequest> pStackRecycler,
                                          ClearCLImage lFusedImage,
                                          StackMetaData pStackMetaData,
                                          String pChannel)
  {
    mFusedStack =
                pStackRecycler.getOrWait(1000,
                                         TimeUnit.SECONDS,
                                         StackRequest.build(lFusedImage.getDimensions()));

    mFusedStack.setMetaData(pStackMetaData);
    mFusedStack.getMetaData().addEntry(MetaDataFusion.Fused, true);
    if (pChannel != null)
      mFusedStack.getMetaData().addEntry(MetaDataChannel.Channel,
                                         pChannel);
    mFusedStack.getMetaData().removeAllEntries(MetaDataView.class);
    mFusedStack.getMetaData()
               .removeAllEntries(MetaDataViewFlags.class);
    mFusedStack.getMetaData().removeEntry(MetaDataOrdinals.Index);

    info("Resulting fused stack metadata:"
         + mFusedStack.getMetaData());

    lFusedImage.writeTo(mFusedStack.getContiguousMemory(), true);

    mEngine.reset(false);

    return mFusedStack;
  }

  private boolean isPassThrough(StackInterface pStack)
  {
    AcquisitionType lAcquisitionType =
                                     pStack.getMetaData()
                                           .getValue(MetaDataAcquisitionType.AcquisitionType);

    if (lAcquisitionType != AcquisitionType.TimeLapse)
      return true;

    if (pStack.getMetaData()
              .hasEntry(MetaDataFusion.RequestFullFusion))
      return false;

    if (pStack.getMetaData()
              .hasEntry(MetaDataFusion.RequestPerCameraFusion))
      return false;

    return true;
  }

}
