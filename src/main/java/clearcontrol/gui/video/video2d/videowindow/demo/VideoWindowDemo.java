package clearcontrol.gui.video.video2d.videowindow.demo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import clearcontrol.gui.video.video2d.videowindow.VideoWindow;
import coremem.enums.NativeTypeEnum;
import coremem.offheap.OffHeapMemory;

import org.junit.Test;

public class VideoWindowDemo
{

  static volatile long rnd = 123456789;

  @Test
  public void simpleRandomUnsignedByteDataWithAspectRatioTest() throws InterruptedException,
                                                                IOException
  {
    final int lWidth = 512;
    final int lHeight = 128;
    final OffHeapMemory lBuffer =
                                OffHeapMemory.allocateBytes(lWidth
                                                            * lHeight);

    final VideoWindow lVideoWindow =
                                   new VideoWindow("VideoWindow test",
                                                   NativeTypeEnum.UnsignedByte,
                                                   768,
                                                   768,
                                                   false);
    lVideoWindow.setDisplayOn(true);
    lVideoWindow.setManualMinMax(true);
    // lVideoWindow.setLinearInterpolation(true);

    lVideoWindow.setVisible(true);

    lVideoWindow.start();
    for (int i = 0; i < 10000 && lVideoWindow.isVisible(); i++)
    {
      generateUnsignedByteNoiseBuffer(lBuffer);
      lVideoWindow.sendBuffer(lBuffer, lWidth, lHeight);
      lVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
      Thread.sleep(10);
    }
    lVideoWindow.stop();

    lVideoWindow.close();

  }

  @Test
  public void simpleRandomUnsignedByteDataTest() throws InterruptedException,
                                                 IOException
  {
    final int lSize = 512;
    final OffHeapMemory lBuffer =
                                OffHeapMemory.allocateBytes(lSize
                                                            * lSize);

    final VideoWindow lVideoWindow =
                                   new VideoWindow("VideoWindow test",
                                                   NativeTypeEnum.UnsignedByte,
                                                   768,
                                                   768,
                                                   false);
    lVideoWindow.setDisplayOn(true);
    lVideoWindow.setManualMinMax(true);
    // lVideoWindow.setLinearInterpolation(true);

    lVideoWindow.setVisible(true);

    lVideoWindow.start();
    for (int i = 0; i < 100 && lVideoWindow.isVisible(); i++)
    {
      generateUnsignedByteNoiseBuffer(lBuffer);
      lVideoWindow.sendBuffer(lBuffer, lSize, lSize);
      lVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
      Thread.sleep(10);
    }
    lVideoWindow.stop();

    lVideoWindow.close();

  }

  @Test
  public void simpleRandomUnsignedShortDataTest() throws InterruptedException,
                                                  IOException
  {
    final int lSize = 512;
    final OffHeapMemory lBuffer =
                                OffHeapMemory.allocateShorts(lSize
                                                             * lSize);

    final VideoWindow lVideoWindow =
                                   new VideoWindow("VideoWindow test",
                                                   NativeTypeEnum.UnsignedShort,
                                                   768,
                                                   768,
                                                   false);
    lVideoWindow.setDisplayOn(true);
    lVideoWindow.setManualMinMax(true);
    // lVideoWindow.setLinearInterpolation(true);

    lVideoWindow.setVisible(true);

    lVideoWindow.start();
    for (int i = 0; i < 100000 && lVideoWindow.isVisible(); i++)
    {
      generateUnsignedShortNoiseBuffer(lBuffer);
      lVideoWindow.sendBuffer(lBuffer, lSize, lSize);
      lVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
      Thread.sleep(10);
    }
    lVideoWindow.stop();

    lVideoWindow.close();

  }

  @Test
  public void simpleRandomUnsignedIntDataTest() throws InterruptedException,
                                                IOException
  {
    final int lSize = 512;
    final OffHeapMemory lBuffer = OffHeapMemory.allocateInts(lSize
                                                             * lSize);

    final VideoWindow lVideoWindow =
                                   new VideoWindow("VideoWindow test",
                                                   NativeTypeEnum.UnsignedInt,
                                                   768,
                                                   768,
                                                   false);
    lVideoWindow.setDisplayOn(true);
    lVideoWindow.setManualMinMax(true);
    // lVideoWindow.setLinearInterpolation(true);

    lVideoWindow.setVisible(true);

    lVideoWindow.start();
    for (int i = 0; i < 100000 && lVideoWindow.isVisible(); i++)
    {
      generateUnsignedIntNoiseBuffer(lBuffer);
      lVideoWindow.sendBuffer(lBuffer, lSize, lSize);
      lVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
      Thread.sleep(10);
    }
    lVideoWindow.stop();

    lVideoWindow.close();

  }

