package clearcontrol.microscope.adaptive.utils.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import clearcontrol.microscope.adaptive.utils.NDIterator;

/**
 * n-dimensionsl iterator tests
 *
 * @author royer
 */
public class NDIteratorTests
{

  /**
   * Tests
   */
  @Test
  public void test()
  {
    NDIterator lNDIterator = new NDIterator(2, 3, 5);

    int lNumberOfIterations = lNDIterator.getNumberOfIterations();

    int i = 0;
    int[] lNext = null;
    while (lNDIterator.hasNext())
    {
      int lRemainingNumberOfIterations =
                                       lNDIterator.getRemainingNumberOfIterations();
      lNext = lNDIterator.next();

      System.out.format("i=%d  -> %s (remaining: %d of %d) \n",
                        i,
                        Arrays.toString(lNext),
                        lRemainingNumberOfIterations,
                        lNumberOfIterations);

      if (i == 0)
        assertArrayEquals(lNext, new int[]
        { 0, 0, 0 });
      if (i == 10)
        assertArrayEquals(lNext, new int[]
        { 0, 2, 1 });
      if (i == 20)
        assertArrayEquals(lNext, new int[]
        { 0, 1, 3 });
      if (i == 29)
        assertArrayEquals(lNext, new int[]
        { 1, 2, 4 });

      i++;
    }

    assertEquals(i, lNDIterator.getNumberOfIterations());
  }

}
