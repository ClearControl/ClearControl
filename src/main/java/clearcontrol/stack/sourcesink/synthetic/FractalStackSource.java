package clearcontrol.stack.sourcesink.synthetic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.source.StackSourceInterface;

import com.google.common.collect.Lists;

import coremem.ContiguousMemoryInterface;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

/**
 * Fractal stack source. used mostly for testing and demoing
 *
 * @author royer
 */
public class FractalStackSource implements
                                StackSourceInterface,
                                AutoCloseable
{

  private RecyclerInterface<StackInterface, StackRequest> mStackRecycler;

  /**
   * Instantiates a fractal stack source
   * 
   */
  public FractalStackSource()
  {
    final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                         new ContiguousOffHeapPlanarStackFactory();

    mStackRecycler =
                   new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                   10);
  }

  @Override
  public long getNumberOfStacks()
  {
    return 1000;
  }

  @Override
  public long getNumberOfStacks(String pChannel)
  {
    if (pChannel.equals(cDefaultChannel))
      return getNumberOfStacks();
    else if (pChannel.equals("blank"))
      return 24;
    else
      return 100;
  }

  @Override
  public ArrayList<String> getChannelList()
  {
    return Lists.newArrayList(cDefaultChannel, "blank", "other");
  }

  @Override
  public void setStackRecycler(final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
  {
    mStackRecycler = pStackRecycler;

  }

  @Override
  public StackInterface getStack(long pStackIndex)
  {
    return getStack(cDefaultChannel, pStackIndex);
  }

  @Override
  public StackInterface getStack(final String pChannel,
                                 final long pStackIndex)
  {
    return getStack(pChannel, pStackIndex, 1, TimeUnit.NANOSECONDS);
  }

  @Override
  public StackInterface getStack(final String pChannel,
                                 final long pStackIndex,
                                 final long pTime,
                                 final TimeUnit pTimeUnit)
  {
    if (mStackRecycler == null)
    {
      return null;
    }
    try
    {

      final StackRequest lStackRequest = StackRequest.build(128,
                                                            128,
                                                            128);

      final StackInterface lStack =
                                  mStackRecycler.getOrWait(pTime,
                                                           pTimeUnit,
                                                           lStackRequest);

      ContiguousMemoryInterface lMemory =
                                        lStack.getContiguousMemory();

      int lWidth = (int) lStack.getWidth();
      int lHeight = (int) lStack.getHeight();
      int lDepth = (int) lStack.getDepth();

      if (pChannel == cDefaultChannel)
        for (int z = 0; z < lDepth; z++)
          for (int y = 0; y < lHeight; y++)
            for (int x = 0; x < lWidth; x++)
            {
              long lIndex = x + y * lWidth + z * lHeight * lWidth;
              short lValue = (short) (x ^ y ^ z ^ pStackIndex);
              lMemory.setShortAligned(lIndex, lValue);
            }
      else if (pChannel == "blank")
        for (int z = 0; z < lDepth; z++)
          for (int y = 0; y < lHeight; y++)
            for (int x = 0; x < lWidth; x++)
            {
              long lIndex = x + y * lWidth + z * lHeight * lWidth;
              short lValue = (short) (0 + pStackIndex);
              lMemory.setShortAligned(lIndex, lValue);
            }
      else if (pChannel == "other")
        for (int z = 0; z < lDepth; z++)
          for (int y = 0; y < lHeight; y++)
            for (int x = 0; x < lWidth; x++)
            {
              long lIndex = x + y * lWidth + z * lHeight * lWidth;
              short lValue = (short) ((x * y) ^ (y * z)
                                      ^ (z * x)
                                      ^ (pStackIndex * (x * y * z)));
              lMemory.setShortAligned(lIndex, lValue);
            }

      return lStack;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public boolean update()
  {
    return true;
  }

  @Override
  public Double getStackTimeStampInSeconds(long pStackIndex)
  {
    return (double) pStackIndex;
  }

  @Override
  public Double getStackTimeStampInSeconds(String pChannel,
                                           long pStackIndex)
  {
    return (double) pStackIndex;
  }

  @Override
  public void close() throws IOException
  {

  }

}
