package clearcontrol.stack.imglib2;

import net.imglib2.algorithm.stats.ComputeMinMax;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import clearcontrol.stack.StackInterface;
import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.type.numeric.RealType;

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
    StackToImgConverter<T> lStackToImgConverter = new StackToImgConverter<T>(pStack);
    Img lConvertedImg = lStackToImgConverter.getImg();

    // fix visualisation window (full range of pixel values should be shown)
    T lMinPixelT = lStackToImgConverter.getAnyPixel().copy();
    T lMaxPixelT = lStackToImgConverter.getAnyPixel().copy();
    new ComputeMinMax<T>(lConvertedImg, lMinPixelT, lMaxPixelT).process();
    ImagePlus lResultImp = ImageJFunctions.show(lConvertedImg);
    lResultImp.setDisplayRange(lMinPixelT.getRealFloat(), lMaxPixelT.getRealFloat());

    return lResultImp;
  }


}
