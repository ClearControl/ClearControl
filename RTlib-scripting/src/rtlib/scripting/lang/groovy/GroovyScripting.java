package rtlib.scripting.lang.groovy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import rtlib.scripting.lang.ScriptingLanguageInterface;

public class GroovyScripting implements ScriptingLanguageInterface
{

	@Override
	public String getPostamble()
	{
		return "";
	}

	@Override
	public String getPreamble()
	{
		return "def isCanceled = { return scriptengine.isCancelRequested() }\n";
	}

	@Override
	public void runScript(String pPreambleString,
												String pScriptName,
												String pScriptString,
												Map<String, Object> pMap,
												OutputStream pOutputStream,
												boolean pDebugMode) throws IOException
	{
		GroovyUtils.runScript(pPreambleString,
													pScriptName,
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
		return pThrowable.getClass().getSimpleName() + "->"
						+ pThrowable.getMessage();
	}



}
