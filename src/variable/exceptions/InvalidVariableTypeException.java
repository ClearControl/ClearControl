package variable.exceptions;

import variable.VariableInterface;

public class InvalidVariableTypeException extends RuntimeException
{

	public InvalidVariableTypeException(final VariableInterface<?> pFromVariable,
																			final VariableInterface<?> pToVariable)
	{
		super(String.format("Variable %s is incompatible with variable %s.",
												pFromVariable.toString(),
												pToVariable.toString()));
	}
}
