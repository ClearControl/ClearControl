package clearcontrol.scripting.lang.jython;

import java.io.OutputStream;
import java.util.Map;

import org.python.core.Options;
import org.python.util.PythonInterpreter;

public class JythonUtils
{

  public static Object runScript(final String pScriptName,
                                 final String pScriptString,
                                 Map<String, Object> pMap,
                                 OutputStream pOutputStream,
                                 boolean pDebug)
  {
    return runScript(pScriptName,
                     "",
                     pScriptString,
                     "",
                     pMap,
                     pOutputStream,
                     pDebug);
  }

  public static Object runScript(final String pScriptName,
                                 final String pPreambleString,
                                 final String pScriptString,
                                 final String pPostambleString,
                                 Map<String, Object> pMap,
                                 OutputStream pOutputStream,
                                 boolean pDebug)
  {
    Options.importSite = false;
    final PythonInterpreter lPythonInterpreter =
                                               new PythonInterpreter();

    for (final Map.Entry<String, Object> lEntry : pMap.entrySet())
    {
      final String lKey = lEntry.getKey();
      final Object lValue = lEntry.getValue();
      lPythonInterpreter.set(lKey, lValue);
    }

    if (pOutputStream != null)
    {
      lPythonInterpreter.setOut(pOutputStream);
      lPythonInterpreter.setErr(pOutputStream);
    }

    lPythonInterpreter.exec(pPreambleString + "\n" + pScriptString);

    for (final Map.Entry<String, Object> lEntry : pMap.entrySet())
    {
      final String lKey = lEntry.getKey();
      final Object lValue = lPythonInterpreter.get(lKey);
      pMap.put(lKey, lValue);
    }

    lPythonInterpreter.close();

    return null;

  }

}
