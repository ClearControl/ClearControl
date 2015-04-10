package rtlib.scripting.engine;

import java.util.Map;

public interface ScriptingEngineListener
{

	void updatedScript(ScriptingEngine pScriptingEngine, String pScript);

	void beforeScriptExecution(	ScriptingEngine pScriptingEngine,
															String pScriptString);

	public void asynchronousResult(	ScriptingEngine pScriptingEngine,
																	String pScriptString,
																	Map<String, Object> pBinding,
																	Throwable pThrowable,
																	String pErrorMessage);

	void afterScriptExecution(ScriptingEngine pScriptingEngine,
														String pScriptString);

}
