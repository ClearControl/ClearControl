package variable;

public interface VariableListener<O>
{
	void getEvent(O pCurrentValue);

	void setEvent(O pCurrentValue, O pNewValue);

}
