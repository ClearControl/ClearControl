package rtlib.microscope.gui.halcyon;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonExternalNode;
import halcyon.model.node.HalcyonNode;
import halcyon.model.node.HalcyonNodeInterface;
import halcyon.model.node.HalcyonSwingNode;
import halcyon.view.TreePanel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.device.switches.gui.jfx.SwitchingDevicePanel;
import rtlib.gui.halcyon.NodeType;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.hardware.cameras.StackCameraDeviceInterface;
import rtlib.hardware.cameras.gui.jfx.CameraDevicePanel;
import rtlib.hardware.lasers.LaserDeviceInterface;
import rtlib.hardware.lasers.gui.jfx.LaserDeviceGUI;
import rtlib.hardware.optomech.filterwheels.FilterWheelDeviceInterface;
import rtlib.hardware.optomech.filterwheels.gui.jfx.FilterWheelDevicePanel;
import rtlib.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import rtlib.hardware.signalamp.ScalingAmplifierDeviceInterface;
import rtlib.hardware.signalamp.gui.ScalingAmplifierPanel;
import rtlib.hardware.signalgen.SignalGeneratorInterface;
import rtlib.hardware.signalgen.gui.swing.SignalGeneratorPanel;
import rtlib.hardware.stages.StageDeviceInterface;
import rtlib.hardware.stages.gui.jfx.StageDeviceGUI;
import rtlib.microscope.MicroscopeDeviceLists;
import rtlib.microscope.MicroscopeInterface;
import rtlib.microscope.gui.MicroscopeGUI;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.gui.ScriptingWindow;

public class HalcyonGUIGenerator extends Application
{
	private MicroscopeInterface mMicroscopeInterface;
	private HalcyonFrame mHalcyonFrame;
	private MicroscopeGUI mMicroscopeGUI;

