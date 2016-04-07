package rtlib.serial;

public class SerialListenerAdapter implements SerialListener
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
		System.out.format("%s\n", pSerial.toString());
		pException.printStackTrace();
	}
}
