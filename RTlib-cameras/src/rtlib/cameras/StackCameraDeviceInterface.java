package rtlib.cameras;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.device.NameableInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;

public interface StackCameraDeviceInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																									CameraDeviceInterface,
																									NameableInterface
{
	DoubleVariable getNumberOfImagesPerPlaneVariable();

	BooleanVariable getStackModeVariable();

	BooleanVariable getKeepPlaneVariable();

	ObjectVariable<StackInterface<T, A>> getStackVariable();

	@Override
	void trigger();

}
