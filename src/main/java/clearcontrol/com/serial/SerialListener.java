package clearcontrol.com.serial;

public interface SerialListener
{

  void textMessageReceived(SerialInterface pSerial, String pMessage);

  void binaryMessageReceived(SerialInterface pSerial,
                             byte[] pMessage);

  void errorOccured(Serial pSerial, Throwable pException);

}
