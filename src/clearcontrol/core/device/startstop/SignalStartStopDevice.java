package clearcontrol.core.device.startstop;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableEdgeListener;

/**
 * base class for signal startable and stoppable devices
 *
 * @author royer
 */
public abstract class SignalStartStopDevice extends VirtualDevice
                                            implements
                                            StartStopSignalVariablesInterface
{

  protected final Variable<Boolean> mStartSignal;
  protected final Variable<Boolean> mStopSignal;
  protected Runnable mStartRunnable = null;
  protected Runnable mStopRunnable = null;

  /**
   * Instanciates a signal startable and stoppable device
   * 
   * @param pDeviceName
   *          device name
   */
  public SignalStartStopDevice(final String pDeviceName)
  {
    super(pDeviceName);

    mStartSignal =
                 new Variable<Boolean>(pDeviceName + "Start", false);

    mStopSignal = new Variable<Boolean>(pDeviceName + "Stop", false);

    mStartSignal.addEdgeListener(new VariableEdgeListener<Boolean>()
    {
      @Override
      public void fire(final Boolean pCurrentBooleanValue)
      {
        if (mStartRunnable != null && pCurrentBooleanValue)
          mStartRunnable.run();
      }
    });

    mStopSignal.addEdgeListener(new VariableEdgeListener<Boolean>()
    {
      @Override
      public void fire(final Boolean pCurrentBooleanValue)
      {
        if (mStopRunnable != null && pCurrentBooleanValue)
          mStopRunnable.run();
      }
    });

  }

  @Override
  public Variable<Boolean> getStartSignalVariable()
  {
    return mStartSignal;
  }

  @Override
  public Variable<Boolean> getStopSignalVariable()
  {
    return mStopSignal;
  }

  /**
   * Sets the runnable to execute on start
   * 
   * @param pStartRunnable
   *          start runnable
   */
  public void setTaskOnStart(Runnable pStartRunnable)
  {
    mStartRunnable = pStartRunnable;
  }

  /**
   * Sets the runnable to execute on stop
   * 
   * @param pStopRunnable
   *          start runnable
   */
  public void setTaskOnStop(Runnable pStopRunnable)
  {
    mStopRunnable = pStopRunnable;
  }

}
