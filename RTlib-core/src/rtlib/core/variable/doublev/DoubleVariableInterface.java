package rtlib.core.variable.doublev;

import rtlib.core.variable.VariableInterface;

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
