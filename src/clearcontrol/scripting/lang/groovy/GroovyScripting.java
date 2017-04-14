package clearcontrol.scripting.lang.groovy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import clearcontrol.scripting.lang.ScriptingLanguageInterface;

/**
 * Groovy scripting language interface
 *
 * @author royer
 */
public class GroovyScripting implements ScriptingLanguageInterface
{

  @Override
  public String getName()
  {
    return "Groovy";
  }

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
  public void runScript(String pScriptName,
                        String pPreambleString,
                        String pScriptString,
                        String pPostambleString,
                        Map<String, Object> pMap,
                        OutputStream pOutputStream,
                        boolean pDebugMode) throws IOException
  {
    GroovyUtils.runScript(pScriptName,
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
    return pThrowable.getClass().getSimpleName() + "->"
           + pThrowable.getMessage()
           + "\n"
           + getStackTrace(pThrowable);
  }

  private String getStackTrace(Throwable pThrowable)
  {
    if (pThrowable == null)
      return "";
    StringBuilder lStringBuilder = new StringBuilder();

    StackTraceElement[] lStackTrace = pThrowable.getStackTrace();
    for (StackTraceElement lStackTraceElement : lStackTrace)
      if (!lStackTraceElement.getClassName().contains("sun.")
          && !lStackTraceElement.getClassName()
                                .contains("org.codehaus.groovy.")
          && !lStackTraceElement.getClassName()
                                .contains("java.lang.reflect.")
          && !lStackTraceElement.getClassName()
                                .contains("groovy.lang."))

      {
        lStringBuilder.append("\t" + lStackTraceElement.toString());
        lStringBuilder.append("\n");
      }

    return lStringBuilder.toString();
  }

}
