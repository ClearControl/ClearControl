package serial;

import jssc.SerialPortException;
import serialcom.Serial;
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

	public SerialDevice(final String pDeviceName, final String pPortName)
	{
		super(pDeviceName);
		mPortName = pPortName;
		mSerial = new Serial(115200);
		mVariableBundle = new VariableBundle(String.format(	"$s($s)",
																												pDeviceName,
																												pPortName));
	}

	public void addSerialVariable(final String pVariableName,
																final SerialBinaryDevice pSerialBinaryDevice)
	{
		final DoubleVariable lDoubleVariable = new DoubleVariable(pVariableName)
		{
			final byte[] cGetValueCommand = pSerialBinaryDevice.getGetValueCommandMessage();

			@Override
			public double getEventHook(final double pCurrentValue)
			{
				try
				{
					mSerial.setBinaryMode(true);
					mSerial.setMessageLength(pSerialBinaryDevice.getGetValueReturnMessageLength());
					mSerial.write(cGetValueCommand);
					final byte[] lAnswerMessage = mSerial.readBinaryMessage();
					return pSerialBinaryDevice.parseValue(lAnswerMessage);
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
					mSerial.setBinaryMode(true);
					mSerial.setMessageLength(pSerialBinaryDevice.getGetValueReturnMessageLength());
					mSerial.write(pSerialBinaryDevice.getSetValueCommandMessage(pNewValue));
					final byte[] lAnswerMessage = mSerial.readBinaryMessage();
					pSerialBinaryDevice.checkAcknowledgementSetValueReturnMessage(lAnswerMessage);
				}
				catch (final SerialPortException e)
				{
					// TODO handle error
				}
				return super.setEventHook(pOldValue, pNewValue);
			}

		};

		mVariableBundle.addVariable(lDoubleVariable);
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
			return false;
		}

	}

}
