package clearcontrol.microscope.state.gui.jfx;

import java.util.List;
import javafx.scene.layout.VBox;

import clearcontrol.microscope.lightsheet.interactive.gui.jfx.InteractiveAcquisitionPanel;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;

/**
 * AcquisitionStateManagerPanelBase is a GUI element that displays information
 * about all acquisition states managed by a LoggingManager. This is a base
 * class offering the basic functionality for derived classes.
 * 
 * @author royer
 */
public abstract class AcquisitionStateManagerPanelBase extends VBox
{

  /**
   * Constructs a {@link InteractiveAcquisitionPanel} given a
   * {@link StackRecyclerManager}.
   * 
   * @param pAcquisitionStateManager
   *          {@link StackRecyclerManager} to use.
   */
  public AcquisitionStateManagerPanelBase(AcquisitionStateManager pAcquisitionStateManager)
  {
    super();

    pAcquisitionStateManager.addChangeListener((m) -> {
      updateStateList(((AcquisitionStateManager) m).getStateList());
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
