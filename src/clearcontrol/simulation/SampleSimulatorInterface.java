package clearcontrol.simulation;

import clearcontrol.device.name.NameableInterface;
import clearcontrol.stack.StackProvider;

/**
 * Created by dibrov on 10/03/17.
 */
public interface SampleSimulatorInterface extends NameableInterface{

    StackProvider getStackProvider(long pIndex);
    public long[] getDimensions();
}
