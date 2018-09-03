package clearcontrol.core.device.task.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.device.task.PeriodicLoopTaskDevice;

import org.junit.Test;

/**
 * Periodic loop task device tests
 *
 * @author royer
 */
public class PeriodicLoopTaskDeviceTests
{

  private volatile long mCounter = 0;

  class TestLoopTaskDevice extends PeriodicLoopTaskDevice
  {
    public TestLoopTaskDevice()
    {
      super("TestDevice", 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean loop()
    {
      System.out.println("counter: " + mCounter);
      mCounter++;
      return true;
    }

  }

  /**
   * Basic test
   * 
   * @throws ExecutionException
   *           NA
   */
  @Test
  public void test() throws ExecutionException
  {
    TestLoopTaskDevice lTestLoopTaskDevice = new TestLoopTaskDevice();

    mCounter = 0;
    lTestLoopTaskDevice.getStartSignalVariable().set(true);
    System.out.println("Waiting to start");
    assertTrue(lTestLoopTaskDevice.waitForStarted(1,
                                                  TimeUnit.SECONDS));
    ThreadSleep.sleep(1, TimeUnit.SECONDS);
    lTestLoopTaskDevice.getStopSignalVariable().set(true);

    assertTrue(lTestLoopTaskDevice.waitForStopped(10,
                                                  TimeUnit.SECONDS));
    long lCounter = mCounter;
    System.out.println("lCounter=" + mCounter);

    ThreadSleep.sleep(200, TimeUnit.MILLISECONDS);

    assertTrue(lCounter >= mCounter);
  }

}
