package rtlib.gui.jfx.onoff.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import rtlib.core.variable.Variable;
import rtlib.core.variable.bounded.BoundedVariable;
import rtlib.gui.jfx.onoff.OnOffArrayPane;
import rtlib.gui.jfx.slider.VariableSlider;
import rtlib.gui.jfx.sliderpanel.CustomVariablePane;

public class OnOffArrayPanelDemo extends Application
{

	@Override
	public void start(Stage stage)
	{
		Group root = new Group();
		Scene scene = new Scene(root, 600, 400);
		stage.setScene(scene);
		stage.setTitle("Slider Sample");
		// scene.setFill(Color.BLACK);

		OnOffArrayPane lOnOffArrayPanel = new OnOffArrayPane();

		for (int i = 0; i < 5; i++)
		{
			final int fi = i;

			Variable<Boolean> lBoolVariable = new Variable<>(	"DemoBoolVar"+i,
																												i%2==0);
			lBoolVariable.addSetListener((o, n) -> {
				System.out.println("bool "+fi+": " + n);
			});

			lOnOffArrayPanel.addSwitch("switch"+i, lBoolVariable);
		}

		root.getChildren().add(lOnOffArrayPanel);

		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
