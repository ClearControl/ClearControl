package clearcontrol.devices.cameras;

import java.util.concurrent.Future;

import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * Base class providing common fields and methods for all stack camera devices.
 *
 * @author royer
 * @param <Q>
 *          queue type
 */
public abstract class StackCameraDeviceBase<Q extends StackCameraQueue<Q>>
                                           extends CameraDeviceBase
                                           implements
                                           StackCameraDeviceInterface<Q>,
                                           LoggingFeature

{
  protected Variable<Boolean> mStackMode;

  protected RecyclerInterface<StackInterface, StackRequest> mRecycler;

  protected Variable<StackInterface> mStackVariable;

  protected Q mTemplateQueue;

  /**
   * Instantiates a stack camera device with a given name
   * 
   * @param pDeviceName
   *          device name
   * @param pTriggerVariable
   *          trigger variable
   * @param pTemplateQueue
   *          template queue
   */
  @SuppressWarnings("unchecked")
  public StackCameraDeviceBase(String pDeviceName,
                               Variable<Boolean> pTriggerVariable,
                               Q pTemplateQueue)
  {
    super(pDeviceName, pTriggerVariable);

    mStackMode = new Variable<Boolean>("StackMode", true);

    mStackVariable = new Variable<>("StackReference");

    @SuppressWarnings("rawtypes")
    final VariableSetListener lVariableListener = (o, n) -> {
      if (o != n)
        notifyListeners(this);
    };

    mTemplateQueue = pTemplateQueue;

    mTemplateQueue.getStackWidthVariable()
                  .addSetListener(lVariableListener);
    mTemplateQueue.getStackHeightVariable()
                  .addSetListener(lVariableListener);
    mTemplateQueue.getStackDepthVariable()
                  .addSetListener(lVariableListener);
    mTemplateQueue.getExposureInSecondsVariable()
                  .addSetListener(lVariableListener);

  }

  @Override
  public long getCurrentStackIndex()
  {
    return getCurrentIndexVariable().get();
  }

  @Override
  public void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler)
  {
    mRecycler = pRecycler;
  }

  @Override
  public RecyclerInterface<StackInterface, StackRequest> getStackRecycler()
  {
    return mRecycler;
  }

  @Override
  public Variable<Boolean> getStackModeVariable()
  {
    return mStackMode;
  }

  @Override
  public Variable<Number> getExposureInSecondsVariable()
  {
    return mTemplateQueue.getExposureInSecondsVariable();
  }

  @Override
  public Variable<Long> getStackWidthVariable()
  {
    return mTemplateQueue.getStackWidthVariable();
  }

  @Override
  public Variable<Long> getStackHeightVariable()
  {
    return mTemplateQueue.getStackHeightVariable();
  }

  @Override
  public Variable<Long> getStackDepthVariable()
  {
    return mTemplateQueue.getStackDepthVariable();
  }

  @Override
  public Variable<StackInterface> getStackVariable()
  {
    return mStackVariable;
  }

  @Override
  public void trigger()
  {
    getTriggerVariable().setEdge(false, true);
  }

  @Override
  abstract public Q requestQueue();

  @Override
  public Future<Boolean> playQueue(Q pQueue)
  {
    if (getStackRecycler() == null)
    {
      severe("No recycler defined for: " + this);
      return null;
    }
    return null;
  }

}