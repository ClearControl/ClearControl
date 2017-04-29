package clearcontrol.microscope.lightsheet.signalgen;

import java.util.concurrent.Future;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.queue.QueueDeviceInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.signalgen.SignalGeneratorQueue;

/**
 * This device knows how to generate the signals for a light sheet microscope
 * (both detection and illumination signals)
 *
 * @author royer
 */
public class LightSheetSignalGeneratorDevice extends VirtualDevice
                                             implements
                                             QueueDeviceInterface<LightSheetSignalGeneratorQueue>,
                                             LoggingInterface
{

  private final SignalGeneratorInterface mDelegatedSignalGenerator;

  private Variable<Boolean> mIsSharedLightSheetControlVariable =
                                                               new Variable<Boolean>("IsSharedLightSheetControl",
                                                                                     true);
  private Variable<Integer> mIsSelectedLightSheetIndexVariable =
                                                               new Variable<Integer>("IsSelectedLightSheetIndex",
                                                                                     0);

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
   * Instantiates a lightsheet signal generator that delegates to another signal
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
  public boolean open()
  {
    return super.open() && mDelegatedSignalGenerator.open();
  }

  @Override
  public boolean close()
  {
    return mDelegatedSignalGenerator.close() && super.close();
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
    SignalGeneratorQueue lDelegatedQueue = pQueue.getDelegatedQueue();
    return mDelegatedSignalGenerator.playQueue(lDelegatedQueue);
  }

  /**
   * Returns the variable that holds the 'is-shared-lightsheet-control'. When
   * all lightsheets share the same digital/control lines, one needs to decide
   * which lightsheet will be used for generating the control signals.
   * 
   * @return is-shared-lightsheet-control variable
   */
  public Variable<Boolean> getIsSharedLightSheetControlVariable()
  {
    return mIsSharedLightSheetControlVariable;
  }

  /**
   * In the case that we are in a shared lightsheet control situation, this
   * variable holds the index of the lightsheet to use to generate the control
   * signals.
   * 
   * @return  is-selected-lightsheet variable
   */
  public Variable<Integer> getIsSelectedLightSheetIndexVariable()
  {
    return mIsSelectedLightSheetIndexVariable;
  }

}
