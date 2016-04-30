package clearcontrol.microscope.lightsheet.gui;

import clearcontrol.microscope.gui.MicroscopeGUI;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.gui.StartStopToolbarWindow;
import clearcontrol.microscope.lightsheet.acquisition.interactive.InteractiveAcquisition;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.detection.gui.jfx.DetectionArmPanel;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.gui.jfx.LightSheetPanel;
import halcyon.model.node.HalcyonNode;
import javafx.scene.layout.GridPane;

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
