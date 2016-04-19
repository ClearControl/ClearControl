package rtlib.gui.variable;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableValue;
import rtlib.core.variable.Variable;

public class JFXSimpleLongPropertyVariable extends Variable<Long>
{
	private SimpleLongProperty mProperty;

	public JFXSimpleLongPropertyVariable(	SimpleLongProperty pSimpleLongProperty,
																				String pVariableName,
																				long pInitialState)
	{
		super(pVariableName, pInitialState);
		mProperty = pSimpleLongProperty;

		mProperty.addListener((	ObservableValue<? extends Number> observable,
														Number pOldValue,
														Number pNewValue) -> {

			if (!pOldValue.equals(pNewValue))
				this.set(pNewValue.longValue());
		});

		addSetListener((Long pOldValue, Long pNewValue) -> {
			if (!pOldValue.equals(pNewValue))
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

	public Property<Number> getProperty()
	{
		return mProperty;
	}
}
