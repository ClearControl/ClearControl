package rtlib.core.variable;

public interface VariableInterface<O>
{

	public String getName();

	public void setCurrent();

	public void set(O pValue);

	public O get();

	public void addListener(VariableListener<O> pDoubleVariableListener);

	public void removeListener(VariableListener<O> pDoubleVariableListener);

	public void removeAllListener();

}
