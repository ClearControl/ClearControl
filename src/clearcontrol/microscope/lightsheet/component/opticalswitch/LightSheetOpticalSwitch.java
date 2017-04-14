package clearcontrol.microscope.lightsheet.component.opticalswitch;

import java.util.concurrent.Future;

import clearcontrol.core.device.QueueableVirtualDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.devices.optomech.opticalswitch.OpticalSwitchDeviceInterface;

/**
 * Lightsheet optical switch
 *
 * @author royer
 */
public class LightSheetOpticalSwitch extends
                                     QueueableVirtualDevice<LightSheetOpticalSwitchQueue>
                                     implements
                                     OpticalSwitchDeviceInterface
{

  private int mNumberOfLightSheets;

  private LightSheetOpticalSwitchQueue mLightSheetOpticalSwitchQueueTemplate;

  /**
   * Instanciates a lightsheet optical switch
   * 
   * @param pName
   *          device name
   * @param pNumberOfLightSheets
   *          number of lightsheets
   */
  public LightSheetOpticalSwitch(String pName,
                                 int pNumberOfLightSheets)
  {
    super(pName);
    mNumberOfLightSheets = pNumberOfLightSheets;

    mLightSheetOpticalSwitchQueueTemplate =
                                          new LightSheetOpticalSwitchQueue(this);

    final VariableSetListener<Boolean> lBooleanVariableListener =
                                                                (u,
                                                                 v) -> {
                                                                  if (u != v)
                                                                  {
                                                                    notifyListeners(this);
                                                                  }
                                                                };

    for (int i = 0; i < mNumberOfLightSheets; i++)
    {
      mLightSheetOpticalSwitchQueueTemplate.getSwitchVariable(i)
                                           .addSetListener(lBooleanVariableListener);
    }

    notifyListeners(this);
  }

  @Override
  public String getSwitchName(int pSwitchIndex)
  {
    return "light sheet " + pSwitchIndex;
  }

  @Override
  public int getNumberOfSwitches()
  {
    return mNumberOfLightSheets;
  }

  @Override
  public Variable<Boolean> getSwitchVariable(int pLightSheetIndex)
  {
    return mLightSheetOpticalSwitchQueueTemplate.getSwitchVariable(pLightSheetIndex);
  }

  @Override
  public LightSheetOpticalSwitchQueue requestQueue()
  {
    return new LightSheetOpticalSwitchQueue(mLightSheetOpticalSwitchQueueTemplate);
  }

  @Override
  public Future<Boolean> playQueue(LightSheetOpticalSwitchQueue pQueue)
  {
    // do nothing
    return null;
  }

}
