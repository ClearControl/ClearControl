package clearcontrol.stack.sourcesink.synthetic;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.units.OrderOfMagnitude;
import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.source.StackSourceInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.RecyclerInterface;

/**
 * Random stack source
 *
 * @author royer
 */
public class RandomStackSource implements StackSourceInterface
{

  private RecyclerInterface<StackInterface, StackRequest> mStackRecycler;
  private final Variable<Long> mWidthVariable, mHeightVariable,
      mDepthVariable;

  /**
   * Instanciates a random stack source.
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @param pStackRecycler
   *          stack recycler
   */
  public RandomStackSource(long pWidth,
                           long pHeight,
                           long pDepth,
                           final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
  {
    mWidthVariable = new Variable<Long>("Width", pWidth);
    mHeightVariable = new Variable<Long>("Height", pHeight);
    mDepthVariable = new Variable<Long>("Depth", pDepth);
    mStackRecycler = pStackRecycler;
  }

  @Override
  public void setStackRecycler(final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
  {
    mStackRecycler = pStackRecycler;
  }

  @Override
  public boolean update()
  {
    return true;
  }

  @Override
  public long getNumberOfStacks()
  {
    return Long.MAX_VALUE;
  }

  @Override
  public long getNumberOfStacks(String pChannel)
  {
    return getNumberOfStacks();
  }

  @Override
  public Double getStackTimeStampInSeconds(long pStackIndex)
  {
    return getStackTimeStampInSeconds(cDefaultChannel, pStackIndex);
  }

  @Override
  public Double getStackTimeStampInSeconds(String pChannel,
                                           long pStackIndex)
  {
    return OrderOfMagnitude.nano2unit(System.nanoTime());
  }

  @Override
  public StackInterface getStack(final long pStackIndex)
  {
    return getStack(cDefaultChannel,
                    pStackIndex,
                    0,
                    TimeUnit.NANOSECONDS);
  }

  @Override
  public StackInterface getStack(String pChannel, long pStackIndex)
  {
    return getStack(pChannel, pStackIndex, 1, TimeUnit.NANOSECONDS);
  }

  @Override
  public StackInterface getStack(String pChannel,
                                 final long pStackIndex,
                                 long pTime,
                                 TimeUnit pTimeUnit)
  {
    if (mStackRecycler == null)
    {
      return null;
    }
    try
    {
      final long lWidth = getWidthVariable().get();
      final long lHeight = getHeightVariable().get();
      final long lDepth = getDepthVariable().get();

      final StackRequest lStackRequest = StackRequest.build(lWidth,
                                                            lHeight,
                                                            lDepth);

      final StackInterface lStack =
                                  mStackRecycler.getOrWait(pTime,
                                                           pTimeUnit,
                                                           lStackRequest);

      ContiguousMemoryInterface lContiguousMemory =
                                                  lStack.getContiguousMemory();

      if (lStack != null)
      {
        if (lContiguousMemory != null)
        {
          final ContiguousBuffer lContiguousBuffer =
                                                   new ContiguousBuffer(lContiguousMemory);
          lContiguousBuffer.rewind();
          for (int z = 0; z < lDepth; z++)
          {
            for (int y = 0; y < lHeight; y++)
            {
              for (int x = 0; x < lWidth; x++)
              {
                final short lValue =
                                   (short) (pStackIndex + x ^ y ^ z);
                lContiguousBuffer.writeShort(lValue);
              }
            }
          }

        }

        lStack.getMetaData()
              .setTimeStampInNanoseconds(System.nanoTime());
        lStack.getMetaData().setIndex(pStackIndex);

      }

      return lStack;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * Returns the width variable
   * 
   * @return width variable
   */
  public Variable<Long> getWidthVariable()
  {
    return mWidthVariable;
  }

  /**
   * Returns the height variable
   * 
   * @return height variable
   */
  public Variable<Long> getHeightVariable()
  {
    return mHeightVariable;
  }

  /**
   * Returns the depth variable
   * 
   * @return depth variable
   */
  public Variable<Long> getDepthVariable()
  {
    return mDepthVariable;
  }

}
