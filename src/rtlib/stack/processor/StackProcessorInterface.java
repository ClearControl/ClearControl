package rtlib.stack.processor;

import coremem.recycling.RecyclerInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public interface StackProcessorInterface
{

	public void setActive(boolean pIsActive);

	public boolean isActive();

	public StackInterface process(StackInterface pStack,
																RecyclerInterface<StackInterface, StackRequest> pStackRecycler);

}
