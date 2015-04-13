package rtlib.scripting.lang.jython;

import java.io.OutputStream;
import java.util.Map;

import org.python.core.Options;
import org.python.util.PythonInterpreter;

public class JythonUtils
{

	public static void runScript(	final String pScriptName,
																final String pScriptString,
																Map<String, Object> pMap,
																OutputStream pOutputStream,
																boolean pB)
	{
		Options.importSite = false;
		final PythonInterpreter lPythonInterpreter = new PythonInterpreter();

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

		lPythonInterpreter.exec(pScriptString);

		for (final Map.Entry<String, Object> lEntry : pMap.entrySet())
		{
			final String lKey = lEntry.getKey();
			final Object lValue = lPythonInterpreter.get(lKey);
			pMap.put(lKey, lValue);
		}

		lPythonInterpreter.close();

	}

}
