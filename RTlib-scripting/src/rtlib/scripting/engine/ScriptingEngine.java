package rtlib.scripting.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Formatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.scripting.lang.ScriptingLanguageInterface;

public class ScriptingEngine implements
														AsynchronousExecutorServiceAccess
{

	private final ScriptingLanguageInterface mScriptingLanguageInterface;

	private final Class<?> mClassForFindingScripts;
	private final String mPathForFindingScripts;

	private static ConcurrentLinkedQueue<ScriptingEngineListener> mScriptListenerList = new ConcurrentLinkedQueue<ScriptingEngineListener>();
	private final boolean mDebugMode = false;
	protected Map<String, Object> mVariableMap = new ConcurrentHashMap<String, Object>();
	private final File mLastExecutedScriptFile;

	private volatile FutureTask<?> mScriptExecutionFuture;

	private String mScriptName = "default";
	private String mPreambleFileName = null;
	private String mPostambleFileName = null;
	private String mRawScriptString;
	private String mScriptString;

	private OutputStream mOutputStream;

	public ScriptingEngine(	ScriptingLanguageInterface pScriptingLanguageInterface,
													Class<?> pClassForFindingScripts,
													String pPathForFindingScripts)
	{
		mScriptingLanguageInterface = pScriptingLanguageInterface;
		mClassForFindingScripts = pClassForFindingScripts;
		mPathForFindingScripts = pPathForFindingScripts;
		mLastExecutedScriptFile = new File(	System.getProperty("user.home"),
																				".script.last.groovy");
	}

	public ScriptingLanguageInterface getScriptingLanguageInterface()
	{
		return mScriptingLanguageInterface;
	}

	public ScriptingEngine(	ScriptingLanguageInterface pScriptingLanguageInterface,
													Class<?> pClassForFindingScripts)
	{
		this(pScriptingLanguageInterface, pClassForFindingScripts, "");

	}

	public final void executeScriptAsynchronously()
	{
		if (mScriptExecutionFuture != null && !mScriptExecutionFuture.isDone())
		{
			System.err.println("Script is already running...");
		}
		else
		{
			executeAsynchronously(() -> execute());
		}
	}

	public final void stopAsynchronousExecution()
	{
		if (mScriptExecutionFuture != null && !mScriptExecutionFuture.isDone())
		{
			try
			{
				mScriptExecutionFuture.cancel(true);

				final String lPostamble = getPostamble();
				final String lPreprocessedPostamble = ScriptingPreprocessor.process(mClassForFindingScripts,
																																						mPathForFindingScripts,
																																						lPostamble);
				mScriptingLanguageInterface.runScript("Postamble",
																							lPreprocessedPostamble,
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

	public final void setScriptName(String pScriptName)
	{
		mScriptName = pScriptName;
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

	public final void execute()
	{
		try
		{
			saveScript(mLastExecutedScriptFile);

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
																							mScriptString,
																							mVariableMap,
																							mOutputStream,
																							mDebugMode);
			}
			catch (final Throwable e)
			{
				lThrowable = e;
			}

			final String lErrorMessage = mScriptingLanguageInterface.getErrorMessage(lThrowable);

			for (final ScriptingEngineListener lScriptListener : mScriptListenerList)
			{
				lScriptListener.afterScriptExecution(this, mRawScriptString);
				lScriptListener.asynchronousResult(	this,
																						mScriptString,
																						mVariableMap,
																						lThrowable,
																						lErrorMessage);
			}
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
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
			final FileInputStream lFileInputStream = new FileInputStream(pScriptFile);
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

	public void setPreambleFileName(String pPreambleFileName)
	{
		mPreambleFileName = pPreambleFileName;
	}

	public void setPostambleFileName(String pPostambleFileName)
	{
		mPostambleFileName = pPostambleFileName;
	}

	public String addPreamble()
	{
		ensureScriptStringNotNull();
		mScriptString = getPreamble() + "\n" + mScriptString;
		return mScriptString;
	}

	private String getPreamble()
	{
		final InputStream lPreambleAsStream = mClassForFindingScripts.getResourceAsStream(mPathForFindingScripts + mPreambleFileName);
		String lPreambleString;
		try
		{
			lPreambleString = IOUtils.toString(lPreambleAsStream);
		}
		catch (final IOException e)
		{
			lPreambleString = "";
			e.printStackTrace();
		}
		return lPreambleString;
	}

	public String addPostamble()
	{
		ensureScriptStringNotNull();
		mScriptString = mScriptString + "\n" + getPostamble();
		return mScriptString;
	}

	private String getPostamble()
	{
		final InputStream lPostambleAsStream = mClassForFindingScripts.getResourceAsStream(mPathForFindingScripts + mPostambleFileName);
		String lPostambleString;
		try
		{
			lPostambleString = IOUtils.toString(lPostambleAsStream);
		}
		catch (final IOException e)
		{
			lPostambleString = "";
			e.printStackTrace();
		}
		return lPostambleString;
	}

	public String preProcess()
	{
		ensureScriptStringNotNull();
		mScriptString = ScriptingPreprocessor.process(mClassForFindingScripts,
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
