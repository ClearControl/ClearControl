package net.imglib2.img.utils.tests;

import static net.imglib2.img.utils.Copy.copy;

import org.junit.Test;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.img.planar.PlanarRandomAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.Fraction;

/**
 * Created by dibrov on 10/03/17.
 */
public class UtilTests
{
  @Test
  public void copyTest()
  {
    long[] dims =
    { 10, 10, 10 };
    OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> img =
                                                                (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) new OffHeapPlanarImgFactory<>().createShortInstance(dims,
                                                                                                                                                                              new Fraction());
    // OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> img = new
    // OffHeapPlanarImg<UnsignedShortType,
    // ShortOffHeapAccess>(dims, new Fraction());

    UnsignedShortType ust = new UnsignedShortType(img);
    img.setLinkedType(ust);

    PlanarRandomAccess<UnsignedShortType> pra = img.randomAccess();

    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++)
      {
        for (int k = 0; k < 10; k++)
        {
          int[] pos =
          { i, j, k };
          pra.setPosition(pos);
          pra.get().set(i + 2 * j + 4 * k);
          System.out.println(pra.get().getIntegerLong());
        }

      }
    }

    long[] dimstocopy =
    { 5, 5, 5 };
    OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> img1 =
                                                                 (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) new OffHeapPlanarImgFactory<>().createShortInstance(dimstocopy,
                                                                                                                                                                               new Fraction());

    long[] loc =
    { 0, 0, 0 };

    UnsignedShortType ust1 = new UnsignedShortType(img1);
    img1.setLinkedType(ust1);

    img1 = copy(img, loc, dimstocopy);

    PlanarRandomAccess<UnsignedShortType> prao = img1.randomAccess();
    System.out.println("new img");
    for (int i = 0; i < 5; i++)
    {
      for (int j = 0; j < 5; j++)
      {
        for (int k = 0; k < 5; k++)
        {
          int[] pos =
          { i, j, k };
          prao.setPosition(pos);
          System.out.println("prao pos: " + prao.getIntPosition(0)
                             + " "
                             + prao.getIntPosition(1)
                             + " "
                             + prao.getIntPosition(2));
          System.out.println(prao.get().getInteger());
        }

      }
    }

  }

}
