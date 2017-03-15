package clearcontrol.stack.imglib2;

import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import clearcontrol.stack.StackInterface;
import ij.ImageJ;

public class ImageJStackDisplay
{
  private static ImageJ sImageJ;

  public static void show(StackInterface pStack)
  {
    if (sImageJ == null)
      sImageJ = new ImageJ();
    if (!sImageJ.isVisible())
      sImageJ.setVisible(true);
    Img<UnsignedShortType> lCopy = pStack.getImage().copy();
    ImageJFunctions.show(lCopy);
  }
}
