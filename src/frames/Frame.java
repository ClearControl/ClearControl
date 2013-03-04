package frames;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import recycling.RecyclableInterface;
import recycling.Recycler;

public class Frame implements RecyclableInterface
{

	private Recycler<Frame> mFrameRecycler;
	private volatile boolean isReleased;

	private BufferHeader mBufferHeader = new BufferHeader();
	public ByteBuffer buffer;
	public int width;
	public int height;
	public int bpp;
	public long index;

	public Frame()
	{
	}

	public Frame(	final long pImageIndex,
								final int pWidth,
								final int pHeight,
								final int pBytesPerPixel)
	{
		index = pImageIndex;
		width = pWidth;
		height = pHeight;
		bpp = pBytesPerPixel;
		buffer = ByteBuffer.allocateDirect(pWidth * pHeight
																				* pBytesPerPixel)
												.order(ByteOrder.nativeOrder());
	}

	public Frame(	ByteBuffer pByteBuffer,
								long pImageIndex,
								int pWidth,
								int pHeight,
								int pBytesPerPixel)
	{
		index = pImageIndex;
		width = pWidth;
		height = pHeight;
		bpp = pBytesPerPixel;
		buffer = pByteBuffer;
	}

	public void copyFrom(	ByteBuffer pByteBufferToBeCopied,
												long pImageIndex,
												int pWidth,
												int pHeight,
												int pBytesPerPixel)
	{
		index = pImageIndex;
		width = pWidth;
		height = pHeight;
		bpp = pBytesPerPixel;

		final int length = pByteBufferToBeCopied.limit();
		if (buffer == null || buffer.capacity() < length)
		{
			/*if (buffer != null)
				System.out.format("length=%d, buffer.capacity()=%d \n",
													length,
													buffer.capacity());/**/
			buffer = ByteBuffer.allocateDirect(length)
													.order(ByteOrder.nativeOrder());
			;
			pByteBufferToBeCopied.rewind();
			buffer.put(pByteBufferToBeCopied);
		}
	}

	@Override
	public void initialize(int... pParameters)
	{
		width = pParameters[0];
		height = pParameters[1];
		bpp = pParameters[2];

		final int length = width * height * bpp;
		if (buffer == null || buffer.capacity() < length)
		{
			buffer = ByteBuffer.allocateDirect(length)
													.order(ByteOrder.nativeOrder());
		}
		buffer.clear();
	}

	public void releaseFrame()
	{
		if (isReleased)
			throw new RuntimeException("Object " + this.hashCode()
																	+ " Already released!");
		isReleased = true;
		mFrameRecycler.release(this);
	}

	public void writeRaw(File pFile) throws IOException
	{
		final FileOutputStream lFileOutputStream = new FileOutputStream(pFile);
		FileChannel lChannel = lFileOutputStream.getChannel();
		lChannel.write(buffer);
		lFileOutputStream.close();
	}

	public boolean writeTo(FileChannel pChannel)
	{
		BufferType lBufferType = null;
		if (bpp == 1)
			lBufferType = BufferType.FrameBuffer8Bit;
		else if (bpp == 2)
			lBufferType = BufferType.FrameBuffer16Bit;

		mBufferHeader.set(lBufferType, buffer.limit());
		try
		{
			mBufferHeader.writeTo(pChannel);
			buffer.rewind();
			pChannel.write(buffer);
			return true;
		}
		catch (IOException e)
		{
			System.err.println(e.getLocalizedMessage());
			return false;
		}
	}

	public boolean writeTo(ByteBuffer pByteBuffer)
	{
		BufferType lBufferType = null;
		if (bpp == 1)
			lBufferType = BufferType.FrameBuffer8Bit;
		else if (bpp == 2)
			lBufferType = BufferType.FrameBuffer16Bit;

		mBufferHeader.set(lBufferType, buffer.limit());
		try
		{
			mBufferHeader.writeTo(pByteBuffer);
			buffer.rewind();
			pByteBuffer.put(buffer);
			return true;
		}
		catch (BufferOverflowException e)
		{
			System.err.println(e.getLocalizedMessage());
			return false;
		}
	}

	@Override
	public String toString()
	{
		return String.format(	"Frame [index=%d, width=%s, height=%s, bpp=%s]",
													index,
													width,
													height,
													bpp);
	}

	public boolean isReleased()
	{
		return isReleased;
	}

	public void setReleased(boolean isReleased)
	{
		this.isReleased = isReleased;
	}

	@Override
	public void setRecycler(Recycler pRecycler)
	{
		mFrameRecycler = pRecycler;
	}

}
