package clearcontrol.ip.iqm;

import static java.lang.Math.sqrt;
import static java.lang.Math.toIntExact;

import clearcontrol.stack.OffHeapPlanarStack;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.util.Size;

import org.jtransforms.dct.DoubleDCT_2D;
import pl.edu.icm.jlargearrays.DoubleLargeArray;

public class DCTS2D implements ImageQualityMetricInterface
{
  private ContiguousBuffer mWorkingBuffer;

  private final Table<Long, Long, DoubleDCT_2D> mDoubleDCT2DCache =
                                                                  HashBasedTable.create();

  private double mPSFSupportRadius = 3;

  public DCTS2D()
  {
    super();
  }

  private DoubleDCT_2D getDCTForWidthAndHeight(long pWidth,
                                               long pHeight)
  {
    DoubleDCT_2D lDoubleDCT_2D =
                               mDoubleDCT2DCache.get(pWidth, pHeight);

    if (lDoubleDCT_2D == null)
    {
      try
      {
        lDoubleDCT_2D = new DoubleDCT_2D(pHeight, pWidth, true);
        mDoubleDCT2DCache.put(pWidth, pHeight, lDoubleDCT_2D);
      }
      catch (final Throwable e)
      {
        e.printStackTrace();
      }
    }

    return lDoubleDCT_2D;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final double[] computeImageQualityMetric(OffHeapPlanarStack pStack)
  {
    long lWidth = pStack.getWidth();
    long lHeight = pStack.getHeight();
    long lDepth = pStack.getDepth();

    final double[] lDCTSArray = new double[toIntExact(lDepth)];
    for (int z = 0; z < lDepth; z++)
    {
      long lNumberOfPixelsPerPlane = lWidth * lHeight;
      final ContiguousMemoryInterface lPlaneContiguousMemory =
                                                             pStack.getContiguousMemory(z);

      if (mWorkingBuffer == null
          || mWorkingBuffer.getSizeInBytes() != lNumberOfPixelsPerPlane
                                                * Size.DOUBLE)
      {
        mWorkingBuffer =
                       ContiguousBuffer.allocate(lNumberOfPixelsPerPlane
                                                 * Size.DOUBLE);
      }

      mWorkingBuffer.rewind();
      for (int i = 0; i < lNumberOfPixelsPerPlane; i++)
      {
        double lValue = lPlaneContiguousMemory.getCharAligned(i);
        mWorkingBuffer.writeDouble(lValue);
      }

      final long lAddress = mWorkingBuffer.getContiguousMemory()
                                          .getAddress();
      final DoubleLargeArray lDoubleLargeArray =
                                               new DoubleLargeArray(pStack,
                                                                    lAddress,
                                                                    lNumberOfPixelsPerPlane);

      final double lDCTS =
                         computeDCTSForSinglePlane(lDoubleLargeArray,
                                                   lWidth,
                                                   lHeight,
                                                   getPSFSupportRadius());

      lDCTSArray[z] = lDCTS;
    }

    return lDCTSArray;
  }

  private final double computeDCTSForSinglePlane(DoubleLargeArray pDoubleLargeArray,
                                                 long pWidth,
                                                 long pHeight,
                                                 double pPSFSupportRadius)
  {
    final DoubleDCT_2D lDCTForWidthAndHeight =
                                             getDCTForWidthAndHeight(pWidth,
                                                                     pHeight);

    lDCTForWidthAndHeight.forward(pDoubleLargeArray, false);

    normalizeL2(pDoubleLargeArray);

    final long lOTFSupportRadiusX = Math.round(pWidth
                                               / pPSFSupportRadius);
    final long lOTFSupportRadiusY = Math.round(pHeight
                                               / pPSFSupportRadius);

    final double lEntropy =
                          entropyPerPixelSubTriangle(pDoubleLargeArray,
                                                     pWidth,
                                                     pHeight,
                                                     0,
                                                     0,
                                                     lOTFSupportRadiusX,
                                                     lOTFSupportRadiusY);

    return lEntropy;
  }

  private void normalizeL2(DoubleLargeArray pDoubleLargeArray)
  {
    final double lL2 = computeL2(pDoubleLargeArray);
    final double lIL2 = 1.0 / lL2;
    final long lLength = pDoubleLargeArray.length();

    for (long i = 0; i < lLength; i++)
    {
      final double lValue = pDoubleLargeArray.getDouble(i);
      pDoubleLargeArray.setDouble(i, lValue * lIL2);
    }
  }

  private double computeL2(DoubleLargeArray pDoubleLargeArray)
  {
    final long lLength = pDoubleLargeArray.length();

    double l2 = 0;
    for (long i = 0; i < lLength; i++)
    {
      final double lValue = pDoubleLargeArray.getDouble(i);
      l2 += lValue * lValue;
    }

    return sqrt(l2);
  }

  private final double entropyPerPixelSubTriangle(DoubleLargeArray pDoubleLargeArray,
                                                  final long pWidth,
                                                  final long pHeight,
                                                  final long xl,
                                                  final long yl,
                                                  final long xh,
                                                  final long yh)
  {
    double entropy = 0;
    for (long y = yl; y < yh; y++)
    {
      final long yi = y * pWidth;

      final long xend = xh - y * xh / yh;
      entropy = entropySub(pDoubleLargeArray, xl, entropy, yi, xend);
    }
    entropy = -entropy / (2 * xh * yh);

    return entropy;
  }

  private double entropySub(DoubleLargeArray pDoubleLargeArray,
                            final long xl,
                            final double entropy,
                            final long yi,
                            final long xend)
  {
    double lEntropy = entropy;
    for (long x = xl; x < xend; x++)
    {
      final long i = yi + x;
      final double value = pDoubleLargeArray.getDouble(i);
      if (value > 0)
      {
        lEntropy += value * Math.log(value);
      }
      else if (value < 0)
      {
        lEntropy += -value * Math.log(-value);
      }
    }
    return lEntropy;
  }

  public double getPSFSupportRadius()
  {
    return mPSFSupportRadius;
  }

  public void setPSFSupportRadius(double pPSFSupportRadius)
  {
    mPSFSupportRadius = pPSFSupportRadius;
  }

}
