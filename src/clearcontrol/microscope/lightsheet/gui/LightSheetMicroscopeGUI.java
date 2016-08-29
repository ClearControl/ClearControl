package clearcontrol.microscope.lightsheet.gui;

import clearcontrol.microscope.gui.MicroscopeGUI;
import clearcontrol.microscope.gui.halcyon.MicroscopeNodeType;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.AcquisitionStateManagerPanel;
import clearcontrol.microscope.lightsheet.autopilot.AutoPilotInterface;
import clearcontrol.microscope.lightsheet.autopilot.gui.jfx.AutoPilotPanel;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.gui.jfx.CalibratorToolbar;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.detection.gui.jfx.DetectionArmPanel;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.gui.jfx.LightSheetPanel;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import clearcontrol.microscope.lightsheet.interactive.gui.jfx.InteractiveAcquisitionPanel;
import clearcontrol.microscope.lightsheet.interactive.gui.jfx.InteractiveAcquisitionToolbar;
import clearcontrol.microscope.lightsheet.timelapse.TimelapseInterface;
import clearcontrol.microscope.lightsheet.timelapse.gui.jfx.TimelapsePanel;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.stacks.gui.jfx.StackRecyclerManagerPanel;
import clearcontrol.microscope.state.AcquisitionStateManager;
import halcyon.model.node.HalcyonNode;

public class LightSheetMicroscopeGUI extends MicroscopeGUI
{

	public LightSheetMicroscopeGUI(	LightSheetMicroscope pLightSheetMicroscope,
																	boolean p2DDisplay, boolean p3DDisplay)
	{
		super(pLightSheetMicroscope, LSMNodeType.values(), p2DDisplay,p3DDisplay);

		addHalcyonMappingEntry(	LightSheetInterface.class,
														LightSheetPanel.class,
														LSMNodeType.LightSheet);

		addHalcyonMappingEntry(	DetectionArmInterface.class,
														DetectionArmPanel.class,
														LSMNodeType.DetectionArm);
		
		addHalcyonMappingEntry(	InteractiveAcquisition.class,
		                       	InteractiveAcquisitionPanel.class,
														MicroscopeNodeType.Acquisition);

		addHalcyonMappingEntry(	AcquisitionStateManager.class,
														AcquisitionStateManagerPanel.class,
														MicroscopeNodeType.Acquisition);

		addHalcyonMappingEntry(	TimelapseInterface.class,
														TimelapsePanel.class,
														MicroscopeNodeType.Acquisition);

		addHalcyonMappingEntry(	AutoPilotInterface.class,
														AutoPilotPanel.class,
														MicroscopeNodeType.Acquisition);

	}

	public void generate()
	{
		super.generate();
		setupToolBars();
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

}
