package clearcontrol.stack.imglib2;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import clearcontrol.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;

/**
 * Stack to imglib2 Img Image format converter
 *
 * @author Robert Haase, http://github.com/haesleinhuepf
 * @param <T>
 *          image data type
 */
public class StackToImgConverter<T extends RealType<T>>
{
  private StackInterface mStack;
  private Img<T> mResultImg;
  private T mAnyResultingPixel;

  /**
   * Instantiates a stack to image converter with a given stack.
   * 
   * @param pStack
   *          stack
   */
  public StackToImgConverter(StackInterface pStack)
  {
    mStack = pStack;
  }

  /**
   * Returns a random accessible interval for the
   * 
   * @return random accessible interval
   */
  @SuppressWarnings("unchecked")
  public RandomAccessibleInterval<T> getRandomAccessibleInterval()
  {
    Img<T> lReturnImg = null;

    final ContiguousMemoryInterface contiguousMemory =
                                                     mStack.getContiguousMemory();

    int numDimensions = mStack.getNumberOfDimensions();
    if (mStack.getNumberOfChannels() > 1)
    {
      // Channels are an additional dimension in imglib2 world
      numDimensions++;
    }

    long[] dimensions = new long[numDimensions];
    dimensions[0] = mStack.getWidth();
    dimensions[1] = mStack.getHeight();
    if (dimensions.length > 2)
    {
      dimensions[2] = mStack.getDepth();
    }
    if (dimensions.length > 3)
    {
      dimensions[3] = mStack.getNumberOfChannels();
    }

    if (mStack.getDataType() == NativeTypeEnum.Float
        || mStack.getDataType() == NativeTypeEnum.HalfFloat)
    {
      float[] pixelArray =
                         new float[(int) (contiguousMemory.getSizeInBytes()
                                          / mStack.getBytesPerVoxel())
                                   % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg = (Img<T>) ArrayImgs.floats(pixelArray, dimensions);
    }
    else if (mStack.getDataType() == NativeTypeEnum.Short
             || mStack.getDataType() == NativeTypeEnum.UnsignedShort)
    {
      short[] pixelArray =
                         new short[(int) (contiguousMemory.getSizeInBytes()
                                          / mStack.getBytesPerVoxel())
                                   % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg = (Img<T>) ArrayImgs.shorts(pixelArray, dimensions);
    }
    else if (mStack.getDataType() == NativeTypeEnum.Byte
             || mStack.getDataType() == NativeTypeEnum.UnsignedByte)
    {
      byte[] pixelArray =
                        new byte[(int) (contiguousMemory.getSizeInBytes()
                                        / mStack.getBytesPerVoxel())
                                 % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg = (Img<T>) ArrayImgs.bytes(pixelArray, dimensions);
    }
    else if (mStack.getDataType() == NativeTypeEnum.Int
             || mStack.getDataType() == NativeTypeEnum.UnsignedInt)
    {
      int[] pixelArray =
                       new int[(int) (contiguousMemory.getSizeInBytes()
                                      / mStack.getBytesPerVoxel())
                               % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg = (Img<T>) ArrayImgs.ints(pixelArray, dimensions);
    }
    else if (mStack.getDataType() == NativeTypeEnum.Long
             || mStack.getDataType() == NativeTypeEnum.UnsignedLong)
    {
      long[] pixelArray =
                        new long[(int) (contiguousMemory.getSizeInBytes()
                                        / mStack.getBytesPerVoxel())
                                 % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg = (Img<T>) ArrayImgs.longs(pixelArray, dimensions);
    }
    else
    {
      throw new IllegalArgumentException("Unknown type: "
                                         + mStack.getDataType());
    }

    mResultImg = lReturnImg;
    mAnyResultingPixel = mResultImg.cursor().next();
    mResultImg.cursor().reset();

    // in ImageJ, the dimension order must be X, Y, C, Z
    if (dimensions.length == 4)
    {
      return Views.permute(mResultImg, 2, 3);
    }

    return lReturnImg;
  }

  /**
   * Returns any pixel.
   * 
   * @return pixel
   */
  public T getAnyPixel()
  {
    return mAnyResultingPixel;
  }
}
