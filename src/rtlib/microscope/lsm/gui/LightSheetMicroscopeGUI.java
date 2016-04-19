package rtlib.microscope.lsm.gui;

import halcyon.model.node.HalcyonNode;
import halcyon.model.node.HalcyonSwingNode;
import rtlib.hardware.signalgen.SignalGeneratorInterface;
import rtlib.hardware.signalgen.gui.swing.SignalGeneratorPanel;
import rtlib.microscope.MicroscopeBase;
import rtlib.microscope.gui.MicroscopeGUI;
import rtlib.microscope.gui.halcyon.MicroscopeNodeType;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.component.detection.DetectionArmInterface;
import rtlib.microscope.lsm.component.detection.gui.jfx.DetectionArmPanel;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;
import rtlib.microscope.lsm.component.lightsheet.gui.jfx.LightSheetPanel;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.lang.groovy.GroovyScripting;

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
		setupLightSheetInHalcyon();
		setupDetectionArmInHalcyon();
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
