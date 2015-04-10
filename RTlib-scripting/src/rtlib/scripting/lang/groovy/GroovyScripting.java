package rtlib.scripting.lang.groovy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import rtlib.scripting.lang.ScriptingLanguageInterface;

public class GroovyScripting implements ScriptingLanguageInterface
{

	@Override
	public void runScript(String pScriptName,
												String pPreprocessedPostamble,
												Map<String, Object> pMap,
												OutputStream pOutputStream,
												boolean pDebugMode) throws IOException
	{
		GroovyUtils.runScript(pScriptName,
													pPreprocessedPostamble,
													pMap,
													pOutputStream,
													pDebugMode);
	}

	@Override
	public String getErrorMessage(Throwable pThrowable)
	{
		if (pThrowable == null)
			return null;
		return pThrowable.getMessage();
	}

}