	public HalcyonGUIGenerator(	MicroscopeInterface pMicroscopeInterface,
															MicroscopeGUI pMicroscopeGUI)
	{
		mMicroscopeInterface = pMicroscopeInterface;
		mMicroscopeGUI = pMicroscopeGUI;
		initJavaFX();

		TreePanel lTreePanel = new TreePanel(	"Config",
																					"Microscopy",
																					this.getClass()
																							.getResourceAsStream("/rtlib/gui/halcyon/icons/folder_16.png"),
																					NodeType.values());

		mHalcyonFrame = new HalcyonFrame(lTreePanel);

		MicroscopeDeviceLists lDeviceLists = mMicroscopeInterface.getDeviceLists();

		// Setting up devices:
		setupLasers(lDeviceLists);
		setupOpticalSwitches(lDeviceLists);
		setupFilterWheels(lDeviceLists);
		setupStages(lDeviceLists);
		setupCameras(lDeviceLists);
		setupSignalGenerators(lDeviceLists);
		setupScalingAmplifiers(lDeviceLists);

		// seting up script engines:
		setupScriptEngines(pMicroscopeInterface);

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
			HalcyonNodeInterface node = new HalcyonExternalNode(lStack3DDisplay.getName(),
																													NodeType.StackDisplay3D,
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
			HalcyonNodeInterface node = new HalcyonExternalNode(lStack2DDisplay.getName(),
																													NodeType.StackDisplay2D,
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

			HalcyonNodeInterface node = new HalcyonSwingNode(	lScriptingEngine.getScriptingLanguageInterface()
																																				.getName(),
																												NodeType.Scripting,
																												lScriptingWindow,
																												true);
			mHalcyonFrame.addNode(node);
		}/**/
	}

	private void setupSignalGenerators(MicroscopeDeviceLists lDeviceLists)
	{
		for (int i = 0; i < lDeviceLists.getNumberOfSignalGeneratorDevices(); i++)
		{
			SignalGeneratorInterface lSignalGenerator = lDeviceLists.getSignalGeneratorDevice(i);

			SignalGeneratorPanel lSignalGeneratorPanel = new SignalGeneratorPanel(lSignalGenerator);

			HalcyonSwingNode node = new HalcyonSwingNode(	lSignalGenerator.getName(),
																										NodeType.SignalGenerator,
																										lSignalGeneratorPanel);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupScalingAmplifiers(MicroscopeDeviceLists lDeviceLists)
	{
		for (int i = 0; i < lDeviceLists.getNumberOfScalingAmplifierDevices(); i++)
		{
			ScalingAmplifierDeviceInterface lScalingAmplifier = lDeviceLists.getScalingAmplifierDevice(i);

			ScalingAmplifierPanel lScalingAmplifierPanel = new ScalingAmplifierPanel(lScalingAmplifier);

			HalcyonNode node = new HalcyonNode(	lScalingAmplifier.getName(),
																					NodeType.ScalingAmplifier,
																					lScalingAmplifierPanel);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupCameras(MicroscopeDeviceLists lDeviceLists)
	{
		// Stack Camera List
		for (int i = 0; i < lDeviceLists.getNumberOfStackCameraDevices(); i++)
		{
			StackCameraDeviceInterface lCameraDevice = lDeviceLists.getStackCameraDevice(i);

			CameraDevicePanel cameraDeviceGUI = new CameraDevicePanel(lCameraDevice);

			HalcyonNode node = new HalcyonNode(	lCameraDevice.getName(),
																					NodeType.Camera,
																					cameraDeviceGUI.getPanel());
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupStages(MicroscopeDeviceLists lDeviceLists)
	{
		// Stage Device List
		for (int i = 0; i < lDeviceLists.getNumberOfStageDevices(); i++)
		{
			StageDeviceInterface lStageDevice = lDeviceLists.getStageDevice(i);

			// Stage
			StageDeviceGUI stageDeviceGUI = new StageDeviceGUI(lStageDevice);

			HalcyonNode node = new HalcyonNode(	lStageDevice.getName(),
																					NodeType.Stage,
																					stageDeviceGUI.getPanel());
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupFilterWheels(MicroscopeDeviceLists lDeviceLists)
	{
		// FilterWheel Device list
		for (int i = 0; i < lDeviceLists.getNumberOfFilterWheelDevices(); i++)
		{
			FilterWheelDeviceInterface lFilterWheelDeviceInterface = lDeviceLists.getFilterWheelDevice(i);

			FilterWheelDevicePanel lFilterWheelDevicePanel = new FilterWheelDevicePanel(lFilterWheelDeviceInterface);

			HalcyonNode node = new HalcyonNode(	lFilterWheelDeviceInterface.getName(),
																					NodeType.FilterWheel,
																					lFilterWheelDevicePanel);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupOpticalSwitches(MicroscopeDeviceLists lDeviceLists)
	{
		// OpticalSwitch Device list
		for (int i = 0; i < lDeviceLists.getNumberOfOpticalSwitchDevices(); i++)
		{
			OpticalSwitchDeviceInterface lOpticalSwitchDeviceInterface = lDeviceLists.getOpticalSwitchDevice(i);

			SwitchingDevicePanel lSwitchingDeviceGUI = new SwitchingDevicePanel(lOpticalSwitchDeviceInterface);

			HalcyonNode node = new HalcyonNode(	lOpticalSwitchDeviceInterface.getName(),
																					NodeType.OpticalSwitch,
																					lSwitchingDeviceGUI);
			mHalcyonFrame.addNode(node);
		}
	}

	private void setupLasers(MicroscopeDeviceLists lDeviceLists)
	{
		// Laser Device list
		for (int i = 0; i < lDeviceLists.getNumberOfLaserDevices(); i++)
		{
			LaserDeviceInterface lLaserDevice = lDeviceLists.getLaserDevice(i);

			LaserDeviceGUI laserDeviceGUI = new LaserDeviceGUI(lLaserDevice);

			HalcyonNode node = new HalcyonNode(	lLaserDevice.getName(),
																					NodeType.Laser,
																					laserDeviceGUI.getPanel());
			mHalcyonFrame.addNode(node);
		}
	}

	@Override
	public void start(Stage pJavaFxStage) throws Exception
	{
		mHalcyonFrame.start(pJavaFxStage);
	}

	public void externalStart()
	{
		HalcyonGUIGenerator lThis = this;
		Platform.runLater(() -> {
			Stage stage = new Stage();
			try
			{
				lThis.start(stage);
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		});

	}

	public void externalStop()
	{
		HalcyonGUIGenerator lThis = this;
		Platform.runLater(() -> {
			try
			{
				lThis.stop();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		});
	}

	public static void main(final String[] args)
	{
		launch(args);
	}

	public boolean isVisible()
	{
		return mHalcyonFrame.isVisible();
	}
}
