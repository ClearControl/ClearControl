package clearcontrol.stack.sourcesink;

import clearcontrol.core.variable.bundle.VariableBundle;
import clearcontrol.core.variable.persistence.VariableBundleAsFile;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public abstract class LocalFileStackBase extends StackServerBase implements
        AutoCloseable {
    protected final File mFolder;
    private final File mDataFolder;
    protected final File mBinaryFile;
    protected long mNextFreeStackIndex;
    protected long mNextFreeTypePosition;

    protected final File mIndexFile;
    protected final File mMetaDataFile;
    protected final VariableBundleAsFile mMetaDataVariableBundleAsFile;

    public LocalFileStackBase(final File pRootFolder,
                              final String pName,
                              final boolean pReadOnly) throws IOException {
        super();
        mFolder = new File(pRootFolder, pName);
        mDataFolder = new File(mFolder, "/data/");
        mBinaryFile = new File(mDataFolder, "data.bin");

        if (!pReadOnly && mBinaryFile.exists()) {
            throw new IOException(this.getClass().getSimpleName() + ": Cannot overwrite an existing file!");
        }

        if (!pReadOnly) {
            final File lParentFile = mBinaryFile.getParentFile();
            lParentFile.mkdirs();
        }

        mNextFreeTypePosition = 0;

        mIndexFile = new File(mFolder, "/data/index.txt");
        if (!pReadOnly) {
            mIndexFile.getParentFile().mkdirs();
        }

        mMetaDataFile = new File(mFolder, "/metadata.txt");
        if (!pReadOnly) {
            mMetaDataFile.getParentFile().mkdirs();
        }

        mMetaDataVariableBundleAsFile = new VariableBundleAsFile(pName + "MetaData",
                mMetaDataFile,
                false);
    }

    protected FileChannel getFileChannelForBinaryFile(final boolean pReadOnly,
                                                      final boolean pForMapping) throws IOException {
        FileChannel lFileChannel;
        if (pReadOnly) {
            if (pForMapping) {
                lFileChannel = FileChannel.open(mBinaryFile.toPath(),
                        StandardOpenOption.READ);
            } else {
                lFileChannel = FileChannel.open(mBinaryFile.toPath(),
                        StandardOpenOption.READ);
            }
        } else {
            if (pForMapping) {
                lFileChannel = FileChannel.open(mBinaryFile.toPath(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.READ,
                        StandardOpenOption.WRITE);
            } else {
                lFileChannel = FileChannel.open(mBinaryFile.toPath(),
                        StandardOpenOption.APPEND,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.CREATE);
            }
        }
        return lFileChannel;
    }

    @Override
    public VariableBundle getMetaDataVariableBundle() {
        return mMetaDataVariableBundleAsFile;
    }

    public File getDataFolder() {
        return mDataFolder;
    }

    public File getFolder() {
        return mFolder;
    }

    @Override
    public void close() throws IOException {
        mMetaDataVariableBundleAsFile.close();
    }

}
