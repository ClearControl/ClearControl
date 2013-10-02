package serial;

import jssc.SerialPortException;
import serial.adapters.SerialBinaryDeviceAdapter;
import serial.adapters.SerialTextDeviceAdapter;
import serialcom.Serial;
import variable.VariableInterface;
import variable.booleanv.BooleanVariable;
import variable.bundle.VariableBundle;
import variable.doublev.DoubleVariable;
import device.NamedVirtualDevice;
import device.VirtualDeviceInterface;

public class SerialDevice extends NamedVirtualDevice implements
																										VirtualDeviceInterface
{

	protected final Serial mSerial;
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
		mSerial.setNotifyEvents(false);

		mVariableBundle = new VariableBundle(String.format(	"$s($s)",
																												pDeviceName,
																												pPortName));

	}

	public DoubleVariable addSerialDoubleVariable(final String pVariableName,
																								final SerialBinaryDeviceAdapter pSerialBinaryDevice)
	{
		final DoubleVariable lDoubleVariable = new DoubleVariable(pVariableName)
		{
			final byte[] cGetValueCommand = pSerialBinaryDevice.getGetValueCommandMessage();

			@Override
			public double getEventHook(final double pCurrentValue)
			{
				try
				{
					if (cGetValueCommand != null)
					{
						synchronized (mDeviceLock)
						{
							mSerial.setBinaryMode(true);
							mSerial.setMessageLength(pSerialBinaryDevice.getGetValueReturnMessageLength());
							mSerial.write(cGetValueCommand);
							sleep(pSerialBinaryDevice.getGetValueReturnWaitTimeInMilliseconds());
							final byte[] lAnswerMessage = mSerial.readBinaryMessage();
							return pSerialBinaryDevice.parseValue(lAnswerMessage);
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
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				try
				{
					final byte[] lSetValueCommandMessage = pSerialBinaryDevice.getSetValueCommandMessage(pNewValue);
					if (lSetValueCommandMessage != null)
					{
						synchronized (mDeviceLock)
						{
							mSerial.setBinaryMode(true);
							mSerial.setMessageLength(pSerialBinaryDevice.getSetValueReturnMessageLength());
							mSerial.write(lSetValueCommandMessage);
							if (pSerialBinaryDevice.getSetValueReturnMessageLength() > 0)
							{
								sleep(pSerialBinaryDevice.getSetValueReturnWaitTimeInMilliseconds());
								final byte[] lAnswerMessage = mSerial.readBinaryMessage();
								if (lAnswerMessage != null)
								{
									pSerialBinaryDevice.checkAcknowledgementSetValueReturnMessage(lAnswerMessage);
								}
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

	public DoubleVariable addSerialDoubleVariable(final String pVariableName,
																								final SerialTextDeviceAdapter pSerialTextDeviceAdapter)
	{
		final DoubleVariable lDoubleVariable = new DoubleVariable(pVariableName)
		{
			final byte[] cGetValueCommand = pSerialTextDeviceAdapter.getGetValueCommandMessage();

			@Override
			public double getEventHook(final double pCurrentValue)
			{
				try
				{
					if (cGetValueCommand != null && mSerial.isConnected())
					{
						synchronized (mDeviceLock)
						{
							mSerial.setBinaryMode(false);
							mSerial.setLineTerminationCharacter(pSerialTextDeviceAdapter.getGetValueReturnMessageTerminationCharacter());
							mSerial.write(cGetValueCommand);
							sleep(pSerialTextDeviceAdapter.getGetValueReturnWaitTimeInMilliseconds());
							final byte[] lAnswerMessage = mSerial.readTextMessage();
							return pSerialTextDeviceAdapter.parseValue(lAnswerMessage);
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
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				try
				{
					final byte[] lSetValueCommandMessage = pSerialTextDeviceAdapter.getSetValueCommandMessage(pNewValue);
					if (lSetValueCommandMessage != null && mSerial.isConnected())
					{
						synchronized (mDeviceLock)
						{
							mSerial.setBinaryMode(false);
							mSerial.setLineTerminationCharacter(pSerialTextDeviceAdapter.getSetValueReturnMessageTerminationCharacter());
							mSerial.write(lSetValueCommandMessage);
							sleep(pSerialTextDeviceAdapter.getSetValueReturnWaitTimeInMilliseconds());
							final byte[] lAnswerMessage = mSerial.readTextMessage();
							if (lAnswerMessage != null)
							{
								pSerialTextDeviceAdapter.checkAcknowledgementSetValueReturnMessage(lAnswerMessage);
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
																																			false);
		lProxyBooleanVariable.syncWith(lDoubleVariable);

		return lProxyBooleanVariable;
	}

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
			final boolean lConnected = mSerial.connect(mPortName);
			mSerial.purge();
			return lConnected;
		}
		catch (final SerialPortException e)
		{
			return false;
		}
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		try
		{
			mSerial.close();
			return true;
		}
		catch (final SerialPortException e)
		{
			e.printStackTrace();
			return false;
		}

	}

}
