package clearcontrol.core.math.interpolation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import clearcontrol.core.math.interpolation.Row;
import clearcontrol.core.math.interpolation.SplineInterpolationTable;

public class InterpolationTableTests
{

	@Test
	public void test()
	{
		final SplineInterpolationTable lInterpolationTable = new SplineInterpolationTable(2);

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

		/*final MultiPlot lDisplayTable = lInterpolationTable.displayTable("test");

		while (lDisplayTable.isVisible())
		{
			ThreadUtils.sleep(10L, TimeUnit.MILLISECONDS);
		}/**/

		assertEquals(	5.4,
									lInterpolationTable.getInterpolatedValue(0, 4),
									0.03);

		assertEquals(	-2,
									lInterpolationTable.getInterpolatedValue(1, 4),
									0.03);

	}
}
