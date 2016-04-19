package rtlib.gui.jfx.sliderpanel;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import rtlib.core.variable.Variable;
import rtlib.gui.variable.JFXPropertyVariable;

public class SliderPanel extends GridPane
{

	private int mCursor = 0;

	public SliderPanel()
	{
		super();

		setAlignment(Pos.CENTER);
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 25, 25, 25));
	}

	public <T extends Number> void addSliderForVariable(Variable<Number> pVariable,
																											T pMin,
																											T pMax,
																											T pTicks)
	{
		final Slider lSlider = new Slider();
		lSlider.setMin(pMin.doubleValue());
		lSlider.setMax(pMax.doubleValue());
		lSlider.setMajorTickUnit(pTicks.doubleValue());
		lSlider.setShowTickMarks(true);
		lSlider.setShowTickLabels(true);

		add(lSlider, 0, mCursor++);

		DoubleProperty lValueProperty = lSlider.valueProperty();

		JFXPropertyVariable<Number> lJFXPropertyVariable = new JFXPropertyVariable<Number>(	lValueProperty,
																																												pVariable.getName(),
																																												pVariable.get());

		lJFXPropertyVariable.syncWith(pVariable);
	}
}
