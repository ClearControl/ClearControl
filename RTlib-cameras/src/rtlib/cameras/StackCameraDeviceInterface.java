package rtlib.cameras;

import coremem.recycling.RecyclerInterface;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.device.NameableInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public interface StackCameraDeviceInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																									CameraDeviceInterface,
																									NameableInterface
{
	DoubleVariable getNumberOfImagesPerPlaneVariable();

	BooleanVariable getStackModeVariable();

	BooleanVariable getKeepPlaneVariable();

	DoubleVariable getStackBytesPerPixelVariable();

	DoubleVariable getStackWidthVariable();

	DoubleVariable getStackHeightVariable();

	DoubleVariable getStackDepthVariable();

	void setStackRecycler(RecyclerInterface<StackInterface<T, A>, StackRequest<T>> pRecycler);
	
	int getMinimalNumberOfAvailableStacks();

	void setMinimalNumberOfAvailableStacks(int pMinimalNumberOfAvailableStacks);

	RecyclerInterface<StackInterface<T, A>, StackRequest<T>> getStackRecycler();

	ObjectVariable<StackInterface<T, A>> getStackVariable();

	@Override
	void trigger();



}
