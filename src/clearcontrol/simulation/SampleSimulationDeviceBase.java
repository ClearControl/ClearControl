package clearcontrol.simulation;

import clearcontrol.core.device.queue.QueueInterface;

/**
 * base class for all sample simulation devices
 *
 * @author royer
 * @param <Q>
 *          queue type
 */
public abstract class SampleSimulationDeviceBase<Q extends QueueInterface>
                                                implements
                                                SampleSimulationDeviceInterface<Q>

{

}
