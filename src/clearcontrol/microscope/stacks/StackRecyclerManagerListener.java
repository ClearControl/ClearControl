package clearcontrol.microscope.stacks;

import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * StackRecyclerManagerListener is a listener interface.
 * 
 * @author royer
 */
public interface StackRecyclerManagerListener
{

	void update(ConcurrentHashMap<String, RecyclerInterface<StackInterface, StackRequest>> pRecyclerMap);

}
