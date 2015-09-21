package rtlib.microscope.lsm.acquisition.interpolation;

import gnu.trove.list.array.TDoubleArrayList;

public class Row implements Comparable<Row>
{
	final double x;
	private final TDoubleArrayList y;
	private volatile boolean mIsUpToDate = false;

	public Row(int pNumberOfColumns, double pX)
	{
		x = pX;

		if (pNumberOfColumns > 0)
		{
			y = new TDoubleArrayList();
			for (int i = 0; i < pNumberOfColumns; i++)
				y.add(0);
		}
		else
			y = null;

		System.out.println(y);
	}

	public int getNumberOfColumns()
	{
		return y.size();
	}

	public double getX()
	{
		return x;
	}

	public double getY(int pColumnIndex)
	{
		return y.get(pColumnIndex);
	}

	public void setY(int pColumnIndex, double pValue)
	{
		y.set(pColumnIndex, pValue);
		mIsUpToDate = false;
	}

	public void addY(int pColumnIndex, double pDelta)
	{
		y.set(pColumnIndex, y.get(pColumnIndex) + pDelta);
		mIsUpToDate = false;
	}

	public boolean isUpToDate()
	{
		return mIsUpToDate;
	}

	public void setUpToDate(boolean pIsUpToDate)
	{
		mIsUpToDate = pIsUpToDate;
	}

	@Override
	public int compareTo(Row pRow)
	{
		if (getX() > pRow.getX())
			return 1;
		else if (getX() < pRow.getX())
			return -1;
		return 0;
	}

	@Override
	public String toString()
	{
		return "Row [x=" + x
				+ ", y="
				+ y
				+ ", mIsUpToDate="
				+ mIsUpToDate
				+ "]";
	}


	
	

}