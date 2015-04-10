package rtlib.scripting.lang;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface ScriptingLanguageInterface
{

	void runScript(	String pScriptName,
									String pScriptString,
									Map<String, Object> pMap,
									OutputStream pOutputStream,
									boolean pDebugMode) throws IOException;

	String getErrorMessage(Throwable pThrowable);

}
