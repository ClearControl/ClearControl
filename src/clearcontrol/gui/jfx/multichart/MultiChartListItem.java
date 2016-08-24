package clearcontrol.gui.jfx.multichart;

import javafx.scene.chart.XYChart.Series;

/**
 * MultiChartListItem wraps a series and a label for it.
 * 
 * @author royer
 */
public class MultiChartListItem
{
	private final String mName;
	private final Series<Number, Number> mSeries;

	public MultiChartListItem(String pName, Series<Number, Number> pItem)
	{
		super();
		mName = pName;
		mSeries = pItem;
	}

	public Series<Number, Number> getSeries()
	{
		return mSeries;
	}

	@Override
	public String toString()
	{
		return mName;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ ((mSeries == null) ? 0 : mSeries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiChartListItem other = (MultiChartListItem) obj;
		if (mSeries == null)
		{
			if (other.mSeries != null)
				return false;
		}
		else if (!mSeries.equals(other.mSeries))
			return false;
		return true;
	}

}
