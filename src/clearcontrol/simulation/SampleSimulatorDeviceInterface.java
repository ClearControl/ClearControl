package clearcontrol.simulation;

import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.simulation.impl.lightsheet.LightSheetSimulationStackProvider;

/**
 * Sample simulation device interface
 *
 * @author royer
 */
public interface SampleSimulatorDeviceInterface extends
                                                SimulationDeviceInterface
{

  /**
   * Returns the stack provider for this sample simulator
   * 
   * @param pIndex
   *          index of stack provider
   * @return stack provider
   */
  LightSheetSimulationStackProvider getStackProvider(int pIndex);

  /**
   * Connects this simulator to all relevant devices of (most logically
   * simulated) microscope, such as stage, laser, detection and illumination
   * arms... Changes in the stage position, laser power, will be reflected by
   * the simulator.
   * 
   * IMPORTANT: The 'connection' must of course happen after the microscope
   * devices have been all set up...
   * 
   * @param pMicroscope
   *          microscope
   */
  void connectTo(MicroscopeInterface pMicroscope);

}
