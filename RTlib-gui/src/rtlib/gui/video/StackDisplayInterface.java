package rtlib.gui.video;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;

public interface StackDisplayInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>
{

	ObjectVariable<StackInterface<T, A>> getOutputStackVariable();

	void setOutputStackVariable(ObjectVariable<StackInterface<T, A>> pOutputStackVariable);

}
