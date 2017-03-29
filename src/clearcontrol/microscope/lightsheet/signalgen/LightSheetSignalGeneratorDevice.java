package clearcontrol.microscope.lightsheet.signalgen;

import java.util.concurrent.Future;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.queue.RealTimeQueueDeviceInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;

/**
 * This device knows how to generate the signals for a light sheet microscope
 * (both detection and illumination signals)
 *
 * @author royer
 */
public class LightSheetSignalGeneratorDevice extends VirtualDevice
                                             implements
                                             RealTimeQueueDeviceInterface<LightSheetSignalGeneratorQueue>,
                                             LoggingInterface
{

  private final SignalGeneratorInterface mDelegatedSignalGenerator;

  /**
   * Wraps a signal generator with a lightsheet signal generation. This
   * lightsheet signal generator simply adds a layer that translate detection
   * arm and lightsheet parameters to actual signals.
   * 
   * @param pSignalGeneratorInterface
   *          delegated signal generator
   * @return lightsheet signal generator
   */
  public static LightSheetSignalGeneratorDevice wrap(SignalGeneratorInterface pSignalGeneratorInterface)
  {
    return new LightSheetSignalGeneratorDevice(pSignalGeneratorInterface);
  }

  /**
   * Instanciates a lightsheet signal generator that delegates to another signal
   * generator for the actual signal generation. This signal generator simply
   * adds a layer that translate detection arm and lightsheet parameters to
   * actual signals.
   * 
   * @param pSignalGeneratorInterface
   *          delegated signal generator
   */
  public LightSheetSignalGeneratorDevice(SignalGeneratorInterface pSignalGeneratorInterface)
  {
    super("LightSheet" + pSignalGeneratorInterface.getName());
    mDelegatedSignalGenerator = pSignalGeneratorInterface;
  }

  @Override
  public LightSheetSignalGeneratorQueue requestQueue()
  {
    return new LightSheetSignalGeneratorQueue(this,
                                              mDelegatedSignalGenerator.requestQueue());
  }

  @Override
  public Future<Boolean> playQueue(LightSheetSignalGeneratorQueue pQueue)
  {
    // do nothing because the delegated signal generator will do the job
    return null;
  }

}
