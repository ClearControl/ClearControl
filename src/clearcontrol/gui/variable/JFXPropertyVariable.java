package clearcontrol.gui.variable;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.events.EventPropagator;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

public class JFXPropertyVariable<O> extends Variable<O>
{
	private Property<O> mProperty;
	private Object mThreadDispatch = new Object();


	
	public JFXPropertyVariable(	Property<O> pProperty,
															String pVariableName,
															O pInitialState)
	{
		super(pVariableName, pInitialState);
		mProperty = pProperty;
		
		mProperty.addListener((	ObservableValue<? extends O> observable,
														O pOldValue,
														O pNewValue) -> {

			if (!pOldValue.equals(pNewValue))
			{
				EventPropagator.add(mThreadDispatch);
				this.set(pNewValue);
			}
		});

		addSetListener((O pOldValue, O pNewValue) -> {
			if (!pOldValue.equals(pNewValue))
			{
				// ArrayList<Object> lListOfTraversedObjects =
				// EventPropagator.getListOfTraversedObjects();

				if (!pOldValue.equals(pNewValue))
				{
					EventPropagator.add(mThreadDispatch);
					mProperty.setValue(pNewValue);
				}

				/*Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						
					}
				});/**/
			}

		});
	}



	public Property<O> getProperty()
	{
		return mProperty;
	}
}
