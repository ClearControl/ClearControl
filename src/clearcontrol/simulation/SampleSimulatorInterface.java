package clearcontrol.simulation;

import clearcontrol.stack.StackProvider;

/**
 * Created by dibrov on 10/03/17.
 */
public interface SampleSimulatorInterface
{

  StackProvider getStackProvider(long pIndex);

  public long[] getDimensions();
}
