package clearcontrol.core.variable.bounded.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import clearcontrol.core.variable.bounded.BoundedVariable;

import org.junit.Test;

/**
 * Bounded variable tests
 *
 * @author royer
 */
public class BoundedVariableTests
{

  /**
   * test bounds
   */
  @Test
  public void testBounds()
  {
    BoundedVariable<Double> lBoundedNumber =
                                           new BoundedVariable<Double>("doublevar",
                                                                       0.0,
                                                                       -1.0,
                                                                       +2.0);

    assertTrue(lBoundedNumber.get() == 0.0);

    lBoundedNumber.set(3.0);

    assertTrue(lBoundedNumber.get() == 2.0);

    lBoundedNumber.set(-2.0);

    assertTrue(lBoundedNumber.get() == -1.0);
  }

  /**
   * Test cross type
   */
  @Test
  public void testCrossType()
  {
    BoundedVariable<Long> lBoundedNumber =
                                         new BoundedVariable<Long>("longvar",
                                                                   0L,
                                                                   -1L,
                                                                   +2L);

    assertTrue(lBoundedNumber.get() == 0);

    lBoundedNumber.set(3L);

    assertTrue(lBoundedNumber.get() == 2);

    lBoundedNumber.set(-2L);

    assertTrue(lBoundedNumber.get() == -1);
  }

  /**
   * Test granularity
   */
  @Test
  public void testGranularity()
  {
    BoundedVariable<Double> lBoundedNumber =
                                           new BoundedVariable<Double>("doublevar",
                                                                       0.0,
                                                                       -2.0,
                                                                       +2.0,
                                                                       0.1);

    assertEquals(0, lBoundedNumber.get(), 0.01);

    lBoundedNumber.set(0.333333);

    assertEquals(0.3, lBoundedNumber.get(), 0.01);

    lBoundedNumber.set(-1.1111111);

    assertEquals(-1.1, lBoundedNumber.get(), 0.01);

    lBoundedNumber.set(-2.1111111);

    assertEquals(-2.0, lBoundedNumber.get(), 0.01);
  }

}
