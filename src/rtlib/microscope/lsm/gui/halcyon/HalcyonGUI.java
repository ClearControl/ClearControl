package rtlib.microscope.lsm.gui.halcyon;

import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonNode;
import halcyon.model.node.HalcyonNodeInterface;
import halcyon.model.node.HalcyonSwingNode;
import halcyon.view.TreePanel;

import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;

import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.cameras.gui.jfx.CameraDevicePanel;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.gui.halcyon.NodeType;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.gui.jfx.LaserDeviceGUI;
import rtlib.microscope.lsm.LightSheetMicroscopeDeviceLists;
import rtlib.microscope.lsm.LightSheetMicroscopeInterface;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.gui.ScriptingWindow;
import rtlib.stages.StageDeviceInterface;
import rtlib.stages.gui.StageDeviceGUI;

public class HalcyonGUI extends Application
{
	private HalcyonFrame mHalcyonFrame;

	public HalcyonGUI()
	{
		TreePanel lTreePanel = new TreePanel(	"Device tree",
																					"Root",
																					this.getClass()
																							.getResourceAsStream("/rtlib/gui/halcyon/icons/folder_16.png"),
																					NodeType.values());
		mHalcyonFrame = new HalcyonFrame(lTreePanel);

	}

	public HalcyonGUI(LightSheetMicroscopeInterface pLightSheetMicroscopeInterface)
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

		TreePanel lTreePanel = new TreePanel(	"Config",
																					"Microscopy",
																					this.getClass()
																							.getResourceAsStream("/rtlib/gui/halcyon/icons/folder_16.png"),
																					NodeType.values());

		mHalcyonFrame = new HalcyonFrame(lTreePanel);

		LightSheetMicroscopeDeviceLists lDeviceLists = pLightSheetMicroscopeInterface.getDeviceLists();

		// Laser Device list
		for (int i = 0; i < lDeviceLists.getNumberOfLaserDevices(); i++)
		{
			LaserDeviceInterface laserDevice = lDeviceLists.getLaserDevice(i);

			LaserDeviceGUI laserDeviceGUI = new LaserDeviceGUI(laserDevice);

			HalcyonNode node = new HalcyonNode(	"Laser-" + i,
																					NodeType.Laser,
																					laserDeviceGUI.getPanel());
			mHalcyonFrame.addNode(node);
		}

		// Stage Device List
		for (int i = 0; i < lDeviceLists.getNumberOfStageDevices(); i++)
		{
			StageDeviceInterface stageDevice = lDeviceLists.getStageDevice(i);

			// Stage
			StageDeviceGUI stageDeviceGUI = new StageDeviceGUI(stageDevice);

			HalcyonNode node = new HalcyonNode(	"Stage-" + i,
																					NodeType.Stage,
																					stageDeviceGUI.getPanel());
			mHalcyonFrame.addNode(node);
		}

		// Stack Camera List
		for (int i = 0; i < lDeviceLists.getNumberOfStackCameraDevices(); i++)
		{
			StackCameraDeviceInterface cameraDevice = lDeviceLists.getStackCameraDevice(i);

			CameraDevicePanel cameraDeviceGUI = new CameraDevicePanel(cameraDevice);

			HalcyonNode node = new HalcyonNode(	"Camera-" + i,
																					NodeType.Camera,
																					cameraDeviceGUI.getPanel());
			mHalcyonFrame.addNode(node);
		}

		// Script Engines:
		for (int i = 0; i < lDeviceLists.getNumberOfScriptingEngines(); i++)
		{
			ScriptingEngine lScriptingEngine = lDeviceLists.getScriptingEngine(i);

			MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

			ScriptingWindow lScriptingWindow = new ScriptingWindow(	pLightSheetMicroscopeInterface.getName() + " scripting window",
																															lScriptingEngine,
																															lCurrentMachineConfiguration.getIntegerProperty("scripting.nbrows",
																																																							60),
																															lCurrentMachineConfiguration.getIntegerProperty("scripting.nbcols",
																																																							80));

			lScriptingWindow.loadLastLoadedScriptFile();

			HalcyonNodeInterface node = new HalcyonSwingNode(	"ScriptingEngine-" + i,
																												NodeType.Scripting,
																												lScriptingWindow,
																												true);
			mHalcyonFrame.addNode(node);
		}

		// Utility interfaces are added
		// lHalcyonFrame.addToolbar( new DemoToolbarWindow(
		// lHalcyonFrame.getViewManager() ) );
	}

	@Override
	public void start(Stage pJavaFxStage) throws Exception
	{
		pJavaFxStage.setOnCloseRequest(event -> System.exit(0));

		mHalcyonFrame.start(pJavaFxStage);
	}

	public void externalStart()
	{
		HalcyonGUI lThis = this;
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
		HalcyonGUI lThis = this;
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
