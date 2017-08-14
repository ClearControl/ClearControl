package clearcontrol.stack.imglib2;

import net.imglib2.img.display.imagej.ImageJFunctions;
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
  public static ImagePlus show(StackInterface pStack)
  {
    if (sImageJ == null)
      sImageJ = new ImageJ();
    if (!sImageJ.isVisible())
      sImageJ.setVisible(true);

    // TODO: make a copy fo the data into an imglib2 image

    return ImageJFunctions.show(null);
  }
}
