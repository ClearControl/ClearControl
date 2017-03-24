package clearcontrol.gui.jfx.custom.singlechecklist;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import clearcontrol.core.device.name.NameableInterface;

public class SingleCheckListView<T extends NameableInterface>
                                extends ListView<T>
{

  final int cLIST_CELL_HEIGHT = 26;
  private SingleCheckCellManager<T> mSingleCheckCellManager;

  @SuppressWarnings("unused")
  private SingleCheckListView(SingleCheckCellManager<T> pSingleCheckCellManager)
  {
    this(pSingleCheckCellManager, null);

  }

  public SingleCheckListView(SingleCheckCellManager<T> pSingleCheckCellManager,
                             ObservableList<T> pItems)
  {
    super(pItems);
    mSingleCheckCellManager = pSingleCheckCellManager;

    setMinHeight(getHeight(pItems));

    final SingleCheckListView<T> lSingleCheckListView = this;

    this.setCellFactory(new Callback<ListView<T>, ListCell<T>>()
    {
      @Override
      public ListCell<T> call(ListView<T> param)
      {
        SingleCheckCell<T> lCell =
                                 new SingleCheckCell<T>(lSingleCheckListView,
                                                        pSingleCheckCellManager);
        pSingleCheckCellManager.addCell(lCell);
        return lCell;
      }
    });/**/

    /*lSingleCheckListView.prefHeightProperty()
    										.bind(Bindings.size(pItems)
    																	.multiply(cLIST_CELL_HEIGHT));/**/

    lSingleCheckListView.setPrefHeight(getHeight(pItems));

    pItems.addListener(new ListChangeListener<T>()
    {
      @Override
      public void onChanged(ListChangeListener.Change change)
      {
        int lHeight = getHeight(pItems);
        lSingleCheckListView.setPrefHeight(lHeight);
      }

    });

  }

  public void checkOnly(T pCheckedItem)
  {
    mSingleCheckCellManager.checkOnlyItem(pCheckedItem);
  }

  private int getHeight(ObservableList<T> pItems)
  {
    return (1 + pItems.size()) * cLIST_CELL_HEIGHT + 2;
  }

}
