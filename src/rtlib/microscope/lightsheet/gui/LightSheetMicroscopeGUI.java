package rtlib.microscope.lightsheet.gui;

import halcyon.model.node.HalcyonNode;
import javafx.scene.layout.GridPane;
import rtlib.microscope.gui.MicroscopeGUI;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.acquisition.gui.StartStopToolbarWindow;
import rtlib.microscope.lightsheet.acquisition.interactive.InteractiveAcquisition;
import rtlib.microscope.lightsheet.component.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.component.detection.gui.jfx.DetectionArmPanel;
import rtlib.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import rtlib.microscope.lightsheet.component.lightsheet.gui.jfx.LightSheetPanel;

public class LightSheetMicroscopeGUI extends MicroscopeGUI
{

	public LightSheetMicroscopeGUI(	LightSheetMicroscope pLightSheetMicroscope,
																	boolean p3DView)
	{
		super(pLightSheetMicroscope, p3DView);
	}

	public void generate()
	{
		super.generate();
		setupToolBar();
		setupLightSheetInHalcyon();
		setupDetectionArmInHalcyon();
	}

	private void setupToolBar()
	{
		InteractiveAcquisition lInteractiveAcquisition = ((LightSheetMicroscope)getMicroscope()).getInteractiveAcquisition();
		StartStopToolbarWindow lStartStopToolbarWindow = new StartStopToolbarWindow(lInteractiveAcquisition);
		getHalcyonFrame().addToolbar(lStartStopToolbarWindow);		
	}

	private void setupLightSheetInHalcyon()
	{
		for (LightSheetInterface lLightSheet : getMicroscope().getDeviceLists()
																													.getDevices(LightSheetInterface.class))
		{
			LightSheetPanel lLightSheetPanel = new LightSheetPanel(lLightSheet);

			HalcyonNode node = new HalcyonNode(	lLightSheet.getName(),
																					LSMNodeType.LightSheet,
																					lLightSheetPanel);
			getHalcyonFrame().addNode(node);
		}
	}

	private void setupDetectionArmInHalcyon()
	{
		for (DetectionArmInterface lDetectionArm : getMicroscope().getDeviceLists()
																															.getDevices(DetectionArmInterface.class))
		{
			DetectionArmPanel lDetetcionArmPanel = new DetectionArmPanel(lDetectionArm);

			HalcyonNode node = new HalcyonNode(	lDetectionArm.getName(),
																					LSMNodeType.DetectionArm,
																					lDetetcionArmPanel);
			getHalcyonFrame().addNode(node);
		}
	}

}
