package rtlib.scripting.lang;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface ScriptingLanguageInterface
{
	String getPostamble();

	String getPreamble();

	void runScript(	String pScriptName,
					String pPreambleString,
					String pScriptString,
					String pPostambleString,
					Map<String, Object> pMap,
					OutputStream pOutputStream,
					boolean pDebugMode) throws IOException;

	String getErrorMessage(Throwable pThrowable);

}
