package clearcontrol.scripting.lang.groovy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.engine.ScriptingEngineListener;
import clearcontrol.scripting.lang.groovy.GroovyScripting;
import clearcontrol.scripting.lang.groovy.GroovyUtils;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;

public class TestGroovyScripting
{

  long cNumberIterations = 100000;

  @Test
  public void testGroovyUtils() throws IOException
  {
    final Double x = new Double(1);
    final Double y = new Double(2);

    final LinkedHashMap<String, Object> lMap =
                                             new LinkedHashMap<String, Object>();
    lMap.put("x", x);
    lMap.put("y", y);

    GroovyUtils.runScript("Test",
                          "x=y; println x",
                          lMap,
                          null,
                          false);

    assertEquals(lMap.get("x"), lMap.get("y"));

  }

  @Test
  public void testAutoImports() throws IOException
  {

    try
    {
      GroovyUtils.runScript("TestAutoImports",
                            "String lString = new String(\"test\");  ",
                            (Map<String, Object>) null,
                            null,
                            false);
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGroovyScriptingWithScriptEngine() throws IOException,
                                                    ExecutionException
  {
    final HashSet<Double> s = new HashSet<>();
    s.add((double) 1);

    final GroovyScripting lGroovyScripting = new GroovyScripting();

    final ScriptingEngine lScriptingEngine =
                                           new ScriptingEngine(lGroovyScripting,
                                                               null);

    lScriptingEngine.set("s", s);
    lScriptingEngine.setScript("s.add(2.0); println \"script:\"+s");

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
      public void afterScriptExecution(ScriptingEngine pScriptingEngine,
                                       String pScriptString)
      {
        System.out.println("after");
      }

      @Override
      public void asynchronousResult(ScriptingEngine pScriptingEngine,
                                     String pScriptString,
                                     Map<String, Object> pBinding,
                                     Throwable pThrowable,
                                     String pErrorMessage)
      {
        System.out.println(pErrorMessage);
        if (pThrowable != null)
          pThrowable.printStackTrace();
      }

      @Override
      public void scriptAlreadyExecuting(ScriptingEngine pScriptingEngine)
      {

      }
    });

    lScriptingEngine.executeScriptAsynchronously();

    assertTrue(lScriptingEngine.waitForCompletion(1000,
                                                  TimeUnit.SECONDS));

    // System.out.println("code:" + s);
    assertTrue(s.contains(1.0));
    assertTrue(s.size() == 2);

  }

  @Test
  public void testPerformance() throws IOException
  {
    for (int i = 0; i < 10; i++)
      runTest();
  }

  private void runTest() throws IOException
  {
    final StopWatch lStopWatch = new StopWatch();
    lStopWatch.start();
    GroovyUtils.runScript("TestIndy",
                          "double[] array = new double[1000]; for(int i=0; i<"
                                      + cNumberIterations
                                      + "; i++) array[i%1000]+=1+array[(i+1)%1000] ",
                          (Map<String, Object>) null,
                          null,
                          false);
    lStopWatch.stop();
    // System.out.println("script:" + lStopWatch.getTime());

    lStopWatch.reset();
    lStopWatch.start();
    final double[] array = new double[1000];
    testMethod(array);
    lStopWatch.stop();
    // System.out.println("native:" + lStopWatch.getTime());
  }

  private void testMethod(final double[] array)
  {
    for (int i = 0; i < cNumberIterations; i++)
      array[i % 1000] += 1 + array[(i + 1) % 1000];
  }
}
