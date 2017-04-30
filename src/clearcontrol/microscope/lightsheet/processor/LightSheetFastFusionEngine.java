package clearcontrol.microscope.lightsheet.processor;

import clearcl.ClearCLContext;
import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.microscope.stacks.metadata.MetaDataView;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.metadata.StackMetaData;
import fastfuse.FastFusionEngine;
import fastfuse.FastFusionEngineInterface;
import fastfuse.tasks.AverageTask;
import fastfuse.tasks.IdentityTask;

/**
 * Lightsheet fast fusion engine
 *
 * @author royer
 */
public class LightSheetFastFusionEngine extends FastFusionEngine
                                        implements
                                        FastFusionEngineInterface,
                                        AsynchronousExecutorServiceAccess
{

  private StackMetaData mFusedStackMetaData = new StackMetaData();

  /**
   * Instantiates a lightsheet fast fusion engine
   * 
   * @param pContext
   *          ClearCL context
   * @param pNumberOfLightSheets
   *          number of lightsheets
   * @param pNumberOfDetectionArms
   *          number of detection arms
   */
  public LightSheetFastFusionEngine(ClearCLContext pContext,
                                    int pNumberOfLightSheets,
                                    int pNumberOfDetectionArms)
  {
    super(pContext);

    if (pNumberOfLightSheets == 1)
    {
      if (pNumberOfDetectionArms == 1)
      {
        addTask(new IdentityTask("C0L0", "fused"));
      }
      else if (pNumberOfDetectionArms == 2)
      {
        addTask(new AverageTask("C0L0", "C1L0", "fused"));
      }
    }
    else if (pNumberOfLightSheets == 2)
    {
      if (pNumberOfDetectionArms == 1)
      {
        addTask(new AverageTask("C0L0", "C0L1", "fused"));
      }
      else if (pNumberOfDetectionArms == 2)
      {
        addTask(new AverageTask("C0L0", "C0L1", "C0"));
        addTask(new AverageTask("C1L0", "C1L1", "C1"));
        addTask(new AverageTask("C0", "C1", "fused"));
      }
    }
    else if (pNumberOfLightSheets == 4)
    {
      if (pNumberOfDetectionArms == 1)
      {
        addTask(new AverageTask("C0L0",
                                "C0L1",
                                "C0L2",
                                "C0L3",
                                "fused"));
      }
      else if (pNumberOfDetectionArms == 2)
      {
        addTask(new AverageTask("C0L0",
                                "C0L1",
                                "C0L2",
                                "C0L3",
                                "C0"));
        addTask(new AverageTask("C1L0",
                                "C1L1",
                                "C1L2",
                                "C1L3",
                                "C1"));
        addTask(new AverageTask("C0", "C1", "fused"));
      }
    }

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
  /*public boolean isReady()
  {
    return getAvailableImagesSlotKeys().contains("C0L0")
           && getAvailableImagesSlotKeys().contains("C0L1");
  }/**/

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

      String lKey = MetaDataView.getCxLyString(lStackMetaData);

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
