package clearcontrol.gui.jfx.slider.demo;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.slider.VariableSlider;
import clearcontrol.gui.jfx.sliderpanel.CustomVariablePane;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class VariableSliderDemo extends Application
{

	@Override
	public void start(Stage stage)
	{
		GridPane root = new GridPane();
		Scene scene = new Scene(root, 600, 400);
		stage.setScene(scene);
		stage.setTitle("Slider Sample");
		// scene.setFill(Color.BLACK);

		Variable<Number> lDoubleVariable = new Variable<Number>("DemoDoubleVar",
																														0.0);
		lDoubleVariable.addSetListener((o, n) -> {
			System.out.println("double: " + n);
		});

		VariableSlider<Number> lVariableDoubleSlider = new VariableSlider<Number>("a double value: ",
																																							lDoubleVariable,
																																							-1.0,
																																							2.0,
																																							0.1,
																																							0.1);

		root.add(lVariableDoubleSlider,0,1);

		Variable<Number> lIntVariable = new Variable<Number>(	"DemoIntVar",
																													0.0);
		lIntVariable.addSetListener((o, n) -> {
			System.out.println("int: " + n);
		});

		VariableSlider<Number> lVariableIntSlider = new VariableSlider<Number>(	"an int value: ",
																																						lIntVariable,
																																						-10,
																																						20,
																																						1,
																																						5);

		root.add(lVariableIntSlider,0,2);

		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
