package rtlib.stack.imglib2;

import ij.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.stack.StackInterface;

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
