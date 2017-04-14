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

  LightSheetFastFusionEngine mEngine;

  /**
   * Instanciates a lightsheet stack processor
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

    mEngine = new LightSheetFastFusionEngine(pContext,
                                             pLightSheetMicroscope);

  }

  @Override
  public StackInterface process(StackInterface pStack,
                                RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
  {
    // info("received stack for processing: %s", pStack);

    if (isPassThrough(pStack))
    {
      // info("pass-through mode on, passing stack untouched: %s",
      // pStack);
      return pStack;
    }

    mEngine.passStack(true, pStack);

    if (mEngine.isReady())
    {
      int lNumberOfTasksExecuted = mEngine.executeAllTasks();
      /*System.out.println("lNumberOfTasksExecuted="
                         + lNumberOfTasksExecuted);/**/

    }

    if (mEngine.isDone())
    {
      ClearCLImage lFusedImage = mEngine.getImage("fused");

      StackInterface lFusedStack =
                                 pStackRecycler.getOrWait(100,
                                                          TimeUnit.SECONDS,
                                                          StackRequest.build(lFusedImage.getDimensions()));

      lFusedStack.setMetaData(mEngine.getFusedMetaData());

      System.out.println("fused:" + lFusedStack.getMetaData());

      lFusedImage.writeTo(lFusedStack.getContiguousMemory(), true);

      mEngine.reset(false);

      return lFusedStack;
    }

    return null;
  }

  private boolean isPassThrough(StackInterface pStack)
  {
    AcquisitionType lAcquisitionType =
                                     (AcquisitionType) pStack.getMetaData()
                                                             .getValue(MetaDataAcquisitionType.AcquisitionType);
    return lAcquisitionType == AcquisitionType.Interactive;
  }

}
