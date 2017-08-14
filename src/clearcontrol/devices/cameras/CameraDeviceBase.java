package clearcontrol.devices.cameras;

import java.util.concurrent.atomic.AtomicBoolean;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.devices.sim.StackCameraDeviceSimulator;

/**
 * Base class providing common fields and methods for all camera devices.
 *
 * @author royer
 */
public abstract class CameraDeviceBase extends VirtualDevice
                                       implements
                                       CameraDeviceInterface,
                                       LoggingFeature
{

  protected final Variable<Long> mMaxWidthVariable,
      mMaxHeightVariable;

  protected final Variable<Double> mPixelSizeInMicrometersVariable,
      mLineReadOutTimeInMicrosecondsVariable;

  protected final Variable<Long> mBytesPerPixelVariable;

  protected final Variable<Long> mCurrentIndexVariable;

  private final Variable<Boolean> mTriggerVariable;

  private final AtomicBoolean mReOpenDeviceNeeded =
                                                  new AtomicBoolean(false);

  protected final Variable<Boolean> mIsAcquiring;

  /**
   * Instanciates a camera device with given name
   * 
   * @param pDeviceName
   *          camera name
   * @param pTriggerVariable
   *          trigger variable
   */
  public CameraDeviceBase(final String pDeviceName,
                          Variable<Boolean> pTriggerVariable)
  {
    super(pDeviceName);

    mTriggerVariable = pTriggerVariable;

    mCurrentIndexVariable = new Variable<Long>("CurrentIndex", 0L);

    mMaxWidthVariable = new Variable<Long>("FrameMaxWidth", 2048L);

    mMaxHeightVariable = new Variable<Long>("FrameMaxHeight", 2048L);

    mLineReadOutTimeInMicrosecondsVariable =
                                           new Variable<Double>("LineReadOutTimeInMicroseconds",
                                                                1.0);

    mPixelSizeInMicrometersVariable =
                                    new Variable<Double>("PixelSizeInMicrometers",
                                                         1.0);

    mBytesPerPixelVariable = new Variable<Long>("FrameBytesPerPixel",
                                                2L);

    mIsAcquiring = new Variable<Boolean>("IsAquiring", false);

    if (pTriggerVariable == null)
    {
      severe("cameras",
             "Cannot instantiate properly: "
                        + StackCameraDeviceSimulator.class.getSimpleName()
                        + " because trigger variable is null!");
      return;
    }
  }

  @Override
  public void setExposureInSeconds(double pExposureInSeconds)
  {
    getExposureInSecondsVariable().set(pExposureInSeconds);
  }

  @Override
  public double getExposureInSeconds()
  {
    return getExposureInSecondsVariable().get().doubleValue();
  }

  @Override
  public void trigger()
  {
    getTriggerVariable().setEdge(false, true);
  }

  @Override
  public boolean isReOpenDeviceNeeded()
  {
    return mReOpenDeviceNeeded.get();
  }

  @Override
  public void requestReOpen()
  {
    mReOpenDeviceNeeded.set(true);
  }

  @Override
  public void clearReOpen()
  {
    mReOpenDeviceNeeded.set(false);
  }

  @Override
  public abstract void reopen();

  @Override
  public Variable<Long> getMaxWidthVariable()
  {
    return mMaxWidthVariable;
  }

  @Override
  public Variable<Long> getMaxHeightVariable()
  {
    return mMaxHeightVariable;
  }

  @Override
  public Variable<Long> getBytesPerPixelVariable()
  {
    return mBytesPerPixelVariable;
  }

  @Override
  public Variable<Long> getCurrentIndexVariable()
  {
    return mCurrentIndexVariable;
  }

  @Override
  public Variable<Boolean> getIsAcquiringVariable()
  {
    return mIsAcquiring;
  }

  @Override
  public Variable<Double> getPixelSizeInMicrometersVariable()
  {
    return mPixelSizeInMicrometersVariable;
  }

  @Override
  public Variable<Double> getLineReadOutTimeInMicrosecondsVariable()
  {
    return mLineReadOutTimeInMicrosecondsVariable;
  }

  @Override
  public Variable<Boolean> getTriggerVariable()
  {
    return mTriggerVariable;
  }

}
