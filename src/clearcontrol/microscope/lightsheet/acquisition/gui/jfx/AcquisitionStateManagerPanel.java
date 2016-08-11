package clearcontrol.microscope.lightsheet.acquisition.gui.jfx;

import java.util.ArrayList;
import java.util.List;

import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.cell.SingleCheckCell;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.cell.SingleCheckCellManager;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.cell.SingleCheckListView;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.state.gui.jfx.AcquisitionStateManagerPanelBase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * AcquisitionStateManagerPanel is a GUI element that displays information about
 * all acquisition states managed by a AcquisitionStateManager.
 * 
 * @author royer
 */
public class AcquisitionStateManagerPanel	extends
																					AcquisitionStateManagerPanelBase
{
	private AcquisitionStateManager mAcquisitionStateManager;

	private SingleCheckListView<AcquisitionStateInterface<?>> mStateListView;
	private ObservableList<AcquisitionStateInterface<?>> mObservableStateList = FXCollections.observableArrayList();
	private Button mNewButton, mDuplicateButton, mDeleteButton;

	public AcquisitionStateManagerPanel(AcquisitionStateManager pAcquisitionStateManager)
	{
		super(pAcquisitionStateManager);
		mAcquisitionStateManager = pAcquisitionStateManager;
		AcquisitionStateManagerPanel lAcquisitionStateManagerPanel = this;

		getColumnConstraints().add(new ColumnConstraints());
		getColumnConstraints().add(new ColumnConstraints(100));

		SingleCheckCellManager<AcquisitionStateInterface<?>> lManager = new SingleCheckCellManager<AcquisitionStateInterface<?>>()
		{

			@Override
			public void checkOnly(SingleCheckCell<AcquisitionStateInterface<?>> pSelectedCell)
			{
				super.checkOnly(pSelectedCell);

				AcquisitionStateInterface<?> lState = pSelectedCell.getItem();

				lAcquisitionStateManagerPanel.setCurrent(lState);
			}

		};

		mStateListView = new SingleCheckListView<AcquisitionStateInterface<?>>(lManager);
		mStateListView.setItems(mObservableStateList);

		add(mStateListView, 0, 0);
		GridPane.setRowSpan(mStateListView, 6);

		mNewButton = new Button("New");
		mNewButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setHgrow(mNewButton, Priority.ALWAYS);
		GridPane.setFillWidth(mNewButton, true);
		add(mNewButton, 1, 0);

		mDuplicateButton = new Button("Duplicate");
		mDuplicateButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setHgrow(mDuplicateButton, Priority.ALWAYS);
		GridPane.setFillWidth(mDuplicateButton, true);
		add(mDuplicateButton, 1, 1);

		mDeleteButton = new Button("Delete");
		mDeleteButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setHgrow(mDeleteButton, Priority.ALWAYS);
		GridPane.setFillWidth(mDeleteButton, true);
		add(mDeleteButton, 1, 2);

		LightSheetMicroscopeInterface lMicroscope = (LightSheetMicroscopeInterface) pAcquisitionStateManager.getMicroscope();

		mNewButton.setOnAction((e) -> {

			InterpolatedAcquisitionState lInterpolatedAcquisitionState = new InterpolatedAcquisitionState("new",
																																																		lMicroscope);
			pAcquisitionStateManager.addState(lInterpolatedAcquisitionState);

		});

		mDuplicateButton.setOnAction((e) -> {

			AcquisitionStateInterface<?> lSelectedItem = mStateListView.getSelectionModel()
																																	.getSelectedItem();

			if (lSelectedItem instanceof InterpolatedAcquisitionState)
			{
				String lNewName = lSelectedItem.getName() + "’";
				InterpolatedAcquisitionState lOriginalState = (InterpolatedAcquisitionState) lSelectedItem;
				InterpolatedAcquisitionState lInterpolatedAcquisitionState = new InterpolatedAcquisitionState(lNewName,
																																																			lOriginalState);
				pAcquisitionStateManager.addState(lInterpolatedAcquisitionState);

			}
		});

		mDeleteButton.setOnAction((e) -> {

			AcquisitionStateInterface<?> lSelectedItem = mStateListView.getSelectionModel()
																																	.getSelectedItem();
			pAcquisitionStateManager.removeState(lSelectedItem);
		});

	}

	protected void updateStateList(List<AcquisitionStateInterface<?>> pStateList)
	{
		Runnable lRunnable = () -> {
			for (AcquisitionStateInterface<?> lState : pStateList)
			{
				if (!mObservableStateList.contains(lState))
				{
					mObservableStateList.add(lState);
				}
			}

			ArrayList<AcquisitionStateInterface<?>> lRemovalList = new ArrayList<>();
			for (AcquisitionStateInterface<?> lAcquisitionState : mObservableStateList)
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

	public void setCurrent(AcquisitionStateInterface<?> pState)
	{
		mAcquisitionStateManager.setCurrent(pState);
	}

}
