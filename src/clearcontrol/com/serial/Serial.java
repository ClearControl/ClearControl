package clearcontrol.com.serial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import clearcontrol.core.log.LoggingFeature;
import gnu.trove.list.array.TByteArrayList;

public class Serial implements SerialInterface, LoggingFeature
{
  public final static int cFLOWCONTROL_NONE =
                                            SerialPort.FLOWCONTROL_NONE;
  public final static int cFLOWCONTROL_RTSCTS =
                                              SerialPort.FLOWCONTROL_RTSCTS_IN
                                                | SerialPort.FLOWCONTROL_RTSCTS_OUT;
  public final static int cFLOWCONTROL_XONXOFF =
                                               SerialPort.FLOWCONTROL_XONXOFF_IN
                                                 | SerialPort.FLOWCONTROL_XONXOFF_OUT;
  private static final int cReadTimeOutInMilliseconds = 10 * 1000;

  private final String mPortNameHint;
  private final int mBaudRate;
  private Character mEndOfMessageCharacter = null;
  private int mFlowControl = cFLOWCONTROL_NONE;
  private boolean mEcho = false;
  private volatile boolean mBinaryMode = false;
  private volatile int mMessageLength = 1;
  private volatile boolean mNotifyEvents = true;

  private final CopyOnWriteArrayList<SerialListener> mListenerList =
                                                                   new CopyOnWriteArrayList<SerialListener>();

  private SerialPort mSerialPort;

  private volatile boolean mIsMessageReceived;
  private final TByteArrayList mBuffer = new TByteArrayList(1024);
  private volatile String mTextMessageReceived;

  public Serial(final String pPortNameHint, final int pBaudRate)
  {
    mPortNameHint = pPortNameHint;
    mBaudRate = pBaudRate;
  }

  public Serial(final int pBaudRate)
  {
    mPortNameHint = null;
    mBaudRate = pBaudRate;
  }

  public static ArrayList<String> getListOfAllTTYSerialCommPorts()
  {
    final ArrayList<String> lListOfFreeCommPorts =
                                                 new ArrayList<String>();
    final String[] lPortNameList =
                                 SerialPortList.getPortNames(Pattern.compile("tty\\..+"),
                                                             new Comparator<String>()
                                                             {
                                                               @Override
                                                               public int compare(final String pO1,
                                                                                  final String pO2)
                                                               {
                                                                 return -1;
                                                               }
                                                             });

    for (final String lPortName : lPortNameList)
    {
      lListOfFreeCommPorts.add(lPortName);
    }
    return lListOfFreeCommPorts;
  }

  public static ArrayList<String> getListOfAllSerialCommPorts()
  {
    final ArrayList<String> lListOfFreeCommPorts =
                                                 new ArrayList<String>();
    final String[] lPortNameList = SerialPortList.getPortNames();
    for (final String lPortName : lPortNameList)
    {
      lListOfFreeCommPorts.add(lPortName);
    }
    return lListOfFreeCommPorts;
  }

  public static ArrayList<String> getListOfAllSerialCommPortsWithNameContaining(final String pNameHint)
  {
    final ArrayList<String> lListOfFreeCommPorts =
                                                 getListOfAllTTYSerialCommPorts();

    final ArrayList<String> lListOfSelectedCommPorts =
                                                     new ArrayList<String>();
    for (final String lPortName : lListOfFreeCommPorts)
    {
      if (lPortName.contains(pNameHint))
        lListOfSelectedCommPorts.add(lPortName);
    }
    return lListOfSelectedCommPorts;
  }

  public static String getOneSerialCommPortWithNameContaining(final String pNameHint)
  {
    final ArrayList<String> lListOfAllSerialCommPortsWithNameContaining =
                                                                        getListOfAllSerialCommPortsWithNameContaining(pNameHint);
    if (lListOfAllSerialCommPortsWithNameContaining.size() > 0)
      return lListOfAllSerialCommPortsWithNameContaining.get(0);
    else
      return null;
  }

  @Override
  public final boolean connect() throws SerialPortException,
                                 SerialException
  {
    if (mPortNameHint == null)
      throw new SerialException("No hint given for port name.");
    final String lPortName =
                           getOneSerialCommPortWithNameContaining(mPortNameHint);

    return connect(lPortName);
  }

  @Override
  public final boolean connect(final String pPortName) throws SerialPortException
  {
    if (pPortName != null)
    {
      info("Connecting to '%s'\n", pPortName);
      mSerialPort = new SerialPort(pPortName);

      info("Opening port '%s' with baudrate: %d \n",
           pPortName,
           mBaudRate);
      mSerialPort.openPort();
      mSerialPort.setParams(mBaudRate,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);

      mSerialPort.setFlowControlMode(mFlowControl);
      mSerialPort.purgePort(SerialPort.PURGE_RXCLEAR
                            | SerialPort.PURGE_TXCLEAR);

      // System.out.println("Flow Control: " +
      // mSerialPort.getFlowControlMode());

      if (mNotifyEvents)
        mSerialPort.addEventListener(new Serial.SerialReaderEventBased(mSerialPort));
      return true;
    }

    return false;
  }

  @Override
  public final void addListener(final SerialListener pSerialListener)
  {
    mListenerList.add(pSerialListener);
  }

  @Override
  public final void write(final String pString) throws SerialPortException
  {
    write(pString.getBytes());
  }

  @Override
  public final void write(final byte[] pBytes) throws SerialPortException
  {
    // System.out.println(new String(pBytes));
    mSerialPort.writeBytes(pBytes);
  }

  @Override
  public final void write(final byte pByte) throws SerialPortException
  {
    mSerialPort.writeByte(pByte);
  }

