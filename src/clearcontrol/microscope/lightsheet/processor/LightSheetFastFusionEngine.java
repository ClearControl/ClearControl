package clearcontrol.microscope.lightsheet.processor;

import clearcl.ClearCLContext;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.processor.fusion.FastFusionEngine;
import clearcontrol.microscope.lightsheet.processor.fusion.FastFusionEngineInterface;
import clearcontrol.microscope.lightsheet.processor.fusion.tasks.AverageTask;
import clearcontrol.microscope.stacks.metadata.MetaDataView;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.metadata.StackMetaData;

/**
 * Lightsheet fast fusion engine
 *
 * @author royer
 */
public class LightSheetFastFusionEngine extends FastFusionEngine
                                        implements
                                        FastFusionEngineInterface
{

  private StackMetaData mFusedStackMetaData = new StackMetaData();

  /**
   * Instanciates a lightsheet fast fusion engine
   * 
   * @param pContext
   *          ClearCL context
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   */
  public LightSheetFastFusionEngine(ClearCLContext pContext,
                                    LightSheetMicroscope pLightSheetMicroscope)
  {
    super(pContext);

    // int lNumberOfLightSheets =
    // pLightSheetMicroscope.getNumberOfLightSheets();
    // int lNumberOfDetectionArms =
    // pLightSheetMicroscope.getNumberOfDetectionArms();

    // TODO: automatic configuration for different number of lightsheets and
    // detection arms:

    addTask(new AverageTask("C0L0", "C0L1", "fused"));

  }

  /**
   * Returns the fused metadata object
   * 
   * @return fused metadata
   */
  public StackMetaData getFusedMetaData()
  {
    return mFusedStackMetaData.clone();
  }

  @Override
  public void reset(boolean pCloseImages)
  {
    super.reset(pCloseImages);
    mFusedStackMetaData.clear();
  }

  /**
   * Returns whether this fusion engine has received all the required stacks
   * 
   * @return true -> ready to fuse
   */
  public boolean isReady()
  {
    return getAvailableImagesKeys().contains("C0L0")
           && getAvailableImagesKeys().contains("C0L1");
  }

  /**
   * Passes a stack to this Fast Fusion engine.
   * 
   * @param pWaitToFinish
   *          true ->
   * @param pStack
   *          stack
   */
  public void passStack(boolean pWaitToFinish, StackInterface pStack)
  {
    try
    {
      StackMetaData lStackMetaData = pStack.getMetaData();

      Integer lCameraIndex =
                           lStackMetaData.getValue(MetaDataView.Camera);
      Integer lLightSheetIndex =
                               lStackMetaData.getValue(MetaDataView.LightSheet);

      if (lCameraIndex == null || lLightSheetIndex == null)
      {
        pStack.release();
        return;
      }

      String lKey = getKey(lCameraIndex, lLightSheetIndex);

      Runnable lRunnable = () -> {
        passImage(lKey,
                  pStack.getContiguousMemory(),
                  pStack.getDimensions());

        fuseMetaData(pStack);

        pStack.release();
      };

      if (pWaitToFinish)
        lRunnable.run();
      else
        executeAsynchronously(lRunnable);
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

  }

  private void fuseMetaData(StackInterface pStack)
  {
    StackMetaData lMetaData = pStack.getMetaData();

    mFusedStackMetaData.addAll(lMetaData);
    System.out.println("passed:" + lMetaData);
  }

  protected String getKey(final int pCameraIndex,
                          final int pLightSheetIndex)
  {
    String lKey = String.format("C%dL%d",
                                pCameraIndex,
                                pLightSheetIndex);
    return lKey;
  }

  /**
   * Returns true if the fusion is done
   * 
   * @return true -> fusion done
   */
  public boolean isDone()
  {
    return isImageAvailable("fused");
  }

}
