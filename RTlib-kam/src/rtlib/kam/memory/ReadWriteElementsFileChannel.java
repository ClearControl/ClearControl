package rtlib.kam.memory;

import java.nio.channels.FileChannel;

public interface ReadWriteElementsFileChannel
{

	public <T> void writeElementsToFileChannel(	FileChannel pFileChannel,
																							long pBytePositionInFile);

	public <T> void writeElementsToFileChannel(	final long pPositionInBufferInElements,
																							FileChannel pFileChannel,
																							long pBytePositionInFile,
																							Class<T> pType,
																							long pLengthInElements);

	public <T> void readElementsFromFileChannel(FileChannel pFileChannel,
																							long pBytePositionInFile,
																							Class<T> pType,
																							long pLengthInElements);

	public <T> void readElementsFromFileChannel(final long pPositionInBufferInElements,
																							FileChannel pFileChannel,
																							long pBytePositionInFile,
																							Class<T> pType,
																							long pLengthInElements);


}
