package clearcontrol.hardware.stages.gui.jfx;

import clearcontrol.hardware.stages.StageDeviceInterface;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * StageDeviceGUI for Halcyon
 */
public class StageDeviceGUI extends BorderPane
{
	public final Stage3DControl mStage;

	public StageDeviceGUI(StageDeviceInterface stageDeviceInterface)
	{
		mStage = new Stage3DControl(this);
	}

	public void init()
	{
		mStage.init();
	}

	public void start(Stage stage)
	{
		mStage.start(stage);
	}

	public void stop()
	{
		mStage.stop();
	}
}
