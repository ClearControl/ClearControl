package clearcontrol.com.serial;

import clearcontrol.com.serial.adapters.SerialBinaryDeviceAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bundle.VariableBundle;
import clearcontrol.device.VirtualDevice;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import jssc.SerialPortException;

public class SerialDevice extends VirtualDevice implements
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

	public <O> Variable<O> addSerialVariable(	final String pVariableName,
																									final SerialBinaryDeviceAdapter<O> pSerialBinaryDevice)
	{
		final Variable<O> lObjectVariable = new Variable<O>(pVariableName)
		{

			@Override
			public O getEventHook(final O pCurrentValue)
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

								final O lParsedValue = pSerialBinaryDevice.parseValue(lAnswerMessage);
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
			public O setEventHook(final O pOldValue, O pNewValue)
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

		mVariableBundle.addVariable(lObjectVariable);
		return lObjectVariable;
	}

	public <O> Variable<O> addSerialVariable(	final String pVariableName,
																									final SerialTextDeviceAdapter<O> pSerialTextDeviceAdapter)
	{
		final Variable<O> lObjectVariable = new Variable<O>(pVariableName)
		{

			@Override
			public O getEventHook(final O pCurrentValue)
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

								final O lParsedValue = pSerialTextDeviceAdapter.parseValue(lAnswerMessage);
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
					return pCurrentValue;
				}
				return null;
			}

			@Override
			public O setEventHook(final O pOldValue, O pNewValue)
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

		mVariableBundle.addVariable(lObjectVariable);
		return lObjectVariable;
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

	public final VariableBundle getVariableBundle()
	{
		return mVariableBundle;
	}

	public final Variable<Double> getDoubleVariableByName(final String pVariableName)
	{
		return mVariableBundle.getVariable(pVariableName);
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
