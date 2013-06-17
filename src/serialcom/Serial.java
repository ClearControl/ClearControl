package serialcom;

import gnu.trove.list.array.TByteArrayList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Serial implements SerialInterface
{
	public final static int cFLOWCONTROL_NONE = SerialPort.FLOWCONTROL_NONE;
	public final static int cFLOWCONTROL_RTSCTS = SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT;
	public final static int cFLOWCONTROL_XONXOFF = SerialPort.FLOWCONTROL_XONXOFF_IN | SerialPort.FLOWCONTROL_XONXOFF_OUT;

	private final String mPortNameHint;
	private final int mBaudRate;
	private char mEndOfMessageCharacter = '\n';
	private int mFlowControl = cFLOWCONTROL_NONE;
	private boolean mEcho = false;
	private final int mConnectionTimeOutInMs = 2000;
	private boolean mBinaryMode = false;
	private int mMessageLength = 1;

	private final CopyOnWriteArrayList<SerialListener> mListenerList = new CopyOnWriteArrayList<SerialListener>();

	private SerialPort mSerialPort;

	private volatile boolean mIsMessageReceived;
	private final TByteArrayList mBuffer = new TByteArrayList(1024);
	private String mTextMessageReceived;
	private byte[] mBinaryMessageReceived;

	public Serial(final String pPortNameHint, final int pBaudRate)
	{
		mPortNameHint = pPortNameHint;
		mBaudRate = pBaudRate;
	}

	public static ArrayList<String> getListOfAllSerialCommPorts()
	{
		final ArrayList<String> lListOfFreeCommPorts = new ArrayList<String>();
		final String[] lPortNameList = SerialPortList.getPortNames(	Pattern.compile("tty\\..+"),
																																new Comparator<String>()
																																{
																																	@Override
																																	public int compare(	final String pO1,
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

	public static ArrayList<String> getListOfAllSerialCommPortsWithNameContaining(final String pNameHint)
	{
		final ArrayList<String> lListOfFreeCommPorts = getListOfAllSerialCommPorts();

		final ArrayList<String> lListOfSelectedCommPorts = new ArrayList<String>();
		for (final String lPortName : lListOfFreeCommPorts)
		{
			if (lPortName.contains(pNameHint))
				lListOfSelectedCommPorts.add(lPortName);
		}
		return lListOfSelectedCommPorts;
	}

	public static String getOneSerialCommPortWithNameContaining(final String pNameHint)
	{
		final ArrayList<String> lListOfAllSerialCommPortsWithNameContaining = getListOfAllSerialCommPortsWithNameContaining(pNameHint);
		if (lListOfAllSerialCommPortsWithNameContaining.size() > 0)
			return lListOfAllSerialCommPortsWithNameContaining.get(0);
		else
			return null;
	}

	@Override
	public final boolean connect() throws SerialPortException
	{
		final String lPortName = getOneSerialCommPortWithNameContaining(mPortNameHint);
		System.out.format("Connecting to '%s'\n", lPortName);
		return connect(lPortName);
	}

	@Override
	public final boolean connect(final String pPortName) throws SerialPortException
	{
		if (pPortName != null)
		{
			mSerialPort = new SerialPort(pPortName);

			mSerialPort.openPort();
			mSerialPort.setParams(mBaudRate,
														SerialPort.DATABITS_8,
														SerialPort.STOPBITS_1,
														SerialPort.PARITY_NONE);

			mSerialPort.setFlowControlMode(mFlowControl);

			System.out.println("Flow Control: " + mSerialPort.getFlowControlMode());

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
		mSerialPort.writeBytes(pString.getBytes());
	}

	@Override
	public final void write(final byte[] pBytes) throws SerialPortException
	{
		mSerialPort.writeBytes(pBytes);
	}

	@Override
	public final void write(final byte pByte) throws SerialPortException
	{
		mSerialPort.writeByte(pByte);
	}

	@Override
	public final void purge() throws SerialPortException
	{
		mSerialPort.purgePort(SerialPort.PURGE_TXCLEAR + SerialPort.PURGE_RXCLEAR);
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
		mBinaryMessageReceived = pMessage;
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

	public final class SerialReaderEventBased	implements
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
					if (lMessage != null)
					{
						binaryMessageReceived(lMessage);
					}
				}
				else
				{
					final String lMessage = readTextMessage();
					if (lMessage != null)
					{
						textMessageReceived(lMessage);
					}
				}
			}
		}
	}

	@Override
	public final void close() throws SerialPortException
	{
		if (mSerialPort != null)
		{
			mSerialPort.removeEventListener();
			if (mSerialPort.isOpened())
				mSerialPort.closePort();
			mSerialPort = null;
		}
	}

	public String waitForTextAnswear(final int pWaitTime)
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
		try
		{
			if (mEndOfMessageCharacter != 0)
				while (mSerialPort.readBytes(1)[0] != mEndOfMessageCharacter)
				{
				}

			final byte[] lReadBytes = mSerialPort.readBytes(mMessageLength);
			return lReadBytes;
		}
		catch (final Throwable e)
		{
			errorOccured(e);
			return null;
		}

	}

	public String readTextMessage()
	{

		int data;
		try
		{
			int len = 0;
			while ((data = mSerialPort.readBytes(1)[0]) != mEndOfMessageCharacter)
			{
				if (data > -1)
				{
					final int i = len++;
					mBuffer.ensureCapacity(len);
					mBuffer.set(i, (byte) data);
				}
				else
				{
					Thread.yield();
				}
			}
			final String lMessage = new String(mBuffer.toArray(), 0, len);
			if (mEcho)
			{
				System.out.print(lMessage);
			}

			return lMessage;

		}
		catch (final Throwable e)
		{
			errorOccured(e);
			return null;
		}

	}

	@Override
	public void setSerialNewLineCharacter(final char pChar)
	{
		mEndOfMessageCharacter = pChar;
	}

	public void setBinaryMode(final boolean pBinaryMode)
	{
		mBinaryMode = pBinaryMode;
	}

	public boolean isBinaryMode()
	{
		return mBinaryMode;
	}

	public void setMessageLength(final int pMessageLength)
	{
		mMessageLength = pMessageLength;
	}

	public int getMessageLength()
	{
		return mMessageLength;
	}

}
