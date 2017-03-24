package clearcontrol.com.serial.demo;

import static org.junit.Assert.assertTrue;

import java.util.Scanner;

import jssc.SerialPortException;
import clearcontrol.com.serial.Serial;
import clearcontrol.com.serial.SerialException;
import clearcontrol.com.serial.SerialInterface;
import clearcontrol.com.serial.SerialListenerAdapter;

import org.junit.Test;

public class SerialDemo
{

  @Test
  public void serialConsoleDemo() throws InterruptedException,
                                  SerialPortException,
                                  SerialException
  {

    final Serial lSerial = new Serial(115200);
    lSerial.setBinaryMode(false);
    lSerial.setLineTerminationCharacter('\n');
    lSerial.addListener(new SerialListenerAdapter()
    {
      @Override
      public void textMessageReceived(SerialInterface pSerial,
                                      String pMessage)
      {
        super.textMessageReceived(pSerial, pMessage);

        pMessage = pMessage.replace("\r", "\\r");
        pMessage = pMessage.replace("\n", "\\n");

        System.out.println("->" + pMessage);
      }
    });

    System.out.println("Connecting...");
    assertTrue(lSerial.connect("COM1"));
    System.out.println("Connected!");

    Scanner lScanner = new Scanner(System.in);

    String lLine;
    while (!(lLine = lScanner.nextLine()).equals("exit"))
    {
      lSerial.write(lLine + "\n");
    }
    lScanner.close();

    Thread.sleep(1000);
    lSerial.close();
  }

}
