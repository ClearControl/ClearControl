package clearcontrol.scripting.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Formatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.scripting.lang.ScriptingLanguageInterface;

import org.apache.commons.io.IOUtils;

public class ScriptingEngine implements AsynchronousExecutorFeature
{
  private static ThreadLocal<Boolean> mCancelThreadLocal =
                                                         new ThreadLocal<>();

  private final ScriptingLanguageInterface mScriptingLanguageInterface;

  private final Class<?> mClassForFindingScripts;
  private final String mPathForFindingScripts;

  private static ConcurrentLinkedQueue<ScriptingEngineListener> mScriptListenerList =
                                                                                    new ConcurrentLinkedQueue<ScriptingEngineListener>();
  private final boolean mDebugMode = false;
  protected Map<String, Object> mVariableMap =
                                             new ConcurrentHashMap<String, Object>();
  private final File mLastExecutedScriptFile;

  private volatile Future<?> mScriptExecutionFuture;

  private volatile String mScriptName = "default";

  private volatile String mPreambleString = "";
  private volatile String mPostambleString = "";
  private volatile String mRawScriptString;
  private volatile String mScriptString;

  private OutputStream mOutputStream;

  public ScriptingEngine(ScriptingLanguageInterface pScriptingLanguageInterface,
                         Class<?> pClassForFindingScripts,
                         String pPathForFindingScripts)
  {
    mScriptingLanguageInterface = pScriptingLanguageInterface;
    mClassForFindingScripts = pClassForFindingScripts;
    mPathForFindingScripts = pPathForFindingScripts;
    mLastExecutedScriptFile =
                            new File(System.getProperty("user.home"),
                                     ".script.last.groovy");

    appendToPreamble(mScriptingLanguageInterface.getPreamble());
    appendToPostamble(mScriptingLanguageInterface.getPostamble());
  }

  public ScriptingEngine(ScriptingLanguageInterface pScriptingLanguageInterface,
                         Class<?> pClassForFindingScripts)
  {
    this(pScriptingLanguageInterface, pClassForFindingScripts, "");

  }

  public final Future<?> executeScriptAsynchronously()
  {
    if (mScriptExecutionFuture != null
        && !mScriptExecutionFuture.isDone())
    {
      for (final ScriptingEngineListener lScriptListener : mScriptListenerList)
      {
        lScriptListener.scriptAlreadyExecuting(this);
      }
      return null;
    }
    else
    {
      return mScriptExecutionFuture = executeAsynchronously(() -> {
        execute();
        mScriptExecutionFuture = null;
      });
    }

  }

  public final void stopAsynchronousExecution()
  {
    if (mScriptExecutionFuture != null
        && !mScriptExecutionFuture.isDone())
    {
      try
      {
        mCancelThreadLocal.set(true);

        mScriptExecutionFuture.cancel(false);

        final String lPreprocessedPostamble =
                                            ScriptingPreprocessor.process(mClassForFindingScripts,
                                                                          mPathForFindingScripts,
                                                                          mPostambleString);
        mScriptingLanguageInterface.runScript("Postamble",
                                              lPreprocessedPostamble,
                                              "",
                                              "",
                                              mVariableMap,
                                              mOutputStream,
                                              mDebugMode);

      }
      catch (final java.lang.ThreadDeath e)
      {
        System.err.println(e.getLocalizedMessage());
      }
      catch (final Throwable e)
      {
        System.err.println(e.getLocalizedMessage());
      }
    }
  }

  public boolean isReady()
  {
    return mScriptExecutionFuture == null
           || mScriptExecutionFuture.isDone()
           || mScriptExecutionFuture.isCancelled();
  }

  public boolean isCancelRequested()
  {
    if (mScriptExecutionFuture == null)
      return true;
    return mScriptExecutionFuture.isCancelled();
  }

  public static boolean isCancelRequestedStatic()
  {
    Boolean lBoolean = mCancelThreadLocal.get();
    if (lBoolean == null)
      return false;
    return lBoolean;
  }

  public ScriptingLanguageInterface getScriptingLanguageInterface()
  {
    return mScriptingLanguageInterface;
  }

  public final void setScriptName(String pScriptName)
  {
    mScriptName = pScriptName;
  }

  public String getScriptName()
  {
    return mScriptName;
  }

