package clearcontrol.scripting.engine;

import java.util.Map;

/**
 * Listener for script engine.
 * 
 * @author royer
 */
public interface ScriptingEngineListener
{

  /**
   * Notifies that the script was updated.
   * 
   * @param pScriptingEngine
   *          scipt engine
   * @param pScriptString
   *          new script string
   */
  void updatedScript(ScriptingEngine pScriptingEngine,
                     String pScriptString);

  /**
   * Notifies that a script is already running.
   * 
   * @param pScriptingEngine
   */
  void scriptAlreadyExecuting(ScriptingEngine pScriptingEngine);

  /**
   * Notifies that script is about to be executed
   * 
   * @param pScriptingEngine
   *          script engine
   * @param pScriptString
   *          script string
   */
  void beforeScriptExecution(ScriptingEngine pScriptingEngine,
                             String pScriptString);

  /**
   * Notifies of the script result, binding after execution and possibly errors
   * 
   * @param pScriptingEngine
   *          script engine
   * @param pScriptString
   *          script string
   * @param pBinding
   *          binding after execution
   * @param pThrowable
   *          throwable
   * @param pErrorMessage
   *          error message
   */
  public void asynchronousResult(ScriptingEngine pScriptingEngine,
                                 String pScriptString,
                                 Map<String, Object> pBinding,
                                 Throwable pThrowable,
                                 String pErrorMessage);

  /**
   * Notifies that script finished to execute.
   * 
   * @param pScriptingEngine
   *          script engine
   * @param pScriptString
   *          script string
   */
  void afterScriptExecution(ScriptingEngine pScriptingEngine,
                            String pScriptString);

}