  public void format(String format,
                     Object... args) throws SerialPortException
  {
    write(String.format(format, args));
  }

  public String writeStringAndGetAnswer(String pString) throws SerialPortException
  {
    write(pString);
    return readTextMessageAsString();
  }

  @Override
  public final void purge() throws SerialPortException
  {
    while (mSerialPort.getInputBufferBytesCount() > 0)
    {
      mSerialPort.readBytes();
    }
    mSerialPort.purgePort(SerialPort.PURGE_TXCLEAR
                          | SerialPort.PURGE_RXCLEAR);
  }

  private final void textMessageReceived(final String pMessage)
  {
    mTextMessageReceived = pMessage;
    mIsMessageReceived = true;

    for (final SerialListener lSerialListener : mListenerList)
    {
      lSerialListener.textMessageReceived(this, pMessage);
    }
  }

  private final void binaryMessageReceived(final byte[] pMessage)
  {
    mIsMessageReceived = true;

    for (final SerialListener lSerialListener : mListenerList)
    {
      lSerialListener.binaryMessageReceived(this, pMessage);
    }
  }

  private final void errorOccured(final Throwable pException)
  {
    for (final SerialListener lSerialListener : mListenerList)
    {
      lSerialListener.errorOccured(this, pException);
    }
  }

  public final void setEcho(final boolean echo)
  {
    this.mEcho = echo;
  }

  public final boolean isEcho()
  {
    return mEcho;
  }

  @Override
  public final void setFlowControl(final int flowControl)
  {
    mFlowControl = flowControl;
  }

  @Override
  public final int getFlowControl()
  {
    return mFlowControl;
  }

  @Override
  public void setLineTerminationCharacter(final char pChar)
  {
    mEndOfMessageCharacter = pChar;
  }

  @Override
  public Character getMessageTerminationCharacter()
  {
    return mEndOfMessageCharacter;
  }

  @Override
  public void setBinaryMode(final boolean pBinaryMode)
  {
    mBinaryMode = pBinaryMode;
  }

  @Override
  public boolean isBinaryMode()
  {
    return mBinaryMode;
  }

  @Override
  public void setMessageLength(final int pMessageLength)
  {
    mMessageLength = pMessageLength;
  }

  @Override
  public int getMessageLength()
  {
    return mMessageLength;
  }

  @Override
  public boolean isNotifyEvents()
  {
    return mNotifyEvents;
  }

  @Override
  public void setNotifyEvents(final boolean notifyEvents)
  {
    mNotifyEvents = notifyEvents;
  }

  public boolean isConnected()
  {
    if (mSerialPort == null)
      return false;
    return mSerialPort.isOpened();
  }

  public final class SerialReaderEventBased implements
                                            SerialPortEventListener
  {
    public SerialReaderEventBased(final SerialPort pSerialPort)
    {
    }

    @Override
    public void serialEvent(final SerialPortEvent event)
    {
      if (event.getEventType() == SerialPortEvent.RXCHAR)
      {
        if (mBinaryMode)
        {
          final byte[] lMessage = readBinaryMessage();
          final String lMessageString = new String(lMessage);
          if (lMessage != null)
          {
            binaryMessageReceived(lMessage);
          }
        }
        else
        {
          final String lMessage = readTextMessageAsString();
          if (lMessage != null)
          {
            textMessageReceived(lMessage);
          }
        }
      }
      else if (event.getEventType() == SerialPortEvent.ERR)
      {
        warning("Serial connection error!");
      }
      else if (event.getEventType() == SerialPortEvent.BREAK)
      {
        warning(": Serial connection broken!");
      }
    }
  }

  @Override
  public final void close() throws SerialPortException
  {
    if (mSerialPort != null)
    {
      try
      {
        mSerialPort.removeEventListener();
      }
      catch (final Throwable e)
      {
      }
      if (mSerialPort.isOpened())
        mSerialPort.closePort();
      mSerialPort = null;
    }
  }

  public String waitForAnswer(final int pWaitTime)
  {
    while (!mIsMessageReceived)
    {
      try
      {
        Thread.sleep(pWaitTime);
      }
      catch (final InterruptedException e)
      {
      }
    }
    mIsMessageReceived = false;
    return mTextMessageReceived;
  }

  public byte[] readBinaryMessage()
  {
    return readBinaryMessage(cReadTimeOutInMilliseconds);
  }

  public byte[] readBinaryMessage(final int pTimeOutInMilliseconds)
  {
    try
    {
      if (mEndOfMessageCharacter != null)
      {
        byte[] lByte;
        do
        {
          lByte = mSerialPort.readBytes(1, pTimeOutInMilliseconds);
        }
        while (lByte[0] != mEndOfMessageCharacter);
      }
      final byte[] lReadBytes =
                              mSerialPort.readBytes(mMessageLength,
                                                    pTimeOutInMilliseconds);
      return lReadBytes;
    }
    catch (final Throwable e)
    {
      errorOccured(e);
      return null;
    }
  }

  public byte[] readTextMessage()
  {

    int data;
    try
    {
      mBuffer.clear();
      while ((data =
                   mSerialPort.readBytes(1)[0]) != mEndOfMessageCharacter.charValue())
      {
        mBuffer.add((byte) data);
      }

      if (mEcho)
      {
        final String lMessage = new String(mBuffer.toArray(),
                                           0,
                                           mBuffer.size());
        // System.out.print(lMessage);
      }

      return mBuffer.toArray();

    }
    catch (final Throwable e)
    {
      errorOccured(e);
      return null;
    }

  }

  public String readTextMessageAsString()
  {
    final byte[] lReadTextMessage = readTextMessage();
    final String lMessage = new String(lReadTextMessage,
                                       0,
                                       lReadTextMessage.length);
    return lMessage;
  }

}
