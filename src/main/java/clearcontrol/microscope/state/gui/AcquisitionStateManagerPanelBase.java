package clearcontrol.microscope.state.gui.jfx;

import java.util.List;
import javafx.scene.layout.BorderPane;

import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;

/**
 * AcquisitionStateManagerPanelBase is a GUI element that displays information
 * about all acquisition states managed by a AcquisitionStateManager. This is a
 * base class offering the basic functionality for derived classes.
 * 
 * @author royer
 * @param <S>
 *          acquisition state type
 */
public abstract class AcquisitionStateManagerPanelBase<S extends AcquisitionStateInterface<?, ?>>
                                                      extends
                                                      BorderPane
{

  /**
   * Constructs an Acquisition state manager panel
   * 
   * @param pAcquisitionStateManager
   *          acquisition state manager to use.
   */
  @SuppressWarnings("unchecked")
  public AcquisitionStateManagerPanelBase(AcquisitionStateManager<S> pAcquisitionStateManager)
  {
    super();

    pAcquisitionStateManager.addChangeListener((m) -> {
      updateStateList(((AcquisitionStateManager<S>) m).getStateList());
    });

  }

  /**
   * This private method is responsible to update the list of acquisition
   * states. It should be called whenever the list of states in the manager is
   * changed.
   * 
   * @param pList
   */
  protected abstract void updateStateList(List<S> pList);

}
