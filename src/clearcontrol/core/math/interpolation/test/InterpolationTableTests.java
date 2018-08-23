package clearcontrol.core.math.interpolation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import clearcontrol.core.math.interpolation.LinearInterpolationTable;
import clearcontrol.core.math.interpolation.Row;
import clearcontrol.core.math.interpolation.SplineInterpolationTable;

import org.junit.Test;

/**
 * Interpolation table tests
 *
 * @author royer
 */
public class InterpolationTableTests
{
  private static double tolerance = 0.00001;

  /**
   * Test
   */
  @Test
  public void test()
  {
    final SplineInterpolationTable lInterpolationTable =
                                                       new SplineInterpolationTable(2);

    final Row lAddRow3 = lInterpolationTable.addRow(3.0);
    final Row lAddRow1 = lInterpolationTable.addRow(1.0);
    final Row lAddRow2 = lInterpolationTable.addRow(2.0);
    final Row lAddRow4 = lInterpolationTable.addRow(3.2);
    final Row lAddRow5 = lInterpolationTable.addRow(4);

    Row lRow = lInterpolationTable.getRow(1);
    // System.out.println(lRow);
    assertTrue(lRow.getX() == 2.0);

    lAddRow1.setY(0, 1);
    lAddRow2.setY(0, 2);
    lAddRow3.setY(0, Double.NaN);
    lAddRow4.setY(0, 4);
    lAddRow5.setY(0, Double.NaN);

    lAddRow1.setY(1, 0);
    lAddRow2.setY(1, 1);
    lAddRow3.setY(1, 1.1);
    lAddRow4.setY(1, 0.5);
    lAddRow5.setY(1, Double.NaN);

    System.out.println(lInterpolationTable.getInterpolatedValue(0,
                                                                1.2));

    System.out.println(lInterpolationTable.getInterpolatedValue(1,
                                                                1.2));

    /*final MultiPlot lDisplayTable =
                                  lInterpolationTable.displayTable("test");
    
    while (lDisplayTable.isVisible())
    {
      ThreadUtils.sleep(10L, TimeUnit.MILLISECONDS);
    } /**/

    assertEquals(5.47,
                 lInterpolationTable.getInterpolatedValue(0, 4),
                 0.03);

    assertEquals(-2.11,
                 lInterpolationTable.getInterpolatedValue(1, 4),
                 0.03);

  }

  @Test
  public void testSplineInterpolation()
  {
    final SplineInterpolationTable lInterpolationTable =
                                                       new SplineInterpolationTable(2);

    Row lAddRow1 = lInterpolationTable.addRow(1.0);
    lAddRow1.setY(0, 0);
    lAddRow1 = lInterpolationTable.addRow(2.0);
    lAddRow1.setY(0, 0);
    lAddRow1 = lInterpolationTable.addRow(3.0);
    lAddRow1.setY(0, 1);
    lAddRow1 = lInterpolationTable.addRow(4.0);
    lAddRow1.setY(0, 1);
    lAddRow1 = lInterpolationTable.addRow(5.0);
    lAddRow1.setY(0, 0);
    lAddRow1 = lInterpolationTable.addRow(6.0);
    lAddRow1.setY(0, 0);

    System.out.println(lInterpolationTable.getInterpolatedValue(0,
                                                                3.5));
    assertTrue(lInterpolationTable.getInterpolatedValue(0, 3.5) > 1);

  }

  @Test
  public void testLinearInterpolation()
  {
    final LinearInterpolationTable lInterpolationTable =
                                                       new LinearInterpolationTable(2);

    Row lAddRow1 = lInterpolationTable.addRow(1.0);
    lAddRow1.setY(0, 0);
    lAddRow1 = lInterpolationTable.addRow(2.0);
    lAddRow1.setY(0, 0);
    lAddRow1 = lInterpolationTable.addRow(3.0);
    lAddRow1.setY(0, 1);
    lAddRow1 = lInterpolationTable.addRow(4.0);
    lAddRow1.setY(0, 1);
    lAddRow1 = lInterpolationTable.addRow(5.0);
    lAddRow1.setY(0, 0);
    lAddRow1 = lInterpolationTable.addRow(6.0);
    lAddRow1.setY(0, 0);

    System.out.println(lInterpolationTable.getInterpolatedValue(0,
                                                                3.5));
    assertTrue(lInterpolationTable.getInterpolatedValue(0, 3.5) == 1);

    lAddRow1 = lInterpolationTable.getRow(3);
    lAddRow1.setY(0, 2);
    System.out.println(lInterpolationTable.getInterpolatedValue(0,
                                                                3.5));
    assertTrue(lInterpolationTable.getInterpolatedValue(0,
                                                        3.5) == 1.5);

    System.out.println(lInterpolationTable.getInterpolatedValue(0,
                                                                3.75));
    assertTrue(lInterpolationTable.getInterpolatedValue(0,
                                                        3.75) == 1.75);

  }

  @Test
  public void testLinearInterpolation2()
  {
    final LinearInterpolationTable lInterpolationTable =
                                                       new LinearInterpolationTable(2);

    Row lAddRow1 = lInterpolationTable.addRow(1.0);
    lAddRow1.setY(0, 1);
    lAddRow1 = lInterpolationTable.addRow(2.0);
    lAddRow1.setY(0, 0);
    lAddRow1 = lInterpolationTable.addRow(3.0);
    lAddRow1.setY(0, 0);

    assertEquals(0, lInterpolationTable.getCeil(0, 2.5), tolerance);

  }
}
