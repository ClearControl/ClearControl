package clearcontrol.microscope.lightsheet.processor;

import java.util.concurrent.TimeUnit;

import clearcl.ClearCLContext;
import clearcl.ClearCLImage;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.AcquisitionType;
import clearcontrol.microscope.stacks.metadata.MetaDataAcquisitionType;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
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

    mEngine.passStack(true, pStack);

    // if (mEngine.isReady())
    {
      int lNumberOfTasksExecuted = mEngine.executeAllTasks();
      info("executed %d fusion tasks", lNumberOfTasksExecuted);/**/
    }

    if (mEngine.isDone())
    {
      ClearCLImage lFusedImage = mEngine.getImage("fused");

      mFusedStack =
                  pStackRecycler.getOrWait(1000,
                                           TimeUnit.SECONDS,
                                           StackRequest.build(lFusedImage.getDimensions()));

      mFusedStack.setMetaData(mEngine.getFusedMetaData());

      System.out.println("fused:" + mFusedStack.getMetaData());

      lFusedImage.writeTo(mFusedStack.getContiguousMemory(), true);

      mEngine.reset(false);

      return mFusedStack;
    }

    return null;
  }

  private boolean isPassThrough(StackInterface pStack)
  {
    AcquisitionType lAcquisitionType =
                                     pStack.getMetaData()
                                           .getValue(MetaDataAcquisitionType.AcquisitionType);
    return lAcquisitionType == AcquisitionType.Interactive;
  }

}
