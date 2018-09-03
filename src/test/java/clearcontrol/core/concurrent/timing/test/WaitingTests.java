package clearcontrol.core.concurrent.timing.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import clearcontrol.core.concurrent.timing.WaitingInterface;

import org.junit.Test;

/**
 * Waiting tests
 *
 * @author royer
 */
public class WaitingTests
{
  AtomicBoolean mWaitFlag = new AtomicBoolean(false);
  AtomicBoolean mDoneFlag = new AtomicBoolean(false);

  class TestClass implements WaitingInterface
  {
    public void switchOn()
    {
      final Runnable lRunnable = () -> {
        if (waitFor(() -> mWaitFlag.get()))
          mDoneFlag.set(true);
      };
      new Thread(lRunnable).start();
    }

    public void switchOff()
    {
      final Runnable lRunnable = () -> {
        if (waitFor(1L, TimeUnit.NANOSECONDS, () -> mWaitFlag.get()))
          mDoneFlag.set(true);
      };
      new Thread(lRunnable).start();
    }
  }

  /**
   * tests the methods provided by the Waiting Interface
   * 
   * @throws InterruptedException
   *           n/A
   */
  @Test
  public void test() throws InterruptedException
  {
    final TestClass lTestClass = new TestClass();
    lTestClass.switchOn();
    assertFalse(mDoneFlag.get());
    mWaitFlag.set(true);
    Thread.sleep(100);
    assertTrue(mDoneFlag.get());
    mDoneFlag.set(false);
    mWaitFlag.set(false);

    lTestClass.switchOff();
    assertFalse(mDoneFlag.get());
    Thread.sleep(100);
    mWaitFlag.set(true);
    Thread.sleep(100);
    assertFalse(mDoneFlag.get());

  }
}
