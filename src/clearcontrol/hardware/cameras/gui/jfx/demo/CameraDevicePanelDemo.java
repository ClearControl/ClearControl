package clearcontrol.hardware.cameras.gui.jfx.demo;

import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.hardware.cameras.gui.jfx.CameraDevicePanel;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.RandomStackSource;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
		lStackCameraDeviceSimulator.setSimLogging(true);

		CameraDevicePanel lCameraDevicePanel = new CameraDevicePanel(lStackCameraDeviceSimulator);

		VBox pane = new VBox();

		pane.getChildren().add(lCameraDevicePanel);

		Scene scene = new Scene(pane, javafx.scene.paint.Color.WHITE);

		pPrimaryStage.setTitle(this.getClass().getSimpleName());
		pPrimaryStage.setScene(scene);
		pPrimaryStage.show();

	}

	public static void main(String[] args)
	{
		Application.launch(CameraDevicePanelDemo.class);
	}

}
