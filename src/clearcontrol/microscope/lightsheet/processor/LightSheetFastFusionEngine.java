package clearcontrol.microscope.lightsheet.processor;

import clearcl.ClearCLContext;
import clearcl.enums.ImageChannelDataType;
import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.microscope.lightsheet.stacks.MetaDataView;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.metadata.StackMetaData;
import fastfuse.FastFusionEngine;
import fastfuse.FastFusionEngineInterface;
import fastfuse.registration.AffineMatrix;
import fastfuse.tasks.FlipTask;
import fastfuse.tasks.IdentityTask;
import fastfuse.tasks.RegistrationTask;
import fastfuse.tasks.TenengradFusionTask;

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

  private volatile boolean mRegistration =
                                         MachineConfiguration.get()
                                                             .getBooleanProperty("fastfuse.register",
                                                                                 false);

  private RegistrationTask mRegisteredFusionTask;

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
        if (isRegistration())
        {
          mRegisteredFusionTask = new RegistrationTask("C0L0",
                                                       "C1L0",
                                                       "C1L0",
                                                       "C1L0reg");
          mRegisteredFusionTask.setZeroTransformMatrix(AffineMatrix.scaling(-1,
                                                                            1,
                                                                            1));

          addTask(mRegisteredFusionTask);
          addTask(new TenengradFusionTask("C0L0",
                                          "C1L0reg",
                                          "fused",
                                          ImageChannelDataType.UnsignedInt16));
        }
        else
        {
          addTask(FlipTask.flipX("C1", "C1flipped"));

          addTask(new TenengradFusionTask("C0L0",
                                          "C1flipped",
                                          "fused",
                                          ImageChannelDataType.UnsignedInt16));

        }

      }
    }
    else if (pNumberOfLightSheets == 2)
    {
      if (pNumberOfDetectionArms == 1)
      {
        addTask(new TenengradFusionTask("C0L0",
                                        "C0L1",
                                        "fused",
                                        ImageChannelDataType.UnsignedInt16));

      }
      else if (pNumberOfDetectionArms == 2)
      {

        if (isRegistration())
        {
          addTask(new TenengradFusionTask("C0L0",
                                          "C0L1",
                                          "C0",
                                          ImageChannelDataType.Float));
          addTask(new TenengradFusionTask("C1L0",
                                          "C1L1",
                                          "C1",
                                          ImageChannelDataType.Float));

          mRegisteredFusionTask = new RegistrationTask("C0",
                                                       "C1",
                                                       "C1",
                                                       "C1reg");
          mRegisteredFusionTask.setZeroTransformMatrix(AffineMatrix.scaling(-1,
                                                                            1,
                                                                            1));

          addTask(mRegisteredFusionTask);
          addTask(new TenengradFusionTask("C0",
                                          "C1reg",
                                          "fused",
                                          ImageChannelDataType.UnsignedInt16));
        }
        else
        {
          addTask(new TenengradFusionTask("C0L0",
                                          "C0L1",
                                          "C0",
                                          ImageChannelDataType.UnsignedInt16));
          addTask(new TenengradFusionTask("C1L0",
                                          "C1L1",
                                          "C1",
                                          ImageChannelDataType.UnsignedInt16));

          addTask(FlipTask.flipX("C1", "C1flipped"));

          addTask(new TenengradFusionTask("C0",
                                          "C1flipped",
                                          "fused",
                                          ImageChannelDataType.UnsignedInt16));
        }
      }
    }
    else if (pNumberOfLightSheets == 4)
    {
      if (pNumberOfDetectionArms == 1)
      {
        addTask(new TenengradFusionTask("C0L0",
                                        "C0L1",
                                        "C0L2",
                                        "C0L3",
                                        "fused",
                                        ImageChannelDataType.UnsignedInt16));
      }
      else if (pNumberOfDetectionArms == 2)
      {

        if (isRegistration())
        {
          addTask(new TenengradFusionTask("C0L0",
                                          "C0L1",
                                          "C0L2",
                                          "C0L3",
                                          "C0",
                                          ImageChannelDataType.Float));

          addTask(new TenengradFusionTask("C1L0",
                                          "C1L1",
                                          "C1L2",
                                          "C1L3",
                                          "C1",
                                          ImageChannelDataType.Float));

          mRegisteredFusionTask = new RegistrationTask("C0",
                                                       "C1",
                                                       "C1",
                                                       "C1reg");
          mRegisteredFusionTask.setZeroTransformMatrix(AffineMatrix.scaling(-1,
                                                                            1,
                                                                            1));

          addTask(mRegisteredFusionTask);
          addTask(new TenengradFusionTask("C0",
                                          "C1reg",
                                          "fused",
                                          ImageChannelDataType.UnsignedInt16));
        }
        else
        {
          addTask(new TenengradFusionTask("C0L0",
                                          "C0L1",
                                          "C0L2",
                                          "C0L3",
                                          "C0",
                                          ImageChannelDataType.UnsignedInt16));

          addTask(new TenengradFusionTask("C1L0",
                                          "C1L1",
                                          "C1L2",
                                          "C1L3",
                                          "C1",
                                          ImageChannelDataType.UnsignedInt16));

          addTask(FlipTask.flipX("C1", "C1flipped"));

          addTask(new TenengradFusionTask("C0",
                                          "C1flipped",
                                          "fused",
                                          ImageChannelDataType.UnsignedInt16));
        }

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

      if (mRegisteredFusionTask != null)
      {
        float lZAspectRatio =
                            (float) (lStackMetaData.getVoxelDimZ()
                                     / lStackMetaData.getVoxelDimX());
        mRegisteredFusionTask.setScaleZ(lZAspectRatio);
      }

      String lKey = MetaDataView.getCxLyString(lStackMetaData);

      Runnable lRunnable = () -> {
        passImage(lKey,
                  pStack.getContiguousMemory(),
                  ImageChannelDataType.UnsignedInt16,
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
    // System.out.println("passed:" + lMetaData);
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

  /**
   * Is registration turned on?
   * 
   * @return true if registration is turned on
   */
  public boolean isRegistration()
  {
    return mRegistration;
  }

  public void setRegistration(boolean pRegistration)
  {
    mRegistration = pRegistration;
  }

}
