package rtlib.scripting.lang.jython;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import rtlib.scripting.lang.ScriptingLanguageInterface;

public class JythonScripting implements ScriptingLanguageInterface
{

	@Override
	public void runScript(String pScriptName,
												String pScriptString,
												Map<String, Object> pMap,
												OutputStream pOutputStream,
												boolean pDebugMode) throws IOException
	{
		JythonUtils.runScript(pScriptName,
													pScriptString,
													pMap,
													pOutputStream,
													pDebugMode);
	}

	@Override
	public String getErrorMessage(Throwable pThrowable)
	{
		if (pThrowable == null)
			return null;
		/*if (pThrowable instanceof PySyntaxError)
		{
			final PySyntaxError lPySyntaxError = (PySyntaxError) pThrowable;
			return 
		}/**/
		return pThrowable.toString();
	}

}
