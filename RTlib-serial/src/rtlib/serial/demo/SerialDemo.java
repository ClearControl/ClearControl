package rtlib.serial.demo;

import jssc.SerialPortException;

import org.junit.Test;

import rtlib.serial.Serial;
import rtlib.serial.SerialException;
import rtlib.serial.SerialInterface;
import rtlib.serial.SerialListenerAdapter;

public class SerialDemo
{

	@Test
	public void test() throws InterruptedException,
										SerialPortException,
										SerialException
	{

		final Serial lSerial = new Serial("Egg3D", 115200);
		lSerial.setBinaryMode(true);
		lSerial.setMessageLength(18);

		lSerial.addListener(new SerialListenerAdapter()
		{

			@Override
			public void binaryMessageReceived(final SerialInterface pSerial,
																				final byte[] pMessage)
			{
				System.out.format("message received length=%d, index=%d \n",
													pMessage.length,
													pMessage[0]);// TODO
			}

		});

		System.out.println("Connecting...");
		lSerial.connect();

		Thread.sleep(1000000);
		lSerial.close();
	}
}
