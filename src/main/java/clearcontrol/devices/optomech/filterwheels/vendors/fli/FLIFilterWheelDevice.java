package clearcontrol.devices.optomech.filterwheels.devices.fli;

import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.com.serial.SerialDevice;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.devices.optomech.filterwheels.devices.fli.adapters.FilterWheelPositionDeviceAdapter;
import clearcontrol.devices.optomech.filterwheels.devices.fli.adapters.FilterWheelSpeedDeviceAdapter;

public class FLIFilterWheelDevice extends SerialDevice implements
                                  FilterWheelDeviceInterface
{

  private final Variable<Integer> mFilterPositionVariable,
      mFilterSpeedVariable;
  private volatile int mCachedPosition, mCachedSpeed;
  private ConcurrentHashMap<Integer, String> mFilterPositionToNameMap;

  public FLIFilterWheelDevice(final int pDeviceIndex)
  {
    this(MachineConfiguration.get().getSerialDevicePort(
                                                        "filterwheel.fli",
                                                        pDeviceIndex,
                                                        "NULL"));

    for (int i : getValidPositions())
    {
      setPositionName(i,
                      MachineConfiguration.get()
                                          .getStringProperty("filterwheel.fli."
                                                             + pDeviceIndex
                                                             + "."
                                                             + i,
                                                             "filter "
                                                                  + i));
    }
  }

  public FLIFilterWheelDevice(final String pPortName)
  {
    super("FLIFilterWheel", pPortName, 9600);

    mFilterPositionToNameMap = new ConcurrentHashMap<>();

    final FilterWheelPositionDeviceAdapter lFilterWheelPosition =
                                                                new FilterWheelPositionDeviceAdapter(this);
    mFilterPositionVariable = addSerialVariable("FilterWheelPosition",
                                                lFilterWheelPosition);
    mFilterPositionVariable.addSetListener(new VariableSetListener<Integer>()
    {
      @Override
      public void setEvent(final Integer pCurrentValue,
                           final Integer pNewValue)
      {
        updateCache(pNewValue);
      }

      private void updateCache(final Integer pNewValue)
      {
        mCachedPosition =
                        (int) (pNewValue == null ? 0
                                                 : pNewValue.doubleValue());
      }
    });

    final FilterWheelSpeedDeviceAdapter lFilterWheelSpeed =
                                                          new FilterWheelSpeedDeviceAdapter(this);
    mFilterSpeedVariable = addSerialVariable("FilterWheelSpeed",
                                             lFilterWheelSpeed);
    mFilterSpeedVariable.addSetListener(new VariableSetListener<Integer>()
    {
      @Override
      public void setEvent(final Integer pCurrentValue,
                           final Integer pNewValue)
      {
        updateCache(pNewValue);
      }

      private void updateCache(final Integer pNewValue)
      {
        mCachedSpeed =
                     (int) (pNewValue == null ? 0
                                              : pNewValue.doubleValue());
      }
    });
  }

  @Override
  public final Variable<Integer> getPositionVariable()
  {
    return mFilterPositionVariable;
  }

  @Override
  public final Variable<Integer> getSpeedVariable()
  {
    return mFilterSpeedVariable;
  }

  @Override
  public int getPosition()
  {
    return mFilterPositionVariable.get();
  }

  public int getCachedPosition()
  {
    return mCachedPosition;
  }

  @Override
  public void setPosition(final int pPosition)
  {
    mFilterPositionVariable.set(pPosition);
  }

  @Override
  public int getSpeed()
  {
    return mFilterSpeedVariable.get();
  }

  public int getCachedSpeed()
  {
    return mCachedSpeed;
  }

  @Override
  public void setSpeed(final int pSpeed)
  {
    mFilterSpeedVariable.set(pSpeed);
  }

  @Override
  public boolean open()
  {
    final boolean lIsOpened = super.open();
    setSpeed(1);
    setPosition(0);
    return lIsOpened;
  }

  @Override
  public int[] getValidPositions()
  {
    return new int[]
    { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
  }

  @Override
  public void setPositionName(int pPositionIndex,
                              String pPositionName)
  {
    mFilterPositionToNameMap.put(pPositionIndex, pPositionName);

  }

  @Override
  public String getPositionName(int pPositionIndex)
  {
    return mFilterPositionToNameMap.get(pPositionIndex);
  }

}
