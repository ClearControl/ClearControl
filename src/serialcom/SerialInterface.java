package serialcom;

import jssc.SerialPortException;

public interface SerialInterface
{

	public boolean connect() throws SerialPortException;

	public void close() throws SerialPortException;

	public void addListener(final SerialListener pSerialListener);

	public void write(final String pString) throws SerialPortException;

	public void write(final byte[] pBytes) throws SerialPortException;

	public void write(byte pByte) throws SerialPortException;

	public void purge() throws SerialPortException;

	public void setSerialNewLineCharacter(char pString);

	public void setFlowControl(final int flowControl);

	public int getFlowControl();

}