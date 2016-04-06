package rtlib.core.variable.javafx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import rtlib.core.variable.types.doublev.DoubleVariable;

public class DoublePropertyVariable extends DoubleVariable
{
	private DoubleProperty mDoubleProperty;

	public DoublePropertyVariable(DoubleProperty pDoubleProperty,
																String pVariableName,
																double pInitialState)
	{
		super(pVariableName, pInitialState);
		mDoubleProperty = pDoubleProperty;

		mDoubleProperty.addListener((	ObservableValue<? extends Number> observable,
																	Number pOldValue,
																	Number pNewValue) -> {

			if (pOldValue != pNewValue)
				this.setValue(pNewValue.doubleValue());

		});

		addSetListener((Double pOldValue, Double pNewValue) -> {
			if (pOldValue != pNewValue)
				mDoubleProperty.set(pNewValue);
		});
	}

	public DoubleProperty getDoubleProperty()
	{
		return mDoubleProperty;
	}
}
