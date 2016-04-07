package rtlib.core.variable.javafx;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import rtlib.core.variable.types.objectv.ObjectVariable;

public class JFXPropertyVariable<O> extends ObjectVariable<O>
{
	private Property<O> mProperty;

	public JFXPropertyVariable(	Property<O> pProperty,
															String pVariableName,
															O pInitialState)
	{
		super(pVariableName, pInitialState);
		mProperty = pProperty;

		mProperty.addListener((	ObservableValue<? extends O> observable,
														O pOldValue,
														O pNewValue) -> {

			if (pOldValue != pNewValue)
				this.set(pNewValue);

		});

		addSetListener((O pOldValue, O pNewValue) -> {
			if (pOldValue != pNewValue)
				mProperty.setValue(pNewValue);
		});
	}

	public Property<O> getProperty()
	{
		return mProperty;
	}
}
