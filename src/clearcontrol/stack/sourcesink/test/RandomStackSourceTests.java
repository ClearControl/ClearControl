package clearcontrol.stack.sourcesink.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.synthetic.RandomStackSource;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

import org.junit.Test;

/**
 * Random stack source tests
 *
 * @author royer
 */
public class RandomStackSourceTests
{

  /**
   * Tests
   * 
   * @throws IOException
   *           NA
   */
  @Test
  public void test() throws IOException
  {
    final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                         new ContiguousOffHeapPlanarStackFactory();

    final RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                                    new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                    10);
    RandomStackSource lRandomStackSource =
                                         new RandomStackSource(100L,
                                                               101L,
                                                               103L,
                                                               lRecycler);

    for (int i = 0; i < 100; i++)
    {
      StackInterface lStack = lRandomStackSource.getStack(i);

      lStack.getContiguousMemory().setByte(1, (byte) i);
      assertTrue(lStack.getContiguousMemory().getByte(1) == (byte) i);

      lStack.release();
    }

  }

}
