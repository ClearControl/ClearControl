package clearcontrol.core.device.task.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.device.task.TaskDevice;

import org.junit.Test;

/**
 * Task device tests
 *
 * @author royer
 */
public class TaskDeviceTests
{

  class TestTaskDevice extends TaskDevice
  {

    public TestTaskDevice()
    {
      super("TestTaskDevice");
    }

    @Override
    public void run()
    {
      System.out.println("Beginned task");
      ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);
      System.out.println("Ended task");
    }

  }

  /**
   * Basic test
   */
  @Test
  public void test()
  {
    TestTaskDevice lTestTaskDevice = new TestTaskDevice();

    assertFalse(lTestTaskDevice.getIsRunningVariable().get());

    lTestTaskDevice.getStartSignalVariable().set(true);
    System.out.println("sent start");
    assertTrue(lTestTaskDevice.waitForStarted(1, TimeUnit.SECONDS));
    System.out.println("started");

    assertTrue(lTestTaskDevice.waitForStopped(1, TimeUnit.SECONDS));
    System.out.println("stopped");

    assertFalse(lTestTaskDevice.getIsRunningVariable().get());

  }

}
