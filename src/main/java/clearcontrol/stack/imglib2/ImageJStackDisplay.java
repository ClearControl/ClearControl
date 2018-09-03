package clearcontrol.stack.imglib2;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.ComputeMinMax;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import clearcontrol.stack.StackInterface;
import ij.ImageJ;
import ij.ImagePlus;

/**
 * ImageJ Stck display
 *
 * @author royer
 */
public class ImageJStackDisplay
{
  private static ImageJ sImageJ;

  /**
   * Opens an ImageJ window and displays the given stack
   *
   * @param pStack
   *          stack to display
   * @return image plus
   */
  public static <T extends RealType<T>> ImagePlus show(StackInterface pStack)
  {
    // run/show ImageJ
    if (sImageJ == null)
      sImageJ = new ImageJ();
    if (!sImageJ.isVisible())
      sImageJ.setVisible(true);

    // do the conversion
    StackToImgConverter<T> lStackToImgConverter =
                                                new StackToImgConverter<T>(pStack);
    RandomAccessibleInterval<T> lConvertedRai =
                                              lStackToImgConverter.getRandomAccessibleInterval();

    // fix visualisation window (full range of pixel values should be shown)
    T lMinPixelT = lStackToImgConverter.getAnyPixel().copy();
    T lMaxPixelT = lStackToImgConverter.getAnyPixel().copy();
    new ComputeMinMax<T>(Views.iterable(lConvertedRai),
                         lMinPixelT,
                         lMaxPixelT).process();
    ImagePlus lResultImp = ImageJFunctions.show(lConvertedRai);
    lResultImp.setDisplayRange(lMinPixelT.getRealFloat(),
                               lMaxPixelT.getRealFloat());

    return lResultImp;
  }

}
