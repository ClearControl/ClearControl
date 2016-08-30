package clearcontrol.microscope.lightsheet.interactive.gui.jfx;

import java.util.ArrayList;
import java.util.List;

import clearcontrol.gui.jfx.customvarpanel.CustomVariablePane;
import clearcontrol.gui.jfx.singlechecklist.SingleCheckCell;
import clearcontrol.gui.jfx.singlechecklist.SingleCheckCellManager;
import clearcontrol.gui.jfx.singlechecklist.SingleCheckListView;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.state.gui.jfx.AcquisitionStateManagerPanelBase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * InteractiveAcquisitionPanel is a GUI element that displays information
 * about the 2D and 3D interactive acquisition.
 * 
 * @author royer
 */
public class InteractiveAcquisitionPanel extends CustomVariablePane

{

	private InteractiveAcquisition mInteractiveAcquisition;

	public InteractiveAcquisitionPanel(InteractiveAcquisition pInteractiveAcquisition)
	{
		super();
		mInteractiveAcquisition = pInteractiveAcquisition;
		
		addTab("DOFs");

		addSliderForVariable(	"Z :",
		                     	mInteractiveAcquisition.get2DAcquisitionZVariable(),
													10.0).setUpdateIfChanging(true);

		
	}

	
	
	
}
