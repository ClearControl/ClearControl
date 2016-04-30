package clearcontrol.core.variable.exceptions;

import clearcontrol.core.variable.Variable;

public class InvalidVariableTypeException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public InvalidVariableTypeException(final Variable<?> pFromVariable,
																			final Variable<?> pToVariable)
	{
		super(String.format("Variable %s is incompatible with variable %s.",
												pFromVariable.toString(),
												pToVariable.toString()));
	}
}
