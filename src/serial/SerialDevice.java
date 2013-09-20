package serial;

import jssc.SerialPortException;
import serialcom.Serial;
import serialcom.SerialInterface;
import serialcom.SerialListener;
import serialcom.SerialListenerAdapter;
import thread.EnhancedThread;
import variable.VariableInterface;
import variable.bundle.VariableBundle;
import variable.doublev.DoubleVariable;
import device.NamedDevice;
import device.VirtualDevice;

public class SerialDevice extends NamedDevice	implements
																							VirtualDevice
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
				catch (final SerialPortException e)
				{
					// TODO handle error
					return pCurrentValue;
				}

			}

			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				try
				{
					synchronized (mDeviceLock)
					{
						mSerial.setBinaryMode(true);
						mSerial.setMessageLength(pSerialBinaryDevice.getSetValueReturnMessageLength());
						mSerial.write(pSerialBinaryDevice.getSetValueCommandMessage(pNewValue));
						sleep(pSerialBinaryDevice.getSetValueReturnWaitTimeInMilliseconds());
						final byte[] lAnswerMessage = mSerial.readBinaryMessage();
						if (lAnswerMessage != null)
							pSerialBinaryDevice.checkAcknowledgementSetValueReturnMessage(lAnswerMessage);
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

	protected void sleep(long pSleepTimeInMilliseconds)
	{
		try
		{
			Thread.sleep(pSleepTimeInMilliseconds);
		}
		catch (InterruptedException e)
		{
		}
	}

	public final VariableBundle getVariableBundle()
	{
		return mVariableBundle;
	}

	public final VariableInterface<Double> getVariableByName(String pVariableName)
	{
		return mVariableBundle.getVariable(pVariableName);
	}

	public final DoubleVariable getDoubleVariableByName(String pVariableName)
	{
		final Object lVariable = (Object) mVariableBundle.getVariable(pVariableName);
		if (lVariable instanceof DoubleVariable)
			return (DoubleVariable) (lVariable);
		return null;
	}

	@Override
	public boolean open()
	{
		try
		{
			return mSerial.connect(mPortName);
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
