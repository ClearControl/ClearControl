package variable;

import variable.doublev.DoubleVariable;

public interface DoubleVariableInterface extends
																				VariableInterface<Double>
{

	public void sendUpdatesTo(DoubleVariable pVariable);

	public void doNotSendUpdatesTo(DoubleVariable pVariable);

	public void doNotSendAnyUpdates();

	public void syncWith(DoubleVariable pVariable);

	public void doNotSyncWith(DoubleVariable pVariable);
	/**/
}
