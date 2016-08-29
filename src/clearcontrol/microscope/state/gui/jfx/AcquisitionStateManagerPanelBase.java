package clearcontrol.microscope.state.gui.jfx;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.gui.jfx.recycler.RecyclerPanel;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * AcquisitionStateManagerPanelBase is a GUI element that displays information
 * about all acquisition states managed by a LoggingManager. This is a
 * base class offering the basic functionality for derived classes.
 * 
 * @author royer
 */
public abstract class AcquisitionStateManagerPanelBase extends
																											VBox
{

	/**
	 * Constructs a {@link InteractiveAcquisitionPanel} given a
	 * {@link StackRecyclerManager}.
	 * 
	 * @param pStackRecyclerManager
	 *          {@link StackRecyclerManager} to use.
	 */
	public AcquisitionStateManagerPanelBase(AcquisitionStateManager pAcquisitionStateManager)
	{
		super();

		pAcquisitionStateManager.addChangeListener((m) -> {
			updateStateList(((AcquisitionStateManager)m).getStateList());
		});

	}

	/**
	 * This private method is responsible to update the list of acquisition
	 * states. It should be called whenever the list of states in the manager is
	 * changed.
	 * 
	 * @param pList
	 */
	protected abstract void updateStateList(List<AcquisitionStateInterface<?>> pList);

}
