package clearcontrol.stack.processor;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclableFactoryInterface;

public interface StackProcessingPipeline extends
																				OpenCloseDeviceInterface
{

	void addStackProcessor(	StackProcessorInterface pStackProcessor,
													RecyclableFactoryInterface<StackInterface, StackRequest> pStackFactory,
													int pMaximumNumberOfObjects);

	public void removeStackProcessor(final StackProcessorInterface pStackProcessor);

	public Variable<StackInterface> getInputVariable();

	public Variable<StackInterface> getOutputVariable();

}
