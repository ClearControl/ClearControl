package serialcom.test;

import jssc.SerialPortException;

import org.junit.Test;

import serialcom.Serial;
import serialcom.SerialInterface;
import serialcom.SerialListener;

public class SerialTests
{

	@Test
	public void test() throws InterruptedException, SerialPortException
	{
		/*final ArrayList<String> lListOfAllSerialCommPorts = Serial.getListOfAllSerialCommPorts();
		for (final String lPortName : lListOfAllSerialCommPorts)
			System.out.println(lPortName);/**/

		final Serial lSerial = new Serial("Egg3D", 115200);
		lSerial.setBinaryMode(true);
		lSerial.setMessageLength(18);

		lSerial.addListener(new SerialListener()
		{

			@Override
			public void textMessageReceived(final SerialInterface pSerial,
																			final String pMessage)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void binaryMessageReceived(final SerialInterface pSerial,
																				final byte[] pMessage)
			{
				System.out.format("message received length=%d, index=%d \n",
													pMessage.length,
													pMessage[0]);// TODO
				// Auto-generated
				// method
				// stub

			}

			@Override
			public void errorOccured(	final Serial pSerial,
																final Throwable pException)
			{
				// TODO Auto-generated method stub

			}
		});

		System.out.println("Connecting...");
		lSerial.connect();

		Thread.sleep(1000000);
		lSerial.close();
	}
}
