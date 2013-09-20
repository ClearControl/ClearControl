package variable;

import variable.doublev.DoubleInputVariableInterface;
import variable.doublev.DoubleOutputVariableInterface;
import variable.doublev.DoubleVariable;

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
