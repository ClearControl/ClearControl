package clearcontrol.microscope.lightsheet.acquisition.gui.jfx.cell;

import clearcontrol.device.name.NameableInterface;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class SingleCheckListView<T extends NameableInterface> extends
																															ListView<T>
{

	public SingleCheckListView(SingleCheckCellManager<T> pSingleCheckCellManager)
	{
		this(	pSingleCheckCellManager,
					FXCollections.<T> observableArrayList());
	}

	public SingleCheckListView(	SingleCheckCellManager<T> pSingleCheckCellManager,
															ObservableList<T> pItems)
	{
		super(pItems);
		final SingleCheckListView<T> lSingleCheckListView = this;

		this.setCellFactory(new Callback<ListView<T>, ListCell<T>>()
		{
			@Override
			public ListCell<T> call(ListView<T> param)
			{
				SingleCheckCell<T> lCell = new SingleCheckCell<T>(lSingleCheckListView,
																													pSingleCheckCellManager);
				pSingleCheckCellManager.addCell(lCell);
				return lCell;
			}
		});/**/
	}

}
