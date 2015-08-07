package rtlib.stack.processor;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;

public interface StackProcessingPipeline<TI extends NativeType<TI>, AI extends ArrayDataAccess<AI>, TO extends NativeType<TO>, AO extends ArrayDataAccess<AO>>	extends
																																								OpenCloseDeviceInterface
{

	public void addStackProcessor(final StackProcessorInterface<TI, AI, TO, AO> pStackProcessor);

	public void removeStackProcessor(final StackProcessorInterface<TI, AI, TO, AO> pStackProcessor);

	public ObjectVariable<StackInterface<TI, AI>> getInputVariable();

	public ObjectVariable<StackInterface<TO, AO>> getOutputVariable();

}
