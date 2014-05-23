package rtlib.core.variable;

import rtlib.core.variable.doublev.DoubleInputVariableInterface;
import rtlib.core.variable.doublev.DoubleOutputVariableInterface;
import rtlib.core.variable.doublev.DoubleVariable;

public interface DoubleVariableInterface extends
																				VariableInterface<Double>,
																				DoubleInputVariableInterface,
																				DoubleOutputVariableInterface
{

	public void sendUpdatesTo(DoubleVariable pVariable);

	public void doNotSendUpdatesTo(DoubleVariable pVariable);

	public void doNotSendAnyUpdates();

	public void syncWith(DoubleVariable pVariable);

	public void doNotSyncWith(DoubleVariable pVariable);

}
