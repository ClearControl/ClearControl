package rtlib.gui.jfx.sliderpanel.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import rtlib.core.variable.Variable;
import rtlib.core.variable.bounded.BoundedVariable;
import rtlib.gui.jfx.slider.VariableSlider;
import rtlib.gui.jfx.sliderpanel.SliderPanel;

public class SliderPanelDemo extends Application
{

	@Override
	public void start(Stage stage)
	{
		Group root = new Group();
		Scene scene = new Scene(root, 600, 400);
		stage.setScene(scene);
		stage.setTitle("Slider Sample");
		// scene.setFill(Color.BLACK);

		SliderPanel lSliderPanel = new SliderPanel();

		Variable<Number> lDoubleVariable = new Variable<Number>("DemoDoubleVar",
																														0.0);
		lDoubleVariable.addSetListener((o, n) -> {
			System.out.println("double: " + n);
		});
		lSliderPanel.addSliderForVariable(lDoubleVariable,
																			-1.0,
																			1.0,
																			0.1,
																			0.1);

		Variable<Number> lIntegerVariable1 = new Variable<Number>("DemoIntegerVar",
																															2);
		lIntegerVariable1.addSetListener((o, n) -> {
			System.out.println("int: " + n);
		});
		lSliderPanel.addSliderForVariable(lIntegerVariable1,
																			-10,
																			30,
																			1,
																			5);

		Variable<Number> lIntegerVariable2 = new Variable<Number>("DemoIntegerVar",
																															2);
		lIntegerVariable2.addSetListener((o, n) -> {
			System.out.println("int2: " + n);
		});
		VariableSlider<Number> lAddSliderForVariable = lSliderPanel.addSliderForVariable(	lIntegerVariable2,
																																											-10,
																																											30,
																																											1,
																																											5);
		lAddSliderForVariable.setUpdateIfChanging(true);

		BoundedVariable<Number> lBoundedVariable = new BoundedVariable<Number>(	"DemoBoundedDoubleVar",
																																						2.0,
																																						-10.0,
																																						10.0,
																																						0.1);
		lBoundedVariable.addSetListener((o, n) -> {
			System.out.println("boundeddouble: " + n);
		});
		VariableSlider<Number> lBoundedVariableSlider = lSliderPanel.addSliderForVariable(lBoundedVariable,
																																											5.0);

		root.getChildren().add(lSliderPanel);

		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
