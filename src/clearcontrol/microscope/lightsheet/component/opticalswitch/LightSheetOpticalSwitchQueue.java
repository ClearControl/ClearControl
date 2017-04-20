package clearcontrol.microscope.lightsheet.component.opticalswitch;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.core.variable.Variable;

/**
 * Lightsheet optical switch queue
 *
 * @author royer
 */
public class LightSheetOpticalSwitchQueue extends VariableQueueBase
                                          implements QueueInterface
{
  private final LightSheetOpticalSwitch mLightSheetOpticalSwitch;

  private final Variable<Boolean>[] mLightSheetOnOff;

  /**
   * Instanciates a lightsheet optical switch queue given a parent lightsheet
   * optical device
   * 
   * @param pLightSheetOpticalSwitch
   *          parent lightsheet optical device
   */
  @SuppressWarnings("unchecked")
  public LightSheetOpticalSwitchQueue(LightSheetOpticalSwitch pLightSheetOpticalSwitch)
  {

    mLightSheetOpticalSwitch = pLightSheetOpticalSwitch;
    mLightSheetOnOff =
                     new Variable[pLightSheetOpticalSwitch.getNumberOfSwitches()];

    for (int i = 0; i < mLightSheetOnOff.length; i++)
    {
      mLightSheetOnOff[i] = new Variable<Boolean>(
                                                  String.format("LightSheet%dOnOff",
                                                                i),
                                                  false);
      registerVariable(mLightSheetOnOff[i]);
    }
  }

  /**
   * Instanciates a lightsheet optical switch queue given a template queue
   * current state.
   * 
   * @param pLightSheetOpticalSwitchQueue
   *          template queue
   */
  public LightSheetOpticalSwitchQueue(LightSheetOpticalSwitchQueue pLightSheetOpticalSwitchQueue)
  {
    this(pLightSheetOpticalSwitchQueue.getLightSheetOpticalSwitch());

    for (int i = 0; i < mLightSheetOnOff.length; i++)
    {
      Variable<Boolean> lSwitchVariable =
                                        pLightSheetOpticalSwitchQueue.getSwitchVariable(i);
      mLightSheetOnOff[i].set(lSwitchVariable.get());
    }
  }

  /**
   * Returns the parent lightsheet optical switch
   * 
   * @return parent lightsheet optical switch
   */
  public LightSheetOpticalSwitch getLightSheetOpticalSwitch()
  {
    return mLightSheetOpticalSwitch;
  }

  /**
   * Returns switch variable for a given lightsheet index
   * 
   * @param pLightSheetIndex
   *          lights heet index
   * @return switch variable
   */
  public Variable<Boolean> getSwitchVariable(int pLightSheetIndex)
  {
    return mLightSheetOnOff[pLightSheetIndex];
  }

  /**
   * Returns the number of switches
   * 
   * @return number of switches
   */
  public int getNumberOfSwitches()
  {
    return getLightSheetOpticalSwitch().getNumberOfSwitches();
  }

}
