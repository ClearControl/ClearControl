package clearcontrol.microscope.lightsheet.processor;

import java.util.List;

import clearcl.ClearCLContext;
import clearcl.enums.ImageChannelDataType;
import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.microscope.lightsheet.stacks.MetaDataView;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.metadata.StackMetaData;
import fastfuse.FastFusionEngine;
import fastfuse.FastFusionEngineInterface;
import fastfuse.FastFusionMemoryPool;
import fastfuse.registration.AffineMatrix;
import fastfuse.tasks.CompositeTasks;
import fastfuse.tasks.DownsampleXYbyHalfTask;
import fastfuse.tasks.DownsampleXYbyHalfTask.Type;
import fastfuse.tasks.FlipTask;
import fastfuse.tasks.GaussianBlurTask;
import fastfuse.tasks.IdentityTask;
import fastfuse.tasks.MemoryReleaseTask;
import fastfuse.tasks.RegistrationTask;
import fastfuse.tasks.TaskInterface;
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

  private volatile boolean mDownscale =
                                      MachineConfiguration.get()
                                                          .getBooleanProperty("fastfuse.downscale",
                                                                              true);

  private volatile double mMemRatio =
                                    MachineConfiguration.get()
                                                        .getDoubleProperty("fastfuse.memratio",
                                                                           0.8);

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

    // setting up pool with max pool size:
    long lMaxMemoryInBytes =
                           (long) (mMemRatio
                                   * pContext.getDevice()
                                             .getMaxMemoryAllocationSizeInBytes());
    FastFusionMemoryPool.getInstance(pContext, lMaxMemoryInBytes);

    int[] lKernelSizesRegistration = new int[]
    { 3, 3, 3 };
    float[] lKernelSigmasRegistration = new float[]
    { 0.5f, 0.5f, 0.5f };

    float[] lKernelSigmasFusion = new float[]
    { 15, 15, 5 };

    float[] lKernelSigmasBackground = new float[]
    { 30, 30, 10 };

    if (pNumberOfLightSheets == 1)
    {
      if (pNumberOfDetectionArms == 1)
      {
        setupOneLightsheetOneDetectionArm();
      }
      else if (pNumberOfDetectionArms == 2)
      {
        setupOneLightsheetTwoDetectionArm(lKernelSizesRegistration,
                                          lKernelSigmasRegistration);
      }
    }
    else if (pNumberOfLightSheets == 2)
    {
      if (pNumberOfDetectionArms == 1)
      {
        setupTwoLightSheetsOneDetectionArm();

      }
      else if (pNumberOfDetectionArms == 2)
      {

        setupTwoLightsheetsTwoDetectionArms(lKernelSizesRegistration,
                                            lKernelSigmasRegistration);
      }
    }
    else if (pNumberOfLightSheets == 4)
    {
      if (pNumberOfDetectionArms == 1)
      {
        setupFourLightsheetsOneDetectionArm();
      }
      else if (pNumberOfDetectionArms == 2)
      {

        setupFourLightsheetsTwoDetectionArms(lKernelSizesRegistration,
                                             lKernelSigmasRegistration,
                                             lKernelSigmasFusion,
                                             lKernelSigmasBackground);
      }
    }

  }

  protected void setupFourLightsheetsTwoDetectionArms(int[] pKernelSizesRegistration,
                                                      float[] pKernelSigmasRegistration,
                                                      float[] pKernelSigmasFusion,
                                                      float[] pKernelSigmasBackground)
  {
    if (isDownscale())
      addTasks(DownsampleXYbyHalfTask.applyAndReleaseInputs(Type.Median,
                                                            "d",
                                                            "C0L0",
                                                            "C0L1",
                                                            "C0L2",
                                                            "C0L3",
                                                            "C1L0",
                                                            "C1L1",
                                                            "C1L2",
                                                            "C1L3"));
    else
      addTasks(IdentityTask.withSuffix("d",
                                       "C0L0",
                                       "C0L1",
                                       "C0L2",
                                       "C0L3",
                                       "C1L0",
                                       "C1L1",
                                       "C1L2",
                                       "C1L3"));

    ImageChannelDataType lInitialFusionDataType =
                                                isRegistration() ? ImageChannelDataType.Float
                                                                 : ImageChannelDataType.UnsignedInt16;

    addTasks(CompositeTasks.fuseWithSmoothWeights("C0",
                                                  lInitialFusionDataType,
                                                  pKernelSigmasFusion,
                                                  true,
                                                  "C0L0d",
                                                  "C0L1d",
                                                  "C0L2d",
                                                  "C0L3d"));

    addTasks(CompositeTasks.fuseWithSmoothWeights("C1",
                                                  lInitialFusionDataType,
                                                  pKernelSigmasFusion,
                                                  true,
                                                  "C1L0d",
                                                  "C1L1d",
                                                  "C1L2d",
                                                  "C1L3d"));

    if (isRegistration())
    {
      List<TaskInterface> lRegistrationTaskList =
                                                CompositeTasks.registerWithBlurPreprocessing("C0",
                                                                                             "C1",
                                                                                             "C1adjusted",
                                                                                             pKernelSigmasRegistration,
                                                                                             pKernelSizesRegistration,
                                                                                             AffineMatrix.scaling(-1,
                                                                                                                  1,
                                                                                                                  1),
                                                                                             true);
      addTasks(lRegistrationTaskList);
      // extract registration task from list
      for (TaskInterface lTask : lRegistrationTaskList)
        if (lTask instanceof RegistrationTask)
        {
          mRegisteredFusionTask = (RegistrationTask) lTask;
          break;
        }
    }
    else
    {
      addTask(FlipTask.flipX("C1", "C1adjusted"));
      addTask(new MemoryReleaseTask("C1adjusted", "C1"));
    }

    // addTasks(CompositeTasks.fuseWithSmoothWeights("fused",
    // ImageChannelDataType.UnsignedInt16,
    // pKernelSigmasFusion,
    // true,
    // "C0",
    // "C1adjusted"));

    addTasks(CompositeTasks.fuseWithSmoothWeights("fused-preliminary",
                                                  ImageChannelDataType.Float,
                                                  pKernelSigmasFusion,
                                                  true,
                                                  "C0",
                                                  "C1adjusted"));

    addTasks(CompositeTasks.subtractBlurredCopyFromFloatImage("fused-preliminary",
                                                              "fused",
                                                              pKernelSigmasBackground,
                                                              true,
                                                              ImageChannelDataType.UnsignedInt16));
  }

  protected void setupFourLightsheetsOneDetectionArm()
  {
    if (isDownscale())
    {
      addTask(new DownsampleXYbyHalfTask("C0L0", "C0L0d"));
      addTask(new DownsampleXYbyHalfTask("C0L1", "C0L1d"));
      addTask(new DownsampleXYbyHalfTask("C0L2", "C0L2d"));
      addTask(new DownsampleXYbyHalfTask("C0L3", "C0L3d"));
    }
    else
    {
      addTask(new IdentityTask("C0L0", "C0L0d"));
      addTask(new IdentityTask("C0L1", "C0L1d"));
      addTask(new IdentityTask("C0L2", "C0L2d"));
      addTask(new IdentityTask("C0L3", "C0L3d"));
    }

    addTask(new TenengradFusionTask("C0L0d",
                                    "C0L1d",
                                    "C0L2d",
                                    "C0L3d",
                                    "fused",
                                    ImageChannelDataType.UnsignedInt16));
  }

  protected void setupTwoLightsheetsTwoDetectionArms(int[] lKernelSizes,
                                                     float[] lKernelSigmas)
  {
    if (isRegistration())
    {
      if (isDownscale())
      {
        addTask(new DownsampleXYbyHalfTask("C0L0", "C0L0d"));
        addTask(new DownsampleXYbyHalfTask("C0L1", "C0L1d"));
        addTask(new DownsampleXYbyHalfTask("C1L0", "C1L0d"));
        addTask(new DownsampleXYbyHalfTask("C1L1", "C1L1d"));
      }
      else
      {
        addTask(new IdentityTask("C0L0", "C0L0d"));
        addTask(new IdentityTask("C0L1", "C0L1d"));
        addTask(new IdentityTask("C1L0", "C1L0d"));
        addTask(new IdentityTask("C1L1", "C1L1d"));
      }

      addTask(new TenengradFusionTask("C0L0d",
                                      "C0L1d",
                                      "C0",
                                      ImageChannelDataType.Float));
      addTask(new TenengradFusionTask("C1L0d",
                                      "C1L1d",
                                      "C1",
                                      ImageChannelDataType.Float));

      addTask(new GaussianBlurTask("C0",
                                   "C0blur",
                                   lKernelSigmas,
                                   lKernelSizes));
      addTask(new GaussianBlurTask("C1",
                                   "C1blur",
                                   lKernelSigmas,
                                   lKernelSizes));

      mRegisteredFusionTask =
                            new RegistrationTask("C0blur",
                                                 "C1blur",
                                                 "C0",
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

  protected void setupTwoLightSheetsOneDetectionArm()
  {
    if (isDownscale())
    {
      addTask(new DownsampleXYbyHalfTask("C0L0", "C0L0d"));
      addTask(new DownsampleXYbyHalfTask("C0L1", "C0L1d"));
    }
    else
    {
      addTask(new IdentityTask("C0L0", "C0L0d"));
      addTask(new IdentityTask("C0L1", "C0L1d"));
    }

    addTask(new TenengradFusionTask("C0L0d",
                                    "C0L1d",
                                    "fused",
                                    ImageChannelDataType.UnsignedInt16));
  }

  protected void setupOneLightsheetTwoDetectionArm(int[] lKernelSizes,
                                                   float[] lKernelSigmas)
  {
    if (isRegistration())
    {
      if (isDownscale())
      {
        addTask(new DownsampleXYbyHalfTask("C0L0", "C0L0d"));
        addTask(new DownsampleXYbyHalfTask("C1L0", "C1L0d"));
      }
      else
      {
        addTask(new IdentityTask("C0L0", "C0L0d"));
        addTask(new IdentityTask("C1L0", "C1L0d"));
      }

      addTask(new GaussianBlurTask("C0L0d",
                                   "C0L0blur",
                                   lKernelSigmas,
                                   lKernelSizes));
      addTask(new GaussianBlurTask("C1L0d",
                                   "C1L0blur",
                                   lKernelSigmas,
                                   lKernelSizes));

      mRegisteredFusionTask =
                            new RegistrationTask("C0L0blur",
                                                 "C1L0blur",
                                                 "C0L0d",
                                                 "C1L0d",
                                                 "C1L0reg");
      mRegisteredFusionTask.setZeroTransformMatrix(AffineMatrix.scaling(-1,
                                                                        1,
                                                                        1));

      addTask(mRegisteredFusionTask);
      addTask(new TenengradFusionTask("C0L0d",
                                      "C1L0reg",
                                      "fused",
                                      ImageChannelDataType.UnsignedInt16));
    }
    else
    {
      if (isDownscale())
      {
        addTask(new DownsampleXYbyHalfTask("C0L0", "C0L0d"));
        addTask(new DownsampleXYbyHalfTask("C1L0", "C1L0d"));
      }
      else
      {
        addTask(new IdentityTask("C0L0", "C0L0d"));
        addTask(new IdentityTask("C1L0", "C1L0d"));
      }

      addTask(FlipTask.flipX("C1L0d", "C1L0flipped"));

      addTask(new TenengradFusionTask("C0L0d",
                                      "C1flipped",
                                      "fused",
                                      ImageChannelDataType.UnsignedInt16));

    }
  }

  protected void setupOneLightsheetOneDetectionArm()
  {
    if (isDownscale())
    {
      addTask(new DownsampleXYbyHalfTask("C0L0", "fused"));
    }
    else
    {
      addTask(new IdentityTask("C0L0", "fused"));
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

  /**
   * Sets the registration flag
   * 
   * @param pRegistration
   *          registration flag
   */
  public void setRegistration(boolean pRegistration)
  {
    mRegistration = pRegistration;
  }

  /**
   * Returns true if downscale by a factor 2 along XY is active
   * 
   * @return true if downscale isactive
   */
  public boolean isDownscale()
  {
    return mDownscale;
  }

  /**
   * Sets whether to downscale by a factor 2 along XY
   * 
   * @param pDownscale
   *          downscale on or off
   */
  public void setDownscale(boolean pDownscale)
  {
    mDownscale = pDownscale;
  }

}
