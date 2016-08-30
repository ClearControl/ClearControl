package clearcontrol.com.serial;

import clearcontrol.core.log.LoggingInterface;

public class SerialListenerAdapter implements SerialListener, LoggingInterface
{

	@Override
	public void textMessageReceived(final SerialInterface pSerial,
																	final String pMessage)
	{
	}

	@Override
	public void binaryMessageReceived(final SerialInterface pSerial,
																		final byte[] pMessage)
	{
	}

	@Override
	public void errorOccured(	final Serial pSerial,
														final Throwable pException)
	{
		warning(pSerial.toString());
		pException.printStackTrace();
	}
}
