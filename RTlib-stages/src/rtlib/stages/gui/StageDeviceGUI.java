package rtlib.stages.gui;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.component.RunnableFX;
import rtlib.stages.StageDeviceInterface;
import utils.RunFX;

/**
 * StageDeviceGUI for Halcyon
 */
public class StageDeviceGUI implements RunnableFX
{
	public final StageControl mStage;

	public StageDeviceGUI( StageDeviceInterface stageDeviceInterface )
	{
		mStage = new StageControl();
	}

	@Override public void init()
	{
		mStage.init();
	}

	@Override public void start(Stage stage) {
		VBox pane = new VBox();

		pane.getChildren().addAll( mStage.getPanel() );

		Scene scene = new Scene(pane, javafx.scene.paint.Color.WHITE);

		stage.setTitle("Stage");
		stage.setScene(scene);
		stage.show();
	}

	@Override public void stop()
	{
	}

	public VBox getPanel()
	{
		return mStage.getPanel();
	}

	public static void main(String[] args)
	{
		RunFX.start( new StageDeviceGUI( null ) );
	}
}
