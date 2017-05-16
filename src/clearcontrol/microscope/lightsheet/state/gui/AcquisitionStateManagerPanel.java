package clearcontrol.microscope.lightsheet.state.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import clearcontrol.gui.jfx.custom.singlechecklist.SingleCheckCell;
import clearcontrol.gui.jfx.custom.singlechecklist.SingleCheckCellManager;
import clearcontrol.gui.jfx.custom.singlechecklist.SingleCheckListView;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.state.gui.jfx.AcquisitionStateManagerPanelBase;

/**
 * Interactive2DAcquisitionPanel is a GUI element that displays information
 * about all acquisition states managed by a LoggingManager.
 * 
 * @author royer
 */
public class AcquisitionStateManagerPanel extends
                                          AcquisitionStateManagerPanelBase<InterpolatedAcquisitionState>
{
  private AcquisitionStateManager<InterpolatedAcquisitionState> mAcquisitionStateManager;

  private SingleCheckListView<InterpolatedAcquisitionState> mStateListView;
  private ObservableList<InterpolatedAcquisitionState> mObservableStateList =
                                                                            FXCollections.observableArrayList();
  private VBox mStateViewVBox;

  /**
   * Instantiates an acquisition state manager panel
   * 
   * @param pAcquisitionStateManager
   *          acquisition state manager
   */
  public AcquisitionStateManagerPanel(AcquisitionStateManager<InterpolatedAcquisitionState> pAcquisitionStateManager)
  {
    super(pAcquisitionStateManager);
    mAcquisitionStateManager = pAcquisitionStateManager;
    AcquisitionStateManagerPanel lAcquisitionStateManagerPanel = this;

    SingleCheckCellManager<InterpolatedAcquisitionState> lSingleCheckCellManager =
                                                                                 new SingleCheckCellManager<InterpolatedAcquisitionState>()
                                                                                 {
                                                                                   @Override
                                                                                   public void checkOnlyCell(SingleCheckCell<InterpolatedAcquisitionState> pSelectedCell)
                                                                                   {
                                                                                     super.checkOnlyCell(pSelectedCell);
                                                                                     InterpolatedAcquisitionState lState =
                                                                                                                         pSelectedCell.getItem();
                                                                                     lAcquisitionStateManagerPanel.setCurrentState(lState);
                                                                                   }
                                                                                 };

    mStateListView =
                   new SingleCheckListView<>(lSingleCheckCellManager,
                                             mObservableStateList);

    mStateListView.getSelectionModel()
                  .getSelectedItems()
                  .addListener(new ListChangeListener<AcquisitionStateInterface<?, ?>>()
                  {
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends AcquisitionStateInterface<?, ?>> change)
                    {
                      AcquisitionStateInterface<?, ?> lAcquisitionStateInterface =
                                                                                 change.getList()
                                                                                       .get(0);
                      setViewedAcquisitionState(lAcquisitionStateInterface);
                    }

                  });

    ContextMenu contextMenu = new ContextMenu();
    mStateListView.setContextMenu(contextMenu);

    MenuItem lNewItem = new MenuItem("New");
    MenuItem lDuplicateItem = new MenuItem("Duplicate");
    MenuItem lDeleteItem = new MenuItem("Delete");
    MenuItem lDeleteOthersItem = new MenuItem("Delete others");

    contextMenu.getItems().addAll(lNewItem,
                                  lDuplicateItem,
                                  lDeleteItem,
                                  lDeleteOthersItem);

    mStateViewVBox = new VBox();

    mStateListView.setPrefWidth(150);

    this.setLeft(mStateListView);
    this.setCenter(mStateViewVBox);

    LightSheetMicroscopeInterface lMicroscope =
                                              (LightSheetMicroscopeInterface) pAcquisitionStateManager.getMicroscope();

    lNewItem.setOnAction((e) -> {

      InterpolatedAcquisitionState lInterpolatedAcquisitionState =
                                                                 new InterpolatedAcquisitionState("new",
                                                                                                  lMicroscope);
      pAcquisitionStateManager.addState(lInterpolatedAcquisitionState);
      // lInterpolatedAcquisitionState.setupDefault(lMicroscope);

    });

    lDuplicateItem.setOnAction((e) -> {

      InterpolatedAcquisitionState lSelectedItem =
                                                 mStateListView.getSelectionModel()
                                                               .getSelectedItem();

      if (lSelectedItem instanceof InterpolatedAcquisitionState)
      {
        String lNewName = lSelectedItem.getName() + "’";
        InterpolatedAcquisitionState lOriginalState = lSelectedItem;
        InterpolatedAcquisitionState lInterpolatedAcquisitionState =
                                                                   new InterpolatedAcquisitionState(lNewName,
                                                                                                    lOriginalState);
        pAcquisitionStateManager.addState(lInterpolatedAcquisitionState);

      }
    });

    lDeleteItem.setOnAction((e) -> {

      InterpolatedAcquisitionState lSelectedItem =
                                                 mStateListView.getSelectionModel()
                                                               .getSelectedItem();
      pAcquisitionStateManager.removeState(lSelectedItem);
    });

    lDeleteOthersItem.setOnAction((e) -> {

      InterpolatedAcquisitionState lSelectedItem =
                                                 mStateListView.getSelectionModel()
                                                               .getSelectedItem();
      pAcquisitionStateManager.removeOtherStates(lSelectedItem);
    });

    mAcquisitionStateManager.addChangeListener((e) -> {
      Platform.runLater(() -> {
        updateStateList(pAcquisitionStateManager.getStateList());
        mStateListView.checkOnly(pAcquisitionStateManager.getCurrentState());
      });
    });

    Platform.runLater(() -> {
      updateStateList(pAcquisitionStateManager.getStateList());
      setCurrentState(pAcquisitionStateManager.getCurrentState());
      setViewedAcquisitionState(pAcquisitionStateManager.getCurrentState());
    });

  }

  @Override
  protected void updateStateList(List<InterpolatedAcquisitionState> pStateList)
  {
    Runnable lRunnable = () -> {
      for (InterpolatedAcquisitionState lState : pStateList)
      {
        if (!mObservableStateList.contains(lState))
        {
          mObservableStateList.add(lState);
        }
      }

      ArrayList<AcquisitionStateInterface<?, ?>> lRemovalList =
                                                              new ArrayList<>();
      for (AcquisitionStateInterface<?, ?> lAcquisitionState : mObservableStateList)
      {
        if (!pStateList.contains(lAcquisitionState))
        {
          lRemovalList.add(lAcquisitionState);
        }
      }
      mObservableStateList.removeAll(lRemovalList);
    };

    if (Platform.isFxApplicationThread())
      lRunnable.run();
    else
      Platform.runLater(lRunnable);
  }

  /**
   * Sets current acquisition state
   * 
   * @param pState
   *          sate to view
   */
  public void setCurrentState(InterpolatedAcquisitionState pState)
  {
    mAcquisitionStateManager.setCurrentState(pState);
  }

  /**
   * Sets the acquisition state to view.
   * 
   * @param pState
   *          state to view
   */
  public void setViewedAcquisitionState(AcquisitionStateInterface<?, ?> pState)
  {
    Platform.runLater(() -> {

      if (pState instanceof InterpolatedAcquisitionState)
      {
        InterpolatedAcquisitionState lInterpolatedAcquisitionState =
                                                                   (InterpolatedAcquisitionState) pState;
        AcquisitionStatePanel lAcquisitionStatePanel =
                                                     new AcquisitionStatePanel(lInterpolatedAcquisitionState);

        for (Node lNode : mStateViewVBox.getChildren())
          lNode.setVisible(false);

        mStateViewVBox.getChildren().clear();
        mStateViewVBox.getChildren().add(lAcquisitionStatePanel);
      }

    });
  }

}
