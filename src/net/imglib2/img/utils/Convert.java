package net.imglib2.img.utils;

import java.io.FileOutputStream;

import io.scif.img.ImgOpener;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * Created by dibrov on 11/03/17.
 */
public class Convert
{
  public static void convertTIFFToRaw(String pPathToTIFF,
                                      String pPathToRaw)
  {
    try (FileOutputStream f = new FileOutputStream(pPathToRaw))
    {
      Img<UnsignedShortType> img =
                                 (Img<UnsignedShortType>) new ImgOpener().openImgs(pPathToTIFF)
                                                                         .get(0);

      RandomAccess<UnsignedShortType> ra = img.randomAccess();

      // ra.get().

      int ndim = img.numDimensions();
      if (ndim != 3)
      {
        throw new IllegalArgumentException("don't know what to do with it yet... ndim != 3");
      }
      int x = (int) img.dimension(0);
      int y = (int) img.dimension(1);
      int z = (int) img.dimension(2);

      System.out.println("converting a tiff with dims: " + x
                         + " "
                         + y
                         + " "
                         + z);

      byte[] arr = new byte[2 * x * y * z];

      int mask1 = 0B1111111100000000;
      int mask2 = 0B0000000011111111;

      for (int i = 0; i < x; i++)
      {
        for (int j = 0; j < y; j++)
        {
          for (int k = 0; k < z; k++)
          {
            int pos[] =
            { i, j, k };
            ra.setPosition(pos);
            int curr = ra.get().getInteger();
            // System.out.println("integer is: " + curr);
            arr[2
                * (i + x * j
                   + x * y
                     * k)] =
                           (byte) ((mask1
                                    & (ra.get().getInteger())) >>> 8);
            arr[2 * (i + x * j + x * y * k)
                + 1] = (byte) ((mask2 & ra.get().getInteger()));
            // System.out.println("byte1 is: " +arr[2 * (i + x * j + x * y *
            // k)]);
            // System.out.println("byte2 is: " +arr[2 * (i + x * j + x * y * k)
            // + 1]);
          }
        }
      }

      f.write(arr);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
