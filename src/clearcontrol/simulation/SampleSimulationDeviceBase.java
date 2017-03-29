package clearcontrol.simulation;

import clearcontrol.core.device.queue.RealTimeQueueInterface;

/**
 * base class for all sample simulation devices
 *
 * @author royer
 * @param <Q>
 *          queue type
 */
public abstract class SampleSimulationDeviceBase<Q extends RealTimeQueueInterface>
                                                implements
                                                SampleSimulationDeviceInterface<Q>

{

}
