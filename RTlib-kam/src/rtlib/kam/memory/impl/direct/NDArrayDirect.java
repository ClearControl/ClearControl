package rtlib.kam.memory.impl.direct;

import java.io.IOException;
import java.nio.channels.FileChannel;

import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.ReadWriteBytesFileChannel;
import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.ndarray.NDArray;
import rtlib.kam.memory.ram.ReadAtAligned;
import rtlib.kam.memory.ram.WriteAtAligned;

public class NDArrayDirect extends NDArray implements
																					SizedInBytes,
																					ReadWriteBytesFileChannel,
																					WriteAtAligned,
																					ReadAtAligned,
																					Freeable
{

	public NDArrayDirect(	NDBoundedCursor pCursor,
												long pElementSizeInBytes)
	{
		super(RAMDirect.allocate(pCursor.getLengthInElements()),
					1,
					pCursor);
	}

	public static NDArrayDirect allocateSXYZ(	long pBytesPerPixel,
																						long pWidth,
																						long pHeight,
																						long pDepth)
	{
		NDBoundedCursor lNDBoundedCursor = NDBoundedCursor.createNDVectorCursor(pBytesPerPixel,
																																						pWidth,
																																						pHeight,
																																						pDepth);
		return new NDArrayDirect(lNDBoundedCursor, pBytesPerPixel);
	}

	private RAMDirect getRAMDirect()
	{
		return (RAMDirect) getRAM();
	}

	@Override
	public long getSizeInBytes()
	{
		return getRAMDirect().getSizeInBytes();
	}

	@Override
	public long writeBytesToFileChannel(FileChannel pFileChannel,
																			long pFilePositionInBytes) throws IOException
	{
		return getRAMDirect().writeBytesToFileChannel(pFileChannel,
																									pFilePositionInBytes);
	}

	@Override
	public long writeBytesToFileChannel(long pBufferPositionInBytes,
																			FileChannel pFileChannel,
																			long pFilePositionInBytes,
																			long pLengthInBytes) throws IOException
	{
		return getRAMDirect().writeBytesToFileChannel(pBufferPositionInBytes,
																									pFileChannel,
																									pFilePositionInBytes,
																									pLengthInBytes);
	}

	@Override
	public void readBytesFromFileChannel(	FileChannel pFileChannel,
																				long pFilePositionInBytes,
																				long pLengthInBytes) throws IOException
	{
		getRAMDirect().readBytesFromFileChannel(pFileChannel,
																						pFilePositionInBytes,
																						pLengthInBytes);
	}

	@Override
	public void readBytesFromFileChannel(	long pBufferPositionInBytes,
																				FileChannel pFileChannel,
																				long pFilePositionInBytes,
																				long pLengthInBytes) throws IOException
	{
		getRAMDirect().readBytesFromFileChannel(pBufferPositionInBytes,
																						pFileChannel,
																						pFilePositionInBytes,
																						pLengthInBytes);
	}

	@Override
	public MemoryType getMemoryType()
	{
		return getRAMDirect().getMemoryType();
	}

	@Override
	public void setByteAligned(long pOffset, byte pValue)
	{
		getRAMDirect().setByteAligned(pOffset, pValue);
	}

	@Override
	public void setCharAligned(long pOffset, char pValue)
	{
		getRAMDirect().setCharAligned(pOffset, pValue);
	}

	@Override
	public void setShortAligned(long pOffset, short pValue)
	{
		getRAMDirect().setShortAligned(pOffset, pValue);
	}

	@Override
	public void setIntAligned(long pOffset, int pValue)
	{
		getRAMDirect().setIntAligned(pOffset, pValue);
	}

	@Override
	public void setLongAligned(long pOffset, long pValue)
	{
		getRAMDirect().setLongAligned(pOffset, pValue);
	}

	@Override
	public void setFloatAligned(long pOffset, float pValue)
	{
		getRAMDirect().setFloatAligned(pOffset, pValue);
	}

	@Override
	public void setDoubleAligned(long pOffset, double pValue)
	{
		getRAMDirect().setDoubleAligned(pOffset, pValue);
	}

	@Override
	public byte getByteAligned(long pOffset)
	{
		return getRAMDirect().getByteAligned(pOffset);
	}

	@Override
	public char getCharAligned(long pOffset)
	{
		return getRAMDirect().getCharAligned(pOffset);
	}

	@Override
	public short getShortAligned(long pOffset)
	{
		return getRAMDirect().getShortAligned(pOffset);
	}

	@Override
	public int getIntAligned(long pOffset)
	{
		return getRAMDirect().getIntAligned(pOffset);
	}

	@Override
	public long getLongAligned(long pOffset)
	{
		return getRAMDirect().getLongAligned(pOffset);
	}

	@Override
	public float getFloatAligned(long pOffset)
	{
		return getRAMDirect().getFloatAligned(pOffset);
	}

	@Override
	public double getDoubleAligned(long pOffset)
	{
		return getRAMDirect().getDoubleAligned(pOffset);
	}

	@Override
	public void free()
	{
		mRAM.free();
	}

	@Override
	public boolean isFree()
	{
		return mRAM.isFree();
	}

}
