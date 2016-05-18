package clearcontrol.microscope.gui.halcyon;

import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonNode;
import halcyon.model.node.HalcyonNodeInterface;
import halcyon.model.node.HalcyonNodeType;
import halcyon.model.node.HalcyonOtherNode;
import halcyon.model.node.HalcyonSwingNode;
import halcyon.view.TreePanel;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import javafx.embed.swing.JFXPanel;

import javax.swing.SwingUtilities;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.device.switches.gui.jfx.SwitchingDevicePanel;
import clearcontrol.gui.video.video2d.Stack2DDisplay;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.hardware.cameras.gui.jfx.CameraDevicePanel;
import clearcontrol.hardware.lasers.LaserDeviceInterface;
import clearcontrol.hardware.lasers.gui.jfx.LaserDeviceGUI;
import clearcontrol.hardware.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.hardware.optomech.filterwheels.gui.jfx.FilterWheelDevicePanel;
import clearcontrol.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import clearcontrol.hardware.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.hardware.signalamp.gui.ScalingAmplifierPanel;
import clearcontrol.hardware.signalgen.SignalGeneratorInterface;
import clearcontrol.hardware.signalgen.gui.swing.SignalGeneratorPanel;
import clearcontrol.hardware.stages.StageDeviceInterface;
import clearcontrol.hardware.stages.gui.jfx.StageDeviceGUI;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.gui.MicroscopeGUI;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.gui.ScriptingWindow;

public class HalcyonGUIGenerator
{
	private MicroscopeInterface mMicroscopeInterface;
	private HalcyonFrame mHalcyonFrame;
	private MicroscopeGUI mMicroscopeGUI;

	public HalcyonGUIGenerator(	MicroscopeInterface pMicroscopeInterface,
															MicroscopeGUI pMicroscopeGUI,
															Collection<HalcyonNodeType> pNodeTypeNamesList)
	{
		mMicroscopeInterface = pMicroscopeInterface;
		mMicroscopeGUI = pMicroscopeGUI;
		initJavaFX();

		TreePanel lTreePanel = new TreePanel(	"component tree",
																					"Hardware",
																					this.getClass()
																							.getResourceAsStream("./icons/folder_16.png"),
																					pNodeTypeNamesList);

		mHalcyonFrame = new HalcyonFrame(1280, 768);

		mHalcyonFrame.setTreeDockNode(lTreePanel);

	}

	public HalcyonFrame getHalcyonFrame()
	{
		return mHalcyonFrame;
	}

	public void setupDeviceGUIs()
	{

		// Setting up devices:
		setupLasers();
		setupOpticalSwitches();
		setupFilterWheels();
		setupStages();
		setupCameras();
		setupSignalGenerators();
		setupScalingAmplifiers();

		// setting up script engines:
		setupScriptEngines(mMicroscopeInterface);

		// setting up 2D and 3D displays:
		setup2DDisplays();
		setup3DDisplays();

		// Utility interfaces are added
		// lHalcyonFrame.addToolbar( new DemoToolbarWindow(
		// lHalcyonFrame.getViewManager() ) );

	}

