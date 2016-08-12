package clearcontrol.microscope.lightsheet.gui;

import clearcontrol.microscope.gui.MicroscopeGUI;
import clearcontrol.microscope.gui.halcyon.MicroscopeNodeType;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.autopilot.AutoPilotInterface;
import clearcontrol.microscope.lightsheet.autopilot.gui.jfx.AutoPilotPane;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.gui.jfx.CalibratorToolbar;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.detection.gui.jfx.DetectionArmPanel;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.gui.jfx.LightSheetPanel;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import clearcontrol.microscope.lightsheet.interactive.gui.jfx.InteractiveAcquisitionToolbar;
import clearcontrol.microscope.lightsheet.timelapse.TimelapseInterface;
import clearcontrol.microscope.lightsheet.timelapse.gui.jfx.TimelapsePane;
import halcyon.model.node.HalcyonNode;

public class LightSheetMicroscopeGUI extends MicroscopeGUI
{

	public LightSheetMicroscopeGUI(	LightSheetMicroscope pLightSheetMicroscope,
																	boolean p3DView)
	{
		super(pLightSheetMicroscope, LSMNodeType.values(), p3DView);

	}

	public void generate()
	{
		super.generate();
		setupToolBars();
		setupLightSheetInHalcyon();
		setupDetectionArmInHalcyon();
		setupTimelapseInHalcyon();
		setupAutoPilotInHalcyon();
	}

	private void setupToolBars()
	{
		InteractiveAcquisition lInteractiveAcquisition = getMicroscope().getDevice(	InteractiveAcquisition.class,
																																								0);
		if (lInteractiveAcquisition != null)
		{
			InteractiveAcquisitionToolbar lInteractiveAcquisitionToolbar = new InteractiveAcquisitionToolbar(lInteractiveAcquisition);
			getHalcyonFrame().addToolbar(lInteractiveAcquisitionToolbar);
		}

		Calibrator lCalibrator = getMicroscope().getDevice(	Calibrator.class,
																												0);

		if (lCalibrator != null)
		{
			CalibratorToolbar lCalibratorToolbar = new CalibratorToolbar(lCalibrator);
			getHalcyonFrame().addToolbar(lCalibratorToolbar);
		}
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

	private void setupTimelapseInHalcyon()
	{
		for (TimelapseInterface lTimelapseInterface : getMicroscope().getDeviceLists()
																																	.getDevices(TimelapseInterface.class))
		{
			TimelapsePane lTimelapsePane = new TimelapsePane(lTimelapseInterface);

			HalcyonNode node = new HalcyonNode(	"Timelapse",
																					MicroscopeNodeType.Acquisition,
																					lTimelapsePane);
			getHalcyonFrame().addNode(node);
		}
	}

	private void setupAutoPilotInHalcyon()
	{
		for (AutoPilotInterface lAutoPilotInterface : getMicroscope().getDeviceLists()
																																	.getDevices(AutoPilotInterface.class))
		{
			AutoPilotPane lAutoPilotPane = new AutoPilotPane(lAutoPilotInterface);

			HalcyonNode node = new HalcyonNode(	"AutoPilot",
																					MicroscopeNodeType.Acquisition,
																					lAutoPilotPane);
			getHalcyonFrame().addNode(node);
		}
	}

}
