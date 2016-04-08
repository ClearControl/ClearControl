package rtlib.microscope.lsm.gui.halcyon;

import halcyon.HalcyonFrame;
import halcyon.model.node.HalcyonNode;
import halcyon.view.TreePanel;

import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;

import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.cameras.gui.jfx.CameraDevicePanel;
import rtlib.gui.halcyon.NodeType;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.gui.jfx.LaserDeviceGUI;
import rtlib.microscope.lsm.LightSheetMicroscopeDeviceLists;
import rtlib.microscope.lsm.LightSheetMicroscopeInterface;
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

		LightSheetMicroscopeDeviceLists deviceLists = pLightSheetMicroscopeInterface.getDeviceLists();

		// Laser Device list
		for (int i = 0; i < deviceLists.getNumberOfLaserDevices(); i++)
		{
			LaserDeviceInterface laserDevice = deviceLists.getLaserDevice(i);

			LaserDeviceGUI laserDeviceGUI = new LaserDeviceGUI(laserDevice);

			HalcyonNode node = new HalcyonNode(	"Laser-" + i,
																					NodeType.Laser,
																					laserDeviceGUI.getPanel());
			mHalcyonFrame.addNode(node);
		}

		// Stage Device List
		for (int i = 0; i < deviceLists.getNumberOfStageDevices(); i++)
		{
			StageDeviceInterface stageDevice = deviceLists.getStageDevice(i);

			// Stage
			StageDeviceGUI stageDeviceGUI = new StageDeviceGUI(stageDevice);

			HalcyonNode node = new HalcyonNode(	"Stage-" + i,
																					NodeType.Stage,
																					stageDeviceGUI.getPanel());
			mHalcyonFrame.addNode(node);
		}

		// Stack Camera List
		for (int i = 0; i < deviceLists.getNumberOfStackCameraDevices(); i++)
		{
			StackCameraDeviceInterface cameraDevice = deviceLists.getStackCameraDevice(i);

			CameraDevicePanel cameraDeviceGUI = new CameraDevicePanel(cameraDevice);

			HalcyonNode node = new HalcyonNode(	"Camera-" + i,
																					NodeType.Camera,
																					cameraDeviceGUI.getPanel());
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
}
