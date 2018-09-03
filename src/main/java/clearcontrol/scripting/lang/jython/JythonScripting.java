package clearcontrol.scripting.lang.jython;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import clearcontrol.scripting.lang.ScriptingLanguageInterface;

public class JythonScripting implements ScriptingLanguageInterface
{
  @Override
  public String getName()
  {
    return "Jython";
  }

  @Override
  public String getPostamble()
  {
    return "";
  }

  @Override
  public String getPreamble()
  {
    return "";
  }

  @Override
  public void runScript(String pScriptName,
                        String pPreambleString,
                        String pScriptString,
                        String pPostambleString,
                        Map<String, Object> pMap,
                        OutputStream pOutputStream,
                        boolean pDebugMode) throws IOException
  {
    JythonUtils.runScript(pScriptName,
                          pPreambleString,
                          pScriptString,
                          pPostambleString,
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
