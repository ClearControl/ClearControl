package clearcontrol.stack.imglib2.demo;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.imglib2.ImageJStackDisplay;
import coremem.enums.NativeTypeEnum;
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
    int lChannels = 2;
    int lWidth = 320;
    int lHeight = 321;
    int lDepth = 322;

    float[] lFloatArray = new float[lWidth * lHeight
                                    * lDepth
                                    * lChannels];
    long[] lLongArray =
                      new long[lWidth * lHeight * lDepth * lChannels];
    short[] lShortArray = new short[lWidth * lHeight
                                    * lDepth
                                    * lChannels];

    for (int i = 0; i < lFloatArray.length; i++)
    {
      lFloatArray[i] = i;
      lLongArray[i] = i;
      lShortArray[i] = (short) i;
    }

    // show a float image
    final OffHeapPlanarStack lFloatStack =
                                         new OffHeapPlanarStack(false,
                                                                0,
                                                                NativeTypeEnum.Float,
                                                                lChannels,
                                                                lWidth,
                                                                lHeight,
                                                                lDepth);
    lFloatStack.getContiguousMemory().copyFrom(lFloatArray);
    ImagePlus lFloatImagePlus = ImageJStackDisplay.show(lFloatStack);
    lFloatImagePlus.setTitle("Float");

    // show a short image
    final OffHeapPlanarStack lShortStack =
                                         new OffHeapPlanarStack(false,
                                                                0,
                                                                NativeTypeEnum.Short,
                                                                lChannels,
                                                                lWidth,
                                                                lHeight,
                                                                lDepth);
    lShortStack.getContiguousMemory().copyFrom(lShortArray);
    ImagePlus lShortImagePlus = ImageJStackDisplay.show(lShortStack);
    lShortImagePlus.setTitle("Short");

    // show a long image
    final OffHeapPlanarStack lLongStack =
                                        new OffHeapPlanarStack(false,
                                                               0,
                                                               NativeTypeEnum.Long,
                                                               lChannels,
                                                               lWidth,
                                                               lHeight,
                                                               lDepth);
    lLongStack.getContiguousMemory().copyFrom(lLongArray);
    ImagePlus lLongImagePlus = ImageJStackDisplay.show(lLongStack);
    lLongImagePlus.setTitle("Long");

    while (lFloatImagePlus.isVisible() || lShortImagePlus.isVisible()
           || lLongImagePlus.isVisible())
      ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);
  }

}
