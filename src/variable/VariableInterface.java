package variable;

public interface VariableInterface<O>
{

	public String getName();

	public void setCurrent();

	public void set(O pValue);

	public O get();

	/*
	public void sendUpdatesTo(VariableInterface<O> pVariable);

	public void doNotSendUpdatesTo(VariableInterface<O> pVariable);

	public void doNotSendAnyUpdates();

	public void syncWith(VariableInterface<O> pVariable);

	public void doNotSyncWith(VariableInterface<O> pVariable);
	/**/
}
