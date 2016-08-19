package clearcontrol.gui.jfx.singlechecklist;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import clearcontrol.core.collection.weak.WeakArrayList;
import clearcontrol.device.name.NameableInterface;
import javafx.application.Platform;

public class SingleCheckCellManager<T extends NameableInterface>
{
	private WeakArrayList<SingleCheckCell<T>> mCellList = new WeakArrayList<>();

	private volatile T mCheckedItem;

	public void addCell(SingleCheckCell<T> pCell)
	{
		mCellList.add(pCell);
	}

	public void checkOnlyCell(SingleCheckCell<T> pCheckedCell)
	{
		if (!mCellList.contains(pCheckedCell))
			mCellList.add(pCheckedCell);
		mCheckedItem = pCheckedCell.getItem();
		updateChecked();
	}

	public void checkOnlyItem(T pCheckedItem)
	{
		mCheckedItem = pCheckedItem;
		for (SingleCheckCell<T> lCell : mCellList)
			if (lCell.getItem() != null && lCell.getItem() == mCheckedItem)
			{
				lCell.setChecked(true);
			}
			else if (lCell.getItem() != null && lCell.getItem() != mCheckedItem)
			{
				lCell.setChecked(false);
			}
			else
			{
				continue;
			}

	}

	public void updateChecked()
	{

		for (SingleCheckCell<T> lCell : mCellList)
			if (mCheckedItem != null && lCell.getItem() == null)
			{
				lCell.setChecked(false);
				continue;
			}
			else if (lCell.getItem() == null)
			{
				continue;
			}
			else if (lCell.getItem() == mCheckedItem)
			{
				lCell.setChecked(true);
			}
			else
			{
				lCell.setChecked(false);
			}

	}

}
