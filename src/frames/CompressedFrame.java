package frames;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CompressedFrame extends Frame 
{

	private BufferHeader mLookUpTableBufferHeader = new BufferHeader();
	private ByteBuffer m8BitTo16BitLookupTableByteBuffer = ByteBuffer.allocateDirect(256 * 4);

	public CompressedFrame()
	{
		super();
	}

	public CompressedFrame(	long pImageIndex,
													int pWidth,
													int pHeight,
													int pBytesPerPixel)
	{
		super(pImageIndex, pWidth, pHeight, pBytesPerPixel);
	}

	public CompressedFrame(	long pImageIndex,
													ByteBuffer pByteBuffer,
													int pWidth,
													int pHeight,
													int pBytesPerPixel)
	{
		super(pByteBuffer, pImageIndex, pWidth, pHeight, pBytesPerPixel);
	}

	public void set8BitTo16BitLookupTable(int[] pGet8BitTo16BitLookupTable)
	{
		m8BitTo16BitLookupTableByteBuffer.rewind();
		m8BitTo16BitLookupTableByteBuffer.asIntBuffer()
																			.put(pGet8BitTo16BitLookupTable);
	}

	public boolean writeTo(FileChannel pChannel)
	{

		mLookUpTableBufferHeader.set(	BufferType.FrameBuffer8BitTo16BitLookupTable,
																	m8BitTo16BitLookupTableByteBuffer.limit());
		try
		{
			mLookUpTableBufferHeader.writeTo(pChannel);
			m8BitTo16BitLookupTableByteBuffer.rewind();
			pChannel.write(m8BitTo16BitLookupTableByteBuffer);
			return super.writeTo(pChannel);
		}
		catch (IOException e)
		{
			System.err.println(e.getLocalizedMessage());
			return false;
		}
	}
	
	@Override
	public boolean writeTo(ByteBuffer pByteBuffer)
	{

		mLookUpTableBufferHeader.set(	BufferType.FrameBuffer8BitTo16BitLookupTable,
																	m8BitTo16BitLookupTableByteBuffer.limit());
		try
		{
			mLookUpTableBufferHeader.writeTo(pByteBuffer);
			m8BitTo16BitLookupTableByteBuffer.rewind();
			pByteBuffer.put(m8BitTo16BitLookupTableByteBuffer);
			return super.writeTo(pByteBuffer);
		}
		catch (BufferOverflowException e)
		{
			System.err.println(e.getLocalizedMessage());
			return false;
		}
	}

}