  @Test
  public void simpleRandomFloatDataTest() throws InterruptedException,
                                          IOException
  {
    final int lSize = 512;
    final OffHeapMemory lBuffer =
                                OffHeapMemory.allocateFloats(lSize
                                                             * lSize);

    final VideoWindow lVideoWindow =
                                   new VideoWindow("VideoWindow test",
                                                   NativeTypeEnum.Float,
                                                   768,
                                                   768,
                                                   false);
    lVideoWindow.setDisplayOn(true);
    lVideoWindow.setManualMinMax(true);
    // lVideoWindow.setLinearInterpolation(true);

    lVideoWindow.setVisible(true);

    lVideoWindow.start();
    for (int i = 0; i < 100000 && lVideoWindow.isVisible(); i++)
    {
      generateFloatNoiseBuffer(lBuffer);
      lVideoWindow.sendBuffer(lBuffer, lSize, lSize);
      lVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
      Thread.sleep(10);
    }
    lVideoWindow.stop();

    lVideoWindow.close();

  }

  @Test
  public void simpleRandomDoubleDataTest() throws InterruptedException,
                                           IOException
  {
    final int lSize = 512;
    final OffHeapMemory lBuffer =
                                OffHeapMemory.allocateDoubles(lSize
                                                              * lSize);

    final VideoWindow lVideoWindow =
                                   new VideoWindow("VideoWindow test",
                                                   NativeTypeEnum.Double,
                                                   768,
                                                   768,
                                                   false);
    lVideoWindow.setDisplayOn(true);
    lVideoWindow.setManualMinMax(true);
    // lVideoWindow.setLinearInterpolation(true);

    lVideoWindow.setVisible(true);

    lVideoWindow.start();
    for (int i = 0; i < 100000 && lVideoWindow.isVisible(); i++)
    {
      generateDoubleNoiseBuffer(lBuffer);
      lVideoWindow.sendBuffer(lBuffer, lSize, lSize);
      lVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
      Thread.sleep(10);
    }
    lVideoWindow.stop();
    lVideoWindow.close();
  }

  private void generateFloatNoiseBuffer(final OffHeapMemory pOffHeapMemory)
  {

    final int lLength = (int) pOffHeapMemory.getSizeInBytes() / 4;
    for (int i = 0; i < lLength; i++)
    {
      final float lValue = (float) (Math.random() * 1);
      pOffHeapMemory.setFloatAligned(i, lValue);
    }
  }

  private void generateDoubleNoiseBuffer(final OffHeapMemory pOffHeapMemory)
  {

    final int lLength = (int) pOffHeapMemory.getSizeInBytes() / 8;
    for (int i = 0; i < lLength; i++)
    {
      final double lValue = Math.random() * 1;
      pOffHeapMemory.setDoubleAligned(i, lValue);
    }
  }

  private void generateUnsignedIntNoiseBuffer(final OffHeapMemory pOffHeapMemory)
  {

    final int lNumberOfInts =
                            (int) pOffHeapMemory.getSizeInBytes() / 4;
    for (int i = 0; i < lNumberOfInts; i++)
    {
      rnd = ((rnd % 257) * i) + 1 + (rnd << 7);
      final int lValue = (int) (rnd & 0xFFFFFFFF); // Math.random()
      // System.out.print(lValue);
      pOffHeapMemory.setIntAligned(i, lValue);
    }
  }

  private void generateUnsignedShortNoiseBuffer(final OffHeapMemory pOffHeapMemory)
  {

    final int lNumberOfShorts = (int) pOffHeapMemory.getSizeInBytes()
                                / 2;
    for (int i = 0; i < lNumberOfShorts; i++)
    {
      rnd = ((rnd % 257) * i) + 1 + (rnd << 7);
      final int lValue = (int) (rnd & 0xFFFF); // Math.random()
      // System.out.print(lValue);
      pOffHeapMemory.setShortAligned(i, (short) lValue);
    }
  }

  private void generateUnsignedByteNoiseBuffer(final OffHeapMemory pOffHeapMemory)
  {

    final int lNumberOfBytes = (int) pOffHeapMemory.getSizeInBytes();
    for (int i = 0; i < lNumberOfBytes; i++)
    {
      rnd = ((rnd % 257) * i) + 1 + (rnd << 7);
      final byte lValue = (byte) (rnd & 0xFF); // Math.random()
      // System.out.print(lValue);
      pOffHeapMemory.setByteAligned(i, lValue);
    }
  }
}
