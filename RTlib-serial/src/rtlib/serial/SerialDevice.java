package rtlib.serial;

import jssc.SerialPortException;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.serial.adapters.SerialBinaryDeviceAdapter;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class SerialDevice extends NamedVirtualDevice implements
																										OpenCloseDeviceInterface
{

	private final Serial mSerial;
	private final String mPortName;
	private final VariableBundle mVariableBundle;

	private final Object mDeviceLock = new Object();

	public SerialDevice(final String pDeviceName,
											final String pPortName,
											final int pBaudRate)
	{
		super(pDeviceName);
		mPortName = pPortName;
		mSerial = new Serial(pBaudRate);
		getSerial().setNotifyEvents(false);

		mVariableBundle = new VariableBundle(String.format(	"$s($s)",
																												pDeviceName,
																												pPortName));

	}

	public Serial getSerial()
	{
		return mSerial;
	}

	public void removeAllVariables()
	{
		mVariableBundle.removeAllListeners();
		mVariableBundle.removeAllVariables();
	}

	public DoubleVariable addSerialDoubleVariable(final String pVariableName,
																								final SerialBinaryDeviceAdapter pSerialBinaryDevice)
	{
		final DoubleVariable lDoubleVariable = new DoubleVariable(pVariableName)
		{

			@Override
			public Double getEventHook(final Double pCurrentValue)
			{
				try
				{
					final byte[] cGetValueCommand = pSerialBinaryDevice.getGetValueCommandMessage();

					if (cGetValueCommand != null)
					{
						synchronized (mDeviceLock)
						{
							getSerial().setBinaryMode(true);
							getSerial().setMessageLength(pSerialBinaryDevice.getGetValueReturnMessageLength());
							getSerial().write(cGetValueCommand);
							sleep(pSerialBinaryDevice.getGetValueReturnWaitTimeInMilliseconds());
							if (pSerialBinaryDevice.hasResponseForGet() && pSerialBinaryDevice.getGetValueReturnMessageLength() > 0)
							{
								final byte[] lAnswerMessage = getSerial().readBinaryMessage();

								if (pSerialBinaryDevice.purgeAfterGet())
								{
									mSerial.purge();
								}

								final Double lParsedValue = pSerialBinaryDevice.parseValue(lAnswerMessage);
								if (lParsedValue != null)
									return super.getEventHook(lParsedValue);
								else
									return super.getEventHook(pCurrentValue);
							}

						}
					}
				}
				catch (final SerialPortException e)
				{
					// TODO handle error
					return super.getEventHook(pCurrentValue);
				}
				return super.getEventHook(pCurrentValue);
			}

			@Override
			public Double setEventHook(	final Double pOldValue,
																	Double pNewValue)
			{
				try
				{
					pNewValue = pSerialBinaryDevice.clampSetValue(pNewValue);

					final byte[] lSetValueCommandMessage = pSerialBinaryDevice.getSetValueCommandMessage(	pOldValue,
																																																pNewValue);
					if (lSetValueCommandMessage != null)
					{
						synchronized (mDeviceLock)
						{
							getSerial().setBinaryMode(true);
							getSerial().setMessageLength(pSerialBinaryDevice.getSetValueReturnMessageLength());
							getSerial().write(lSetValueCommandMessage);
							sleep(pSerialBinaryDevice.getSetValueReturnWaitTimeInMilliseconds());
							if (pSerialBinaryDevice.hasResponseForSet() && pSerialBinaryDevice.getSetValueReturnMessageLength() > 0)
							{
								final byte[] lAnswerMessage = getSerial().readBinaryMessage();
								if (lAnswerMessage != null)
								{
									pSerialBinaryDevice.checkAcknowledgementSetValueReturnMessage(lAnswerMessage);
								}
							}

							if (pSerialBinaryDevice.purgeAfterSet())
							{
								mSerial.purge();
							}
						}
					}
				}
				catch (final SerialPortException e)
				{
					// TODO handle error
					return super.setEventHook(pOldValue, pNewValue);
				}
				return super.setEventHook(pOldValue, pNewValue);
			}

		};

		mVariableBundle.addVariable(lDoubleVariable);
		return lDoubleVariable;
	}

	public DoubleVariable addSerialDoubleVariable(final String pVariableName,
																								final SerialTextDeviceAdapter pSerialTextDeviceAdapter)
	{
		final DoubleVariable lDoubleVariable = new DoubleVariable(pVariableName)
		{

			@Override
			public Double getEventHook(final Double pCurrentValue)
			{
				try
				{
					final byte[] cGetValueCommand = pSerialTextDeviceAdapter.getGetValueCommandMessage();

					if (cGetValueCommand != null && getSerial().isConnected())
					{
						synchronized (mDeviceLock)
						{
							getSerial().setBinaryMode(false);
							getSerial().setLineTerminationCharacter(pSerialTextDeviceAdapter.getGetValueReturnMessageTerminationCharacter());
							getSerial().write(cGetValueCommand);
							sleep(pSerialTextDeviceAdapter.getGetValueReturnWaitTimeInMilliseconds());
							if (pSerialTextDeviceAdapter.hasResponseForGet())
							{
								final byte[] lAnswerMessage = getSerial().readTextMessage();

								if (pSerialTextDeviceAdapter.purgeAfterGet())
								{
									mSerial.purge();
								}

								final Double lParsedValue = pSerialTextDeviceAdapter.parseValue(lAnswerMessage);
								if(lParsedValue!=null)
									return super.getEventHook(lParsedValue);
								else
									return super.getEventHook(pCurrentValue);
							}

						}
					}
				}
				catch (final SerialPortException e)
				{
					// TODO handle error
					return pCurrentValue;
				}
				return Double.NaN;
			}

			@Override
			public Double setEventHook(	final Double pOldValue,
																	Double pNewValue)
			{
				try
				{
					pNewValue = pSerialTextDeviceAdapter.clampSetValue(pNewValue);

					final byte[] lSetValueCommandMessage = pSerialTextDeviceAdapter.getSetValueCommandMessage(pOldValue,
																																																		pNewValue);
					if (lSetValueCommandMessage != null && getSerial().isConnected())
					{
						synchronized (mDeviceLock)
						{
							getSerial().setBinaryMode(false);
							getSerial().setLineTerminationCharacter(pSerialTextDeviceAdapter.getSetValueReturnMessageTerminationCharacter());
							getSerial().write(lSetValueCommandMessage);
							sleep(pSerialTextDeviceAdapter.getSetValueReturnWaitTimeInMilliseconds());
							if (pSerialTextDeviceAdapter.hasResponseForSet())
							{
								final byte[] lAnswerMessage = getSerial().readTextMessage();
								if (lAnswerMessage != null)
								{
									pSerialTextDeviceAdapter.checkAcknowledgementSetValueReturnMessage(lAnswerMessage);
								}
							}

							if (pSerialTextDeviceAdapter.purgeAfterSet())
							{
								mSerial.purge();
							}
						}
					}
				}
				catch (final SerialPortException e)
				{
					// TODO handle error
				}
				return super.setEventHook(pOldValue, pNewValue);
			}

		};

		mVariableBundle.addVariable(lDoubleVariable);
		return lDoubleVariable;
	}

	protected void sleep(final long pSleepTimeInMilliseconds)
	{
		if (pSleepTimeInMilliseconds > 0)
		{
			try
			{
				Thread.sleep(pSleepTimeInMilliseconds);
			}
			catch (final InterruptedException e)
			{
			}
		}
	}

	public BooleanVariable addSerialBooleanVariable(final String pVariableName,
																									final SerialBinaryDeviceAdapter pSerialBinaryDevice)
	{
		final DoubleVariable lDoubleVariable = addSerialDoubleVariable(	pVariableName,
																																		pSerialBinaryDevice);

		final BooleanVariable lProxyBooleanVariable = new BooleanVariable(pVariableName,
																																			false);
		lProxyBooleanVariable.syncWith(lDoubleVariable);

		return lProxyBooleanVariable;
	}

	public BooleanVariable addSerialBooleanVariable(final String pVariableName,
																									final SerialTextDeviceAdapter pSerialTextDeviceAdapter)
	{
		final DoubleVariable lDoubleVariable = addSerialDoubleVariable(	pVariableName,
																																		pSerialTextDeviceAdapter);

		final BooleanVariable lProxyBooleanVariable = new BooleanVariable(pVariableName,
																																			false)
		{

			@Override
			public Double getEventHook(Double pCurrentValue)
			{
				return lDoubleVariable.get();
			}

		};
		lProxyBooleanVariable.syncWith(lDoubleVariable);

		return lProxyBooleanVariable;
	}/**/

	public final VariableBundle getVariableBundle()
	{
		return mVariableBundle;
	}

	public final VariableInterface<Double> getVariableByName(final String pVariableName)
	{
		return mVariableBundle.getVariable(pVariableName);
	}

	public final DoubleVariable getDoubleVariableByName(final String pVariableName)
	{
		final Object lVariable = mVariableBundle.getVariable(pVariableName);
		if (lVariable instanceof DoubleVariable)
		{
			return (DoubleVariable) lVariable;
		}
		return null;
	}

	@Override
	public boolean open()
	{
		try
		{
			final boolean lConnected = getSerial().connect(mPortName);
			getSerial().purge();
			return lConnected;
		}
		catch (final SerialPortException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean close()
	{
		try
		{
			getSerial().close();
			return true;
		}
		catch (final SerialPortException e)
		{
			e.printStackTrace();
			return false;
		}

	}

}
