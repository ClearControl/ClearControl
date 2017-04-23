package clearcontrol.microscope.simulation.gui;

import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.gui.jfx.var.togglebutton.VariableToggleButton;
import clearcontrol.microscope.simulation.SimulationManager;

/**
 * Simulation manager panel
 *
 * @author royer
 */
public class SimulationManagerPanel extends CustomVariablePane
{

  /**
   * Instanciates a simulation manager panel.
   * 
   * @param pSimulationManager
   *          simulation manager
   */
  public SimulationManagerPanel(SimulationManager pSimulationManager)
  {
    super();

    addTab("Logging");

    VariableToggleButton lToggleButton =
                                       addToggleButton("Logging On",
                                                       "Logging Off",
                                                       pSimulationManager.getLoggingOnVariable());

    lToggleButton.setMinWidth(250);

  }

}
