package rtlib.hardware.cameras.gui.jfx.demo;

import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rtlib.core.variable.Variable;
import rtlib.hardware.cameras.devices.sim.StackCameraDeviceSimulator;
import rtlib.hardware.cameras.gui.jfx.CameraDevicePanel;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.sourcesink.RandomStackSource;

public class CameraDevicePanelDemo extends Application
{

	@Override
	public void start(Stage pPrimaryStage) throws Exception
	{

		final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

		final RecyclerInterface<StackInterface, StackRequest> lRecycler = new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
																																																											10);
		RandomStackSource lRandomStackSource = new RandomStackSource(	100L,
																																	101L,
																																	103L,
																																	lRecycler);

		Variable<Boolean> lTrigger = new Variable<Boolean>(	"CameraTrigger",
																												false);

		StackCameraDeviceSimulator lStackCameraDeviceSimulator = new StackCameraDeviceSimulator("StackCamera",
																																														lRandomStackSource,
																																														lTrigger);

		CameraDevicePanel lCameraDevicePanel = new CameraDevicePanel(lStackCameraDeviceSimulator);

		VBox pane = new VBox();

		pane.getChildren().add(lCameraDevicePanel);

		Scene scene = new Scene(pane, javafx.scene.paint.Color.WHITE);

		pPrimaryStage.setTitle("Lasers");
		pPrimaryStage.setScene(scene);
		pPrimaryStage.show();

	}

	public static void main(String[] args)
	{
		Application.launch(CameraDevicePanelDemo.class);
	}

}
