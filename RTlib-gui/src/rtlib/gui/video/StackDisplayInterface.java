package rtlib.gui.video;

import net.imglib2.type.NativeType;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.StackInterface;

public interface StackDisplayInterface<T extends NativeType<T>>
{

	ObjectVariable<StackInterface<T, ?>> getOutputOffHeapPlanarStackVariable();

	void setOutputStackVariable(ObjectVariable<StackInterface<T, ?>> pOutputStackVariable);

}
