package rtlib.microscope.lightsheet.acquisition.interpolation;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import gnu.trove.list.array.TDoubleArrayList;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;

public class InterpolationTable
{
	private final TreeSet<Row> mTable;
	private final ArrayList<UnivariateFunction> mInterpolatingFunctionsList;
	private final int mNumberOfColumns;
	private volatile boolean mIsUpToDate = false;

	public InterpolationTable(int pNumberOfColumns)
	{
		super();
		mTable = new TreeSet<>();
		mInterpolatingFunctionsList = new ArrayList<>();
		mNumberOfColumns = pNumberOfColumns;
	}

	public InterpolationTable(InterpolationTable pInterpolationTable)
	{
		this(pInterpolationTable.mNumberOfColumns);
		for (Row lRow : pInterpolationTable.mTable)
			mTable.add(new Row(lRow));
		mIsUpToDate = false;
	}

	public int getNumberOfRows()
	{
		return mTable.size();
	}

	public int getNumberOfColumns()
	{
		return mTable.first().getNumberOfColumns();
	}

	public Row getRow(int pRowIndex)
	{
		Iterator<Row> lIterator = mTable.iterator();

		for (int i = 0; i < pRowIndex && lIterator.hasNext(); i++)
			lIterator.next();

		return lIterator.next();
	}

	public Row getNearestRow(double pX)
	{
		final Row lCeiling = mTable.ceiling(new Row(0, pX));
		final Row lFloor = mTable.floor(new Row(0, pX));

		if (abs(lCeiling.getX() - pX) < abs(lFloor.getX() - pX))
			return lCeiling;
		else
			return lFloor;
	}

	public Row getCeilRow(double pX)
	{
		final Row lCeiling = mTable.ceiling(new Row(0, pX));
		return lCeiling;
	}

	public Row getFloorRow(double pX)
	{
		final Row lFloor = mTable.floor(new Row(0, pX));
		return lFloor;
	}

	public Row addRow(double pX)
	{
		final Row lRow = new Row(mNumberOfColumns, pX);
		mTable.add(lRow);
		mIsUpToDate = false;
		return lRow;
	}

	public double getMaxX()
	{
		return mTable.last().x;
	}

	public double getMinX()
	{
		return mTable.first().x;
	}

	public boolean isIsUpToDate()
	{
		boolean lIsUpToDate = mIsUpToDate;
		for (final Row lRow : mTable)
		{
			lIsUpToDate &= lRow.isUpToDate();
		}
		return lIsUpToDate;
	}

	private void setUpToDate()
	{
		mIsUpToDate = true;
		for (final Row lRow : mTable)
		{
			lRow.setUpToDate(true);
		}
	}

	private void ensureIsUpToDate()
	{
		if (!isIsUpToDate())
		{
			final UnivariateInterpolator lUnivariateInterpolator = new SplineInterpolator();
			/*new LoessInterpolator(	max(0.25 * mTable.size(),
																																										3) / mTable.size(),
																																								0);/**/
			mInterpolatingFunctionsList.clear();

			final TDoubleArrayList x = new TDoubleArrayList();
			final TDoubleArrayList y = new TDoubleArrayList();

			for (int i = 0; i < mNumberOfColumns; i++)
			{
				x.clear();
				y.clear();
				for (final Row lRow : mTable)
				{
					double lValueY = lRow.getY(i);

					if (!Double.isNaN(lValueY))
					{
						y.add(lValueY);
						x.add(lRow.getX());
					}
				}

				final double lMinX = getMinX();
				final double lMaxX = getMaxX();
				final double lRangeWidth = abs(lMaxX - lMinX);

				if (x.size() >= 2)
				{
					x.insert(0, x.get(0) - lRangeWidth);
					x.add(x.get(x.size() - 1) + lRangeWidth);
				}

				if (x.size() >= 2)
				{
					y.insert(0, y.get(0));
					y.add(y.get(y.size() - 1));

					y.set(0, y.get(1) - lRangeWidth
										* ((y.get(1) - y.get(2)) / (x.get(1) - x.get(2))));
					y.set(y.size() - 1,
								y.get(y.size() - 2) + lRangeWidth
										* ((y.get(y.size() - 2) - y.get(y.size() - 3)) / (x.get(y.size() - 2) - x.get(y.size() - 3))));
				}

				final UnivariateFunction lUnivariateFunction;

				if (x.size() == 0)
				{
					lUnivariateFunction = new UnivariateAffineFunction(0, 0);
				}
				else if (x.size() <= 1)
				{
					lUnivariateFunction = new UnivariateAffineFunction(	0,
																															x.get(0));
				}
				else
				{

					lUnivariateFunction = lUnivariateInterpolator.interpolate(x.toArray(),
																																		y.toArray());
				}
				mInterpolatingFunctionsList.add(lUnivariateFunction);

			}

			setUpToDate();
		}
	}

	public double getNearestValue(int pColumnIndex, double pX)
	{
		return getNearestRow(pX).getY(pColumnIndex);
	}

	public double getCeil(int pColumnIndex, double pX)
	{
		return getNearestRow(pX).getY(pColumnIndex);
	}

	public double getInterpolatedValue(int pColumnIndex, double pX)
	{
		ensureIsUpToDate();

		final UnivariateFunction lUnivariateFunction = mInterpolatingFunctionsList.get(pColumnIndex);
		final double lValue = lUnivariateFunction.value(pX);
		return lValue;
	}

	public MultiPlot displayTable(String pMultiPlotName)
	{
		final MultiPlot lMultiPlot = MultiPlot.getMultiPlot(pMultiPlotName);

		for (int i = 0; i < mNumberOfColumns; i++)
		{
			final PlotTab lPlot = lMultiPlot.getPlot("Column" + i);
			lPlot.setLinePlot("interpolated");
			lPlot.setScatterPlot("samples");

			for (final Row lRow : mTable)
			{
				final double x = lRow.x;
				final double y = lRow.getY(i);

				lPlot.addPoint("samples", x, y);
			}

			final double lMinX = getMinX();
			final double lMaxX = getMaxX();
			final double lRangeWidth = lMaxX - lMinX;
			final double lStep = (lMaxX - lMinX) / 1024;

			for (double x = lMinX - 0.1 * lRangeWidth; x <= lMaxX + 0.1
																											* lRangeWidth; x += lStep)
			{
				final double y = getInterpolatedValue(i, x);
				lPlot.addPoint("interpolated", x, y);
			}

			lPlot.ensureUpToDate();

		}

		return lMultiPlot;
	}

}