	private void initJavaFX()
	{
		final CountDownLatch latch = new CountDownLatch(1);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new JFXPanel(); // initializes JavaFX environment
				latch.countDown();
			}
		});
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void setup3DDisplays()
	{
		// 3D Views:

		for (Stack3DDisplay lStack3DDisplay : mMicroscopeGUI.get3DStackDisplayList())
		{
			HalcyonNodeInterface node = new HalcyonOtherNode(	lStack3DDisplay.getName(),
																												MicroscopeNodeType.StackDisplay3D,
																												() -> {
																													lStack3DDisplay.setVisible(true);
																													lStack3DDisplay.requestFocus();
																												},
																												() -> {
																													lStack3DDisplay.setVisible(false);
																												},
																												() -> {
																													lStack3DDisplay.close();
																												});
			mHalcyonFrame.addNode(node);
		}
	}

	private void setup2DDisplays()
	{
		// 2D Views:

		for (Stack2DDisplay lStack2DDisplay : mMicroscopeGUI.get2DStackDisplayList())
		{
			HalcyonNodeInterface node = new HalcyonOtherNode(	lStack2DDisplay.getName(),
																												MicroscopeNodeType.StackDisplay2D,
																												() -> {
																													lStack2DDisplay.setVisible(true);
																													lStack2DDisplay.requestFocus();
																												},
																												() -> {
																													lStack2DDisplay.setVisible(false);
																												},
																												() -> {
																													lStack2DDisplay.close();
																												});
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupScriptEngines(MicroscopeInterface pMicroscopeInterface)
	{
		// Script Engines:

		for (ScriptingEngine lScriptingEngine : mMicroscopeGUI.getScriptingEnginesList())
		{

			MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

			ScriptingWindow lScriptingWindow = new ScriptingWindow(	pMicroscopeInterface.getName() + " scripting window",
																															lScriptingEngine,
																															lCurrentMachineConfiguration.getIntegerProperty("scripting.nbrows",
																																																							60),
																															lCurrentMachineConfiguration.getIntegerProperty("scripting.nbcols",
																																																							80));

			lScriptingWindow.loadLastLoadedScriptFile();

			HalcyonNodeInterface node = new HalcyonNode(lScriptingEngine.getScriptingLanguageInterface()
																																	.getName(),
																									MicroscopeNodeType.Scripting,
																									lScriptingWindow);
			mHalcyonFrame.addNode(node);
		}/**/
	}

	private void setupSignalGenerators()
	{
		for (SignalGeneratorInterface lSignalGenerator : mMicroscopeInterface.getDeviceLists()
																																					.getDevices(SignalGeneratorInterface.class))
		{
			SignalGeneratorPanel lSignalGeneratorPanel = new SignalGeneratorPanel(lSignalGenerator);

			HalcyonSwingNode node = new HalcyonSwingNode(	lSignalGenerator.getName(),
																										MicroscopeNodeType.SignalGenerator,
																										lSignalGeneratorPanel);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupScalingAmplifiers()
	{
		for (ScalingAmplifierDeviceInterface lScalingAmplifier : mMicroscopeInterface.getDeviceLists()
																																									.getDevices(ScalingAmplifierDeviceInterface.class))
		{
			ScalingAmplifierPanel lScalingAmplifierPanel = new ScalingAmplifierPanel(lScalingAmplifier);

			HalcyonNode node = new HalcyonNode(	lScalingAmplifier.getName(),
																					MicroscopeNodeType.ScalingAmplifier,
																					lScalingAmplifierPanel);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupCameras()
	{
		for (StackCameraDeviceInterface lStackCamera : mMicroscopeInterface.getDeviceLists()
																																				.getDevices(StackCameraDeviceInterface.class))
		{
			CameraDevicePanel cameraDeviceGUI = new CameraDevicePanel(lStackCamera);

			HalcyonNode node = new HalcyonNode(	lStackCamera.getName(),
																					MicroscopeNodeType.Camera,
																					cameraDeviceGUI);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupStages()
	{
		for (StageDeviceInterface lStageDevice : mMicroscopeInterface.getDeviceLists()
																																	.getDevices(StageDeviceInterface.class))
		{
			StageDeviceGUI stageDeviceGUI = new StageDeviceGUI(lStageDevice);

			HalcyonNode node = new HalcyonNode(	lStageDevice.getName(),
																					MicroscopeNodeType.Stage,
																					stageDeviceGUI);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupFilterWheels()
	{
		for (FilterWheelDeviceInterface lFilterWheel : mMicroscopeInterface.getDeviceLists()
																																				.getDevices(FilterWheelDeviceInterface.class))
		{
			FilterWheelDevicePanel lFilterWheelDevicePanel = new FilterWheelDevicePanel(lFilterWheel);

			HalcyonNode node = new HalcyonNode(	lFilterWheel.getName(),
																					MicroscopeNodeType.FilterWheel,
																					lFilterWheelDevicePanel);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupOpticalSwitches()
	{
		for (OpticalSwitchDeviceInterface lOpticalSwitch : mMicroscopeInterface.getDeviceLists()
																																						.getDevices(OpticalSwitchDeviceInterface.class))
		{
			SwitchingDevicePanel lSwitchingDeviceGUI = new SwitchingDevicePanel(lOpticalSwitch);

			HalcyonNode node = new HalcyonNode(	lOpticalSwitch.getName(),
																					MicroscopeNodeType.OpticalSwitch,
																					lSwitchingDeviceGUI);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupLasers()
	{
		for (LaserDeviceInterface lLaserDevice : mMicroscopeInterface.getDeviceLists()
																																	.getDevices(LaserDeviceInterface.class))
		{
			LaserDeviceGUI laserDeviceGUI = new LaserDeviceGUI(lLaserDevice);

			HalcyonNode node = new HalcyonNode(	lLaserDevice.getName(),
																					MicroscopeNodeType.Laser,
																					laserDeviceGUI);
			mHalcyonFrame.addNode(node);
		}
	}

	public boolean isVisible()
	{
		return mHalcyonFrame.isVisible();
	}

}
