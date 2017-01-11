package clearcontrol.microscope.lightsheet.gui;

import clearcontrol.microscope.gui.MicroscopeGUI;
import clearcontrol.microscope.gui.halcyon.MicroscopeNodeType;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.AcquisitionStateManagerPanel;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.gui.jfx.CalibratorToolbar;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.detection.gui.jfx.DetectionArmPanel;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.gui.jfx.LightSheetPanel;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import clearcontrol.microscope.lightsheet.interactive.gui.jfx.InteractiveAcquisitionPanel;
import clearcontrol.microscope.lightsheet.interactive.gui.jfx.InteractiveAcquisitionToolbar;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import clearcontrol.microscope.timelapse.gui.jfx.TimelapsePanel;
import clearcontrol.microscope.timelapse.gui.jfx.TimelapseToolbar;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;

public class LightSheetMicroscopeGUI extends MicroscopeGUI
{

	public LightSheetMicroscopeGUI(	LightSheetMicroscope pLightSheetMicroscope,
																	boolean p2DDisplay,
																	boolean p3DDisplay)
	{
		super(pLightSheetMicroscope,
					LSMNodeType.values(),
					p2DDisplay,
					p3DDisplay);

		addPanelMappingEntry(	LightSheetInterface.class,
														LightSheetPanel.class,
														LSMNodeType.LightSheet);

		addPanelMappingEntry(	DetectionArmInterface.class,
														DetectionArmPanel.class,
														LSMNodeType.DetectionArm);

		addPanelMappingEntry(	InteractiveAcquisition.class,
														InteractiveAcquisitionPanel.class,
														MicroscopeNodeType.Acquisition);

		addPanelMappingEntry(	AcquisitionStateManager.class,
														AcquisitionStateManagerPanel.class,
														MicroscopeNodeType.Acquisition);

		addPanelMappingEntry(	TimelapseTimerInterface.class,
														TimelapsePanel.class,
														MicroscopeNodeType.Acquisition);

		/*addHalcyonMappingEntry(	AutoPilotInterface.class,
														AutoPilotPanel.class,
														MicroscopeNodeType.Acquisition);/**/

		addToolbarMappingEntry( InteractiveAcquisition.class, InteractiveAcquisitionToolbar.class);
		
		addToolbarMappingEntry( Calibrator.class, CalibratorToolbar.class);
		
		addToolbarMappingEntry( TimelapseInterface.class, TimelapseToolbar.class);
		
	}

	@Override
	public void generate()
	{
		super.generate();
	}

  

}
