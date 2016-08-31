package clearcontrol.microscope.stacks.gui.jfx.demo;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFXScrollPaneDemo extends Application
{

	final ScrollPane sp = new ScrollPane();
	final Label[] labels = new Label[50];
	final VBox vb = new VBox();
	final Label fileName = new Label();

	@Override
	public void start(Stage stage)
	{
		VBox box = new VBox();
		Scene scene = new Scene(box, 180, 180);
		stage.setScene(scene);
		stage.setTitle("Scroll Pane");
		box.getChildren().addAll(sp, fileName);
		VBox.setVgrow(sp, Priority.ALWAYS);

		fileName.setLayoutX(30);
		fileName.setLayoutY(160);

		for (int i = 0; i < 50; i++)
		{
			labels[i] = new Label("item" + i);
			vb.getChildren().add(labels[i]);
		}

		sp.setVmax(440);
		sp.setPrefSize(115, 150);
		sp.setContent(vb);
		sp.vvalueProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> ov,
													Number old_val,
													Number new_val)
			{
				fileName.setText("position" + new_val);
			}
		});
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
