package rtlib.stack.sourcesink;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.variable.VariableInterface;
import rtlib.stack.StackInterface;

public interface StackSinkInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>
{

	public boolean appendStack(final StackInterface<T, A> pStack);

	public void addMetaData(String pPrefix, double pValue);

	public void addMetaDataVariable(final String pPrefix,
									final VariableInterface<?> pVariable);

	public void removeAllMetaDataVariables();

	public void removeMetaDataVariable(VariableInterface<?> pVariable);

}
