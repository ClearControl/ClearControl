package rtlib.serialdevice.laser.omicron.adapters.protocol.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import rtlib.serialdevice.laser.omicron.adapters.protocol.ProtocolXX;

public class ProtocolXXTests
{

	@Test
	public void testSplitMessage() throws InterruptedException
	{
		final String lTestMessage = new String("!GFwLuxX񘄵.30.");
		final String[] lSplitMessage = ProtocolXX.splitMessage(lTestMessage.getBytes());
		assertEquals(lSplitMessage[0], "LuxX");
		assertEquals(lSplitMessage[1], "4");
		assertEquals(lSplitMessage[2], "1.30.");
		System.out.println(Arrays.toString(lSplitMessage));
	}

	@Test
	public void testToHexadecimalString() throws InterruptedException
	{
		assertEquals(ProtocolXX.toHexadecimalString(4095, 3), "FFF");
		assertEquals(ProtocolXX.toHexadecimalString(0000, 3), "000");
	}

}
