package rtlib.stack.processor;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.Variable;
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

	public Variable<StackInterface> getInputVariable();

	public Variable<StackInterface> getOutputVariable();

}
