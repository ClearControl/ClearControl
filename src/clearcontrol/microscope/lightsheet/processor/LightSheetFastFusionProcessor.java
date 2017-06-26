package clearcontrol.microscope.lightsheet.processor;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import clearcl.ClearCLContext;
import clearcl.ClearCLImage;
import clearcl.util.ElapsedTime;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface;
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

import org.apache.commons.lang3.tuple.Triple;

/**
 * Lightsheet fusion processor
 *
 * @author royer
 */
public class LightSheetFastFusionProcessor extends
                                           ClearCLStackProcessorBase
                                           implements
                                           StackProcessorInterface,
                                           VisualConsoleInterface,
                                           LoggingInterface
{
  private final LightSheetMicroscope mLightSheetMicroscope;
  private LightSheetFastFusionEngine mEngine;

  ConcurrentLinkedQueue<Triple<Integer, StackMetaData, ClearCLImage>> mFusedStackQueue =
                                                                                       new ConcurrentLinkedQueue<>();

  private volatile StackInterface mFusedStack;

  private final Variable<Integer> mNumberOfRestartsVariable =
                                                            new Variable<Integer>("NumberOfRestarts",
                                                                                  5);

  private final Variable<Integer> mMaxNumberOfEvaluationsVariable =
                                                                  new Variable<Integer>("MaxNumberOfEvaluations",
                                                                                        200);

  private final Variable<Double> mTranslationSearchRadiusVariable =
                                                                  new Variable<Double>("TranslationSearchRadius",
                                                                                       15.0);
  private final Variable<Double> mRotationSearchRadiusVariable =
                                                               new Variable<Double>("RotationSearchRadius",
                                                                                    3.0);

  private final Variable<Double> mSmoothingConstantVariable =
                                                            new Variable<Double>("SmoothingConstant",
                                                                                 0.05);

  private final Variable<Boolean> mTransformLockSwitchVariable =
                                                               new Variable<Boolean>("TransformLockSwitch",
                                                                                     true);

  private final Variable<Integer> mTransformLockThresholdVariable =
                                                                  new Variable<Integer>("TransformLockThreshold",
                                                                                        20);

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
                                             (VisualConsoleInterface) this,
                                             mLightSheetMicroscope.getNumberOfLightSheets(),
                                             mLightSheetMicroscope.getNumberOfDetectionArms());

    info("Received stack for processing: %s", pStack);

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

    if (mEngine.getRegistrationTask() != null)
    {
      try
      {

        if (getTransformLockSwitchVariable().get().booleanValue()
            && pStack.getMetaData()
                     .getValue(MetaDataOrdinals.TimePoint) > getTransformLockThresholdVariable().get()
                                                                                                .intValue())
        {
          getSmoothingConstantVariable().set(0.02);
          getTransformLockSwitchVariable().set(false);
        }

        mEngine.getRegistrationTask()
               .getParameters()
               .setNumberOfRestarts(getNumberOfRestartsVariable().get()
                                                                 .intValue());

        mEngine.getRegistrationTask()
               .getParameters()
               .setTranslationSearchRadius(getTranslationSearchRadiusVariable().get()
                                                                               .doubleValue());

        mEngine.getRegistrationTask()
               .getParameters()
               .setRotationSearchRadius(getRotationSearchRadiusVariable().get()
                                                                         .doubleValue());

        mEngine.getRegistrationTask()
               .getParameters()
               .setMaxNumberOfEvaluations((int) getMaxNumberOfEvaluationsVariable().get()
                                                                                   .intValue());

        mEngine.getRegistrationTask()
               .setSmoothingConstant(getSmoothingConstantVariable().get()
                                                                   .doubleValue());

      }
      catch (Throwable e)
      {
        severe("Problem while setting fast fusion parameters: %s",
               e.toString());
      }
    }

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

  /**
   * Returns the variable holding the translation search radius.
   * 
   * @return translation search radius variable.
   */
  public Variable<Double> getTranslationSearchRadiusVariable()
  {
    return mTranslationSearchRadiusVariable;
  }

  /**
   * Returns the variable holding the rotation search radius
   * 
   * @return rotation search radius
   */
  public Variable<Double> getRotationSearchRadiusVariable()
  {
    return mRotationSearchRadiusVariable;
  }

  /**
   * Returns the variable holding the number of optimization restarts
   * 
   * @return number of optimization restarts variable
   */
  public Variable<Integer> getNumberOfRestartsVariable()
  {
    return mNumberOfRestartsVariable;
  }

  /**
   * Returns the max number of evaluations variable
   * 
   * @return max number of evaluations variable
   */
  public Variable<Integer> getMaxNumberOfEvaluationsVariable()
  {
    return mMaxNumberOfEvaluationsVariable;
  }

  /**
   * Returns the variable holding the smoothing constant
   * 
   * @return smoothing constant variable
   */
  public Variable<Double> getSmoothingConstantVariable()
  {
    return mSmoothingConstantVariable;
  }

  /**
   * Returns the switch that decides whether to lock the transformation after a
   * certain number of time points has elapsed
   * 
   * @return Transform lock switch variable
   */
  public Variable<Boolean> getTransformLockSwitchVariable()
  {
    return mTransformLockSwitchVariable;
  }

  /**
   * Returns the variable holding the number of timepoints until the
   * transformation should be 'locked' with more stringent temporal filtering
   * 
   * @return transform lock timer variable
   */
  public Variable<Integer> getTransformLockThresholdVariable()
  {
    return mTransformLockThresholdVariable;
  }

}
