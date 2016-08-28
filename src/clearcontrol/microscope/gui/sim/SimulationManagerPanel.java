package clearcontrol.microscope.gui.sim;

import clearcontrol.gui.jfx.customvarpanel.CustomVariablePane;
import clearcontrol.microscope.sim.SimulationManager;

public class SimulationManagerPanel extends CustomVariablePane
{

	public SimulationManagerPanel(SimulationManager pSimulationManager)
	{
		super();

		addTab("Logging");
		
		addToggleButton("Logging On","Logging Off", pSimulationManager.getLoggingOnVariable());

	}



}
