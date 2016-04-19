package rtlib.stack.processor;

import coremem.recycling.RecyclableFactory;
import rtlib.core.variable.Variable;
import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public interface StackProcessingPipeline extends
																				OpenCloseDeviceInterface
{

	void addStackProcessor(	StackProcessorInterface pStackProcessor,
													RecyclableFactory<StackInterface, StackRequest> pStackFactory,
													int pMaximumNumberOfObjects);

	public void removeStackProcessor(final StackProcessorInterface pStackProcessor);

	public Variable<StackInterface> getInputVariable();

	public Variable<StackInterface> getOutputVariable();

}
