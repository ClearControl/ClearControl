package clearcontrol.devices.lasers.devices.omicron.adapters.protocol.test;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

import org.junit.Test;

/**
 * Omicron protocol tests
 *
 * @author royer
 */
public class ProtocolOmicronTests
{

  /**
   * Tests splitting of message
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testSplitMessage() throws InterruptedException
  {
    final String lTestMessage =
                              new String("!GFwLuxX\u00A74\u00A71.30.");
    final String[] lSplitMessage =
                                 ProtocolOmicron.splitMessage("!GFw",
                                                              lTestMessage.getBytes(StandardCharsets.ISO_8859_1));
    assertEquals(lSplitMessage[0], "LuxX");
    assertEquals(lSplitMessage[1], "4");
    assertEquals(lSplitMessage[2], "1.30.");
    System.out.println(Arrays.toString(lSplitMessage));
  }

  /**
   * Tests conversion to hexadec string.
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testToHexadecimalString() throws InterruptedException
  {
    assertEquals(ProtocolOmicron.toHexadecimalString(4095, 3), "FFF");
    assertEquals(ProtocolOmicron.toHexadecimalString(0000, 3), "000");
  }

}
