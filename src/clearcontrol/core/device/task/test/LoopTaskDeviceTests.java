package clearcontrol.core.device.task.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.device.task.LoopTaskDevice;

import org.junit.Test;

/**
 * Loop task device tests
 *
 * @author royer
 */
public class LoopTaskDeviceTests
{

  private volatile long mCounter = 0;

  class TestLoopTaskDevice extends LoopTaskDevice
  {
    public TestLoopTaskDevice()
    {
      super("TestDevice");
    }

    @Override
    public boolean loop()
    {
      System.out.println("counter: " + mCounter);
      mCounter++;
      ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
      return true;
    }

  }

  @Test
  public void test() throws ExecutionException
  {
    TestLoopTaskDevice lTestLoopTaskDevice = new TestLoopTaskDevice();

    mCounter = 0;
    lTestLoopTaskDevice.getStartSignalVariable().set(true);
    System.out.println("Waiting to start");
    assertTrue(lTestLoopTaskDevice.waitForStarted(1,
                                                  TimeUnit.SECONDS));
    ThreadUtils.sleep(1, TimeUnit.SECONDS);
    lTestLoopTaskDevice.getStopSignalVariable().set(true);

    assertTrue(lTestLoopTaskDevice.waitForStopped(10,
                                                  TimeUnit.SECONDS));
    long lCounter = mCounter;
    System.out.println("lCounter=" + mCounter);

    ThreadUtils.sleep(200, TimeUnit.MILLISECONDS);

    assertTrue(lCounter >= mCounter);
  }

}
