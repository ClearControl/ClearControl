package clearcontrol.stack.processor;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
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
