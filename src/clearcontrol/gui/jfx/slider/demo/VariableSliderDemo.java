package clearcontrol.gui.jfx.slider.demo;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.slider.VariableSlider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class VariableSliderDemo extends Application	implements
																										AsynchronousExecutorServiceAccess
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

		root.add(lVariableDoubleSlider, 0, 1);

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
		lVariableIntSlider.setUpdateIfChanging(false);

		root.add(lVariableIntSlider, 0, 2);

		Variable<Number> lChangingIntVariable = new Variable<Number>(	"DemoChangingIntVar",
																																	0.0);
		lChangingIntVariable.addSetListener((o, n) -> {
			System.out.println("changing int: " + n);
		});

		executeAsynchronously(() -> {
			while (true)
			{
				lChangingIntVariable.set((int) (Math.random() * 10));
				ThreadUtils.sleep(700, TimeUnit.MILLISECONDS);
			}
		});

		VariableSlider<Number> lVariableChangingIntSlider = new VariableSlider<Number>(	"an int value that changes: ",
																																										lChangingIntVariable,
																																										-10,
																																										20,
																																										1,
																																										5);
		lVariableChangingIntSlider.setUpdateIfChanging(false);

		root.add(lVariableChangingIntSlider, 0, 3);

		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
