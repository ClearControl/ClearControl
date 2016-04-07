package rtlib.core.variable.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import rtlib.core.variable.types.booleanv.BooleanVariable;

public class BooleanPropertyVariable extends BooleanVariable
{
	private BooleanProperty mBooleanProperty;

	public BooleanPropertyVariable(	BooleanProperty pBooleanProperty,
																	String pVariableName,
																	boolean pInitialState)
	{
		super(pVariableName, pInitialState);
		mBooleanProperty = pBooleanProperty;

		mBooleanProperty.addListener((ObservableValue<? extends Boolean> observable,
																	Boolean pOldValue,
																	Boolean pNewValue) -> {

			if (pOldValue != pNewValue)
				this.setValue(pNewValue);

		});

		addSetListener((Boolean pOldValue, Boolean pNewValue) -> {
			if (pOldValue != pNewValue)
				mBooleanProperty.setValue(pNewValue);
		});
	}

	public BooleanProperty getBooleanProperty()
	{
		return mBooleanProperty;
	}
}
