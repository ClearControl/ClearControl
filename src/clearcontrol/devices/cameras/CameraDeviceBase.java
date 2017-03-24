package clearcontrol.devices.cameras;

import java.util.concurrent.atomic.AtomicBoolean;

import clearcontrol.core.device.QueueableVirtualDevice;
import clearcontrol.core.variable.Variable;

/**
 * Base class providing common fields and methods for all camera devices.
 *
 * @author royer
 */
public abstract class CameraDeviceBase extends QueueableVirtualDevice
                                       implements
                                       CameraDeviceInterface
{

  protected Variable<Double> mExposureInMicrosecondsVariable,
      mPixelSizeinNanometersVariable,
      mLineReadOutTimeInMicrosecondsVariable;

  protected Variable<Boolean> mTriggerVariable;

  protected Variable<Integer> mChannelVariable;

  private AtomicBoolean mReOpenDeviceNeeded =
                                            new AtomicBoolean(false);

  protected Variable<Boolean> mIsAcquiring;

  /**
   * Instanciates a camera device with given name
   * 
   * @param pDeviceName
   *          camera name
   */
  public CameraDeviceBase(final String pDeviceName)
  {
    super(pDeviceName);
  }

  @Override
  public void setExposure(double pExposureInMicroseconds)
  {
    getExposureInMicrosecondsVariable().set(pExposureInMicroseconds);
  }

  @Override
  public double getExposure()
  {
    return getExposureInMicrosecondsVariable().get();
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
  public Variable<Integer> getChannelVariable()
  {
    return mChannelVariable;
  }

  @Override
  public Variable<Double> getExposureInMicrosecondsVariable()
  {
    return mExposureInMicrosecondsVariable;
  }

  @Override
  public Variable<Double> getPixelSizeInNanometersVariable()
  {
    return mPixelSizeinNanometersVariable;
  }

  @Override
  public Variable<Boolean> getIsAcquiringVariable()
  {
    return mIsAcquiring;
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
