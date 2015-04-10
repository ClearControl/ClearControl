package rtlib.scripting.lang.groovy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;

import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.engine.ScriptingEngineListener;
import rtlib.scripting.lang.groovy.GroovyScripting;
import rtlib.scripting.lang.groovy.GroovyUtils;

public class TestGroovyScripting
{

	long cNumberIterations = 100000;

	@Test
	public void testGroovyUtils() throws IOException
	{
		final Double x = new Double(1);
		final Double y = new Double(2);

		final LinkedHashMap<String, Object> lMap = new LinkedHashMap<String, Object>();
		lMap.put("x", x);
		lMap.put("y", y);

		GroovyUtils.runScript("Test", "x=y; println x", lMap, null, false);

		assertEquals(lMap.get("x"), lMap.get("y"));

	}

	@Test
	public void testGroovyScriptingWithScriptEngine()	throws IOException,
																										ExecutionException
	{
		final Double x = new Double(1);
		final Double y = new Double(2);

		final GroovyScripting lGroovyScripting = new GroovyScripting();

		final ScriptingEngine lScriptingEngine = new ScriptingEngine(lGroovyScripting,
																												null);

		lScriptingEngine.set("x", x);
		lScriptingEngine.set("y", y);
		lScriptingEngine.setScript("x=y");

		lScriptingEngine.addListener(new ScriptingEngineListener()
		{

			@Override
			public void updatedScript(ScriptingEngine pScriptingEngine,
																String pScript)
			{
			}

			@Override
			public void beforeScriptExecution(ScriptingEngine pScriptingEngine,
																				String pScriptString)
			{
				System.out.println("before");
			}

			@Override
			public void afterScriptExecution(	ScriptingEngine pScriptingEngine,
																				String pScriptString)
			{
				System.out.println("after");
			}

			@Override
			public void asynchronousResult(	ScriptingEngine pScriptingEngine,
																			String pScriptString,
																			Map<String, Object> pBinding,
																			Throwable pThrowable,
																			String pErrorMessage)
			{
				System.out.println(pErrorMessage);
			}
		});

		lScriptingEngine.executeScriptAsynchronously();

		assertTrue(lScriptingEngine.waitForCompletion(1, TimeUnit.SECONDS));

		assertEquals(lScriptingEngine.get("x"), lScriptingEngine.get("y"));

	}

	@Test
	public void testPerformance() throws IOException
	{
		for (int i = 0; i < 100; i++)
			runTest();
	}

	private void runTest() throws IOException
	{
		final StopWatch lStopWatch = new StopWatch();
		lStopWatch.start();
		GroovyUtils.runScript("TestIndy",
													"double[] array = new double[1000]; for(int i=0; i<" + cNumberIterations
															+ "; i++) array[i%1000]+=1+array[(i+1)%1000] ",
													(Map<String, Object>) null,
													null,
													false);
		lStopWatch.stop();
		System.out.println("script:" + lStopWatch.getTime());

		lStopWatch.reset();
		lStopWatch.start();
		final double[] array = new double[1000];
		testMethod(array);
		lStopWatch.stop();
		System.out.println("native:" + lStopWatch.getTime());
	}

	private void testMethod(final double[] array)
	{
		for (int i = 0; i < cNumberIterations; i++)
			array[i % 1000] += 1 + array[(i + 1) % 1000];
	}
}
