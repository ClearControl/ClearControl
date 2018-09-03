package clearcontrol.microscope.adaptive.test;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.NameableWithChangeListener;
import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.state.AcquisitionStateInterface;

/**
 * Acquisition state for testing purposes
 *
 * @author royer
 */
public class TestState extends
                       NameableWithChangeListener<AcquisitionStateInterface<MicroscopeInterface<QueueInterface>, QueueInterface>>
                       implements
                       AcquisitionStateInterface<MicroscopeInterface<QueueInterface>, QueueInterface>
{

  /**
   * Instanciates a test acquisition state
   * 
   * @param pName
   *          state name
   */
  public TestState(String pName)
  {
    super(pName);
  }

  @Override
  public TestState duplicate(String pName)
  {
    return new TestState(pName);
  }

  @Override
  public void prepareAcquisition(long pTimeOut, TimeUnit pTimeUnit)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public QueueInterface getQueue()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BoundedVariable<Number> getExposureInSecondsVariable()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
