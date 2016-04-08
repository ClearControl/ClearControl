package rtlib.core.variable.javafx;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import rtlib.core.variable.Variable;

public class JFXPropertyVariable<O> extends Variable<O>
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
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						mProperty.setValue(pNewValue);
					}
				});
			}

		});
	}

	public Property<O> getProperty()
	{
		return mProperty;
	}
}
