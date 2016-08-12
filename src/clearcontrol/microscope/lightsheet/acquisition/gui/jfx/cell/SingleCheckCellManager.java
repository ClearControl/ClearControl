package clearcontrol.microscope.lightsheet.acquisition.gui.jfx.cell;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import clearcontrol.device.name.NameableInterface;
import javafx.application.Platform;

public class SingleCheckCellManager<T extends NameableInterface>
{
	private CopyOnWriteArrayList<SingleCheckCell<T>> mCellList = new CopyOnWriteArrayList<>();

	private volatile T mCheckedItem;

	public void addCell(SingleCheckCell<T> pCell)
	{
		mCellList.add(pCell);
	}

	public void checkOnly(SingleCheckCell<T> pCheckedCell)
	{
		if (!mCellList.contains(pCheckedCell))
			mCellList.add(pCheckedCell);
		mCheckedItem = pCheckedCell.getItem();
		updateChecked();
	}

	public void updateChecked()
	{

		// System.out.println("______________________");
		ArrayList<SingleCheckCell<T>> lRemovalList = new ArrayList<>();
		for (SingleCheckCell<T> lCell : mCellList)
			if (mCheckedItem != null && lCell.getItem() == null)
			{
				// System.out.println(lCell + "->removed");
				lRemovalList.add(lCell);

				// System.out.println(lCell.getItem() + "->false");
				lCell.setChecked(false);

				continue;
			}
			else if (lCell.getItem() == null)
			{
				// System.out.println(lCell + "->null");
				continue;
			}
			else if (lCell.getItem() == mCheckedItem)
			{

				// System.out.println(lCell.getItem().getName() + "->true");
				lCell.setChecked(true);
			}
			else
			{

				// System.out.println(lCell.getItem().getName() + "->false");
				lCell.setChecked(false);
			}
		// mCellList.removeAll(lRemovalList);

	}

}
