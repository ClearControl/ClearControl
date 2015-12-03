package rtlib.stages.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rtlib.stages.StageDeviceInterface;

/**
 * Created by moon on 12/1/15.
 */
public class StageDeviceGUI extends Application
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

	@Override public void start(Stage stage) throws Exception {
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

	public GridPane getPanel()
	{
		return mStage.getPanel();
	}

	public static void main(String[] args)
	{
		Application.launch(args);
	}
}
