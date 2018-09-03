package clearcontrol.scripting.lang.jython.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.engine.ScriptingEngineListener;
import clearcontrol.scripting.lang.jython.JythonScripting;
import clearcontrol.scripting.lang.jython.JythonUtils;

import org.junit.Test;
import org.python.core.Options;
import org.python.core.PyInteger;
import org.python.util.PythonInterpreter;

public class TestJythonScripting
{

  long cNumberIterations = 100000;

  @Test
  public void testJythonInterpreter() throws IOException
  {
    Options.importSite = false;
    final PythonInterpreter lPythonInterpreter =
                                               new PythonInterpreter();
    lPythonInterpreter.set("integer", new PyInteger(42));
    lPythonInterpreter.exec("square = integer*integer");
    final PyInteger square =
                           (PyInteger) lPythonInterpreter.get("square");
    System.out.println("square: " + square.asInt());
    assertEquals(1764, square.asInt());
    lPythonInterpreter.close();
  }

  @Test
  public void testJythonUtils() throws IOException
  {
    final Double x = new Double(1);
    final Double y = new Double(2);

    final LinkedHashMap<String, Object> lMap =
                                             new LinkedHashMap<String, Object>();
    lMap.put("x", x);
    lMap.put("y", y);

    JythonUtils.runScript("Test", "x=y", lMap, null, false);

    assertEquals(lMap.get("x"), lMap.get("y"));
  }

  @Test
  public void testJythonScriptingWithScriptEngine() throws IOException,
                                                    ExecutionException
  {
    final Double x = new Double(1);
    final Double y = new Double(2);

    final JythonScripting lJythonScripting = new JythonScripting();

    final ScriptingEngine lScriptingEngine =
                                           new ScriptingEngine(lJythonScripting,
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
        System.out.println(pBinding);
      }

      @Override
      public void scriptAlreadyExecuting(ScriptingEngine pScriptingEngine)
      {

      }
    });

    lScriptingEngine.executeScriptAsynchronously();

    assertTrue(lScriptingEngine.waitForCompletion(1,
                                                  TimeUnit.SECONDS));
    assertEquals(lScriptingEngine.get("x"),
                 lScriptingEngine.get("y"));

  }

}
