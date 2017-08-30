package clearcontrol.stack.imglib2.demo;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.imglib2.ImageJStackDisplay;
import ij.ImagePlus;

import org.junit.Test;

/**
 * ImageJ stack display demo
 *
 * @author royer
 */
public class ImageJStackDisplayDemo
{

  /**
   * Demo
   */
  @Test
  public void demo()
  {
    final OffHeapPlanarStack lStack =
                                    OffHeapPlanarStack.createStack(320,
                                                                   321,
                                                                   322);

    ImagePlus lShow = ImageJStackDisplay.show(lStack);

    while (lShow.isVisible())
      ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);
  }

}
