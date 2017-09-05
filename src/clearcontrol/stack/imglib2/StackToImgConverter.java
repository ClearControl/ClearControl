package clearcontrol.stack.imglib2;

import clearcontrol.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Stack to imglib2 Img Image format converter
 *
 * @author Robert Haase, http://github.com/haesleinhuepf
 */
public class StackToImgConverter <T extends RealType<T>>
{
  private StackInterface mStack;
  private Img<T> mResultImg;
  private T mAnyResultingPixel;

  public StackToImgConverter(StackInterface pStack)
  {
    mStack = pStack;
  }

  public Img getImg()
  {
    Img lReturnImg = null;

    final ContiguousMemoryInterface
        contiguousMemory =
        mStack.getContiguousMemory();

    /*
    long[] dimensions = new long[mStack.getDimensions().length];
    dimensions[0] = mStack.getWidth();
    dimensions[1] = mStack.getHeight();
    dimensions[2] = mStack.getWidth();
    dimensions[3] = mStack.getWidth();
    */

    if (mStack.getDataType() == NativeTypeEnum.Float
        || mStack.getDataType() == NativeTypeEnum.HalfFloat)
    {
      float[]
          pixelArray =
          new float[(int) (contiguousMemory.getSizeInBytes()
                           / mStack.getBytesPerVoxel())
                    % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg =
          ArrayImgs.floats(pixelArray, mStack.getDimensions());
    }
    else if (mStack.getDataType() == NativeTypeEnum.Short
             || mStack.getDataType() == NativeTypeEnum.UnsignedShort)
    {
      short[]
          pixelArray =
          new short[(int) (contiguousMemory.getSizeInBytes()
                           / mStack.getBytesPerVoxel())
                    % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg =
          ArrayImgs.shorts(pixelArray, mStack.getDimensions());
    }
    else if (mStack.getDataType() == NativeTypeEnum.Byte
             || mStack.getDataType() == NativeTypeEnum.UnsignedByte)
    {
      byte[]
          pixelArray =
          new byte[(int) (contiguousMemory.getSizeInBytes()
                          / mStack.getBytesPerVoxel())
                   % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg =
          ArrayImgs.bytes(pixelArray, mStack.getDimensions());
    }
    else if (mStack.getDataType() == NativeTypeEnum.Int
             || mStack.getDataType() == NativeTypeEnum.UnsignedInt)
    {
      int[]
          pixelArray =
          new int[(int) (contiguousMemory.getSizeInBytes()
                         / mStack.getBytesPerVoxel())
                  % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg = ArrayImgs.ints(pixelArray, mStack.getDimensions());
    }
    else if (mStack.getDataType() == NativeTypeEnum.Long
             || mStack.getDataType() == NativeTypeEnum.UnsignedLong)
    {
      long[]
          pixelArray =
          new long[(int) (contiguousMemory.getSizeInBytes()
                          / mStack.getBytesPerVoxel())
                   % Integer.MAX_VALUE];
      contiguousMemory.copyTo(pixelArray);
      lReturnImg =
          ArrayImgs.longs(pixelArray, mStack.getDimensions());
    } else {
      throw new IllegalArgumentException("Unknown type: " + mStack.getDataType());
    }

    mResultImg = lReturnImg;
    mAnyResultingPixel = mResultImg.cursor().next();
    mResultImg.cursor().reset();

    return lReturnImg;
  }

  public T getAnyPixel() {
    return mAnyResultingPixel;
  }
}
