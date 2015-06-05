package rtlib.stack.processor;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclableFactory;

public interface SameTypeStackProcessingPipeline<T extends NativeType<T>, A extends ArrayDataAccess<A>> extends
																																																				OpenCloseDeviceInterface,
																																																				StartStopDeviceInterface
{

	public void addStackProcessor(final SameTypeStackProcessorInterface<T, A> pStackProcessor,
																RecyclableFactory<StackInterface<T, A>, StackRequest<T>> pStackFactory,
																int pMaximumNumberOfObjects);

	public void removeStackProcessor(final SameTypeStackProcessorInterface<T, A> pStackProcessor);

	public ObjectVariable<StackInterface<T, A>> getInputVariable();

	public ObjectVariable<StackInterface<T, A>> getOutputVariable();

}