  public final void setScript(String pScriptString)
  {
    mRawScriptString = pScriptString;
    mScriptString = null;
    for (final ScriptingEngineListener lScriptListener : mScriptListenerList)
    {
      lScriptListener.updatedScript(this, mRawScriptString);
    }
  }

  public String getScript()
  {
    return mRawScriptString;
  }

  public void appendToPreamble(String pString)
  {
    mPreambleString += pString;
  }

  public void appendToPostamble(String pString)
  {
    mPostambleString += pString;
  }

  public final void execute()
  {
    try
    {
      mCancelThreadLocal.set(false);
      saveScript(mLastExecutedScriptFile);

      // TODO: might have to call preProcess() if we implement import
      // resolution..
      ensureScriptStringNotNull();

      for (final ScriptingEngineListener lScriptListener : mScriptListenerList)
      {
        lScriptListener.beforeScriptExecution(this, mRawScriptString);
      }
      Throwable lThrowable = null;

      try
      {
        mVariableMap.put("scriptengine", this);
        mScriptingLanguageInterface.runScript(mScriptName,
                                              mPreambleString,
                                              mScriptString,
                                              mPostambleString,
                                              mVariableMap,
                                              mOutputStream,
                                              mDebugMode);
      }
      catch (final Throwable e)
      {
        lThrowable = e;
      }

      final String lErrorMessage =
                                 mScriptingLanguageInterface.getErrorMessage(lThrowable);

      for (final ScriptingEngineListener lScriptListener : mScriptListenerList)
      {
        try
        {
          lScriptListener.afterScriptExecution(this,
                                               mRawScriptString);
          lScriptListener.asynchronousResult(this,
                                             mScriptString,
                                             mVariableMap,
                                             lThrowable,
                                             lErrorMessage);
        }
        catch (final Throwable e)
        {
          e.printStackTrace();
        }
      }
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
    }
    finally
    {
      mScriptExecutionFuture = null;
    }

  }

  private void ensureScriptStringNotNull()
  {
    if (mScriptString == null)
    {
      mScriptString = mRawScriptString;
    }
  }

  public final void saveScript(final File pScriptFile) throws FileNotFoundException
  {
    final Formatter lFormatter = new Formatter(pScriptFile);
    lFormatter.format("%s", mRawScriptString);
    lFormatter.close();
  }

  public boolean loadLastExecutedScript()
  {
    try
    {
      if (mLastExecutedScriptFile.exists())
      {
        return loadScript(mLastExecutedScriptFile);
      }
      return false;
    }
    catch (final Throwable e)
    {
      return false;
    }
  }

  public final boolean loadScript(final File pScriptFile)
  {
    try
    {
      final FileInputStream lFileInputStream =
                                             new FileInputStream(pScriptFile);
      final String lScriptString = IOUtils.toString(lFileInputStream);
      setScript(lScriptString);
      return true;
    }
    catch (final IOException e)
    {
      System.err.format("Could not read script: %s",
                        pScriptFile.getAbsolutePath());
      return false;
    }
  }

  public void addListener(final ScriptingEngineListener pScriptListener)
  {
    if (!mScriptListenerList.contains(pScriptListener))
      mScriptListenerList.add(pScriptListener);
  }

  public void set(final String pVariableName, final Object pObject)
  {
    mVariableMap.put(pVariableName, pObject);
  }

  public Object get(final String pVariableName)
  {
    return mVariableMap.get(pVariableName);
  }

  public String preProcess()
  {
    ensureScriptStringNotNull();
    mScriptString =
                  ScriptingPreprocessor.process(mClassForFindingScripts,
                                                mPathForFindingScripts,
                                                mScriptString);
    return mScriptString;
  }

  public boolean waitForScriptExecutionToFinish(long pTimeout,
                                                TimeUnit pTimeUnit) throws ExecutionException
  {
    try
    {
      mScriptExecutionFuture.get(pTimeout, pTimeUnit);
      return true;
    }
    catch (final InterruptedException e)
    {
      waitForScriptExecutionToFinish(pTimeout, pTimeUnit);
    }
    catch (final TimeoutException e)
    {
      return false;
    }
    return false;
  }

  public boolean hasAsynchronousExecutionFinished()
  {
    return mScriptExecutionFuture.isDone();
  }

  public OutputStream getOutputStream()
  {
    return mOutputStream;
  }

  public void setOutputStream(OutputStream pOutputStream)
  {
    mOutputStream = pOutputStream;
  }

}
