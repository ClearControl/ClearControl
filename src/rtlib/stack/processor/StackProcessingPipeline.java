package rtlib.stack.processor;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclableFactory;

public interface StackProcessingPipeline extends
																				OpenCloseDeviceInterface
{

	void addStackProcessor(	StackProcessorInterface pStackProcessor,
													RecyclableFactory<StackInterface, StackRequest> pStackFactory,
													int pMaximumNumberOfObjects);

	public void removeStackProcessor(final StackProcessorInterface pStackProcessor);

	public ObjectVariable<StackInterface> getInputVariable();

	public ObjectVariable<StackInterface> getOutputVariable();


}
