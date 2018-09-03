package clearcontrol.core.variable.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.variable.Variable;

/**
 * Variable as File
 * 
 * Stores and retrieves the variable value in a file using Java's standard
 * serialization
 *
 * @param <O>
 *          variable type
 * @author royer
 */
public class VariableAsFile<O> extends Variable<O>
                           implements AutoCloseable

{
  private volatile long mCachedReferenceFileSignature =
                                                      Long.MIN_VALUE;

  private final File mFile;

  private final Object mLock = new Object();

  /**
   * Instantiates a variables-as-file with a given name. The file is retrieved
   * from the machine's configuration.
   * 
   * @param pVariableName
   *          variable name
   */
  public VariableAsFile(final String pVariableName)
  {

    this(pVariableName,
         MachineConfiguration.get()
                             .getPersistentVariableFile(pVariableName),
         null);
  }

  /**
   * Instantiates a variable-as-file with a given name, file
   * 
   * @param pVariableName
   *          variable name
   * @param pFile
   *          file o use
   * @param pValue
   *          initial value
   */
  public VariableAsFile(final String pVariableName,
                        final File pFile,
                        final O pValue)
  {
    super(pVariableName, pValue);
    mFile = pFile;
    mFile.getParentFile().mkdirs();
  }

  @Override
  public O get()
  {
    if (mValue != null && mFile != null
        && mFile.exists()
        && mFile.lastModified() < mCachedReferenceFileSignature)
    {
      return mValue;
    }

    try
    {
      mValue = readFromFile();
      return super.get();
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return super.get();
    }
  }

  @Override
  public void set(final O pNewReference)
  {
    saveToFile(pNewReference);
    super.set(pNewReference);
  }

  @SuppressWarnings("unchecked")
  private O readFromFile() throws FileNotFoundException,
                           IOException,
                           ClassNotFoundException
  {
    O lReference = null;
    synchronized (mLock)
    {
      ObjectInputStream lObjectInputStream = null;
      try
      {
        if (!(mFile.exists() && mFile.isFile()))
        {
          return super.get();
        }

        final FileInputStream lFileInputStream =
                                               new FileInputStream(mFile);
        final BufferedInputStream lBufferedInputStream =
                                                       new BufferedInputStream(lFileInputStream);
        lObjectInputStream =
                           new ObjectInputStream(lBufferedInputStream);

        lReference = (O) lObjectInputStream.readObject();
        mCachedReferenceFileSignature = mFile.lastModified();

        return lReference;
      }
      catch (final Throwable e)
      {
        e.printStackTrace();
      }
      finally
      {
        try
        {
          if (lObjectInputStream != null)
            lObjectInputStream.close();
        }
        catch (final IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    return lReference;
  }

  private void saveToFile(O pReference)
  {
    synchronized (mLock)
    {
      ObjectOutputStream lObjectOutputStream = null;
      try
      {
        mFile.getParentFile().mkdirs();
        final FileOutputStream lFileOutputStream =
                                                 new FileOutputStream(mFile);
        final BufferedOutputStream lBufferedOutputStream =
                                                         new BufferedOutputStream(lFileOutputStream);
        lObjectOutputStream =
                            new ObjectOutputStream(lBufferedOutputStream);

        lObjectOutputStream.writeObject(pReference);

      }
      catch (final FileNotFoundException e)
      {
        e.printStackTrace();
      }
      catch (final IOException e)
      {
        e.printStackTrace();
      }
      finally
      {
        try
        {
          if (lObjectOutputStream != null)
            lObjectOutputStream.close();
        }
        catch (final IOException e)
        {
          e.printStackTrace();
        }
      }
    }

  };

  @Override
  public void close() throws IOException
  {
    saveToFile(mValue);
    // nothing to do
  }

}
