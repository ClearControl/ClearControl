package rtlib.stack.processor;

import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public interface StackProcessorInterface
{

	public void setActive(boolean pIsActive);

	public boolean isActive();

	public StackInterface process(StackInterface pStack,
																RecyclerInterface<StackInterface, StackRequest> pStackRecycler);

}
