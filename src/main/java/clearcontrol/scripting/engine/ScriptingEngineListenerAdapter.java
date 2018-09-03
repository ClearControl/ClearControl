package clearcontrol.scripting.engine;

import java.util.Map;

public class ScriptingEngineListenerAdapter implements
                                            ScriptingEngineListener
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

  }

  @Override
  public void asynchronousResult(ScriptingEngine pScriptingEngine,
                                 String pScriptString,
                                 Map<String, Object> pBinding,
                                 Throwable pThrowable,
                                 String pErrorMessage)
  {
    if (pThrowable != null)
      pThrowable.printStackTrace();
  }

  @Override
  public void afterScriptExecution(ScriptingEngine pScriptingEngine,
                                   String pScriptString)
  {

  }

  @Override
  public void scriptAlreadyExecuting(ScriptingEngine pScriptingEngine)
  {

  }
}
