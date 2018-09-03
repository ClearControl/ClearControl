package clearcontrol.scripting.engine;

import java.util.regex.Pattern;

public class ScriptChecks
{
  final static Pattern cWhilePattern =
                                     Pattern.compile("\\s*while\\W+");
  final static Pattern cForPattern = Pattern.compile("\\s*for\\W");

  public final static boolean check(final String pScriptString)
  {
    checkLoopingStructures(pScriptString);

    return true; // for now, maybe later we may have hard errors...
  }

  private final static void checkLoopingStructures(final String pScriptString)
  {
    if (cWhilePattern.matcher(pScriptString).find())
    {
      warning("The use of 'while(){}' is discouraged! use wait{} instead! ");
    }
    if (cForPattern.matcher(pScriptString).find())
    {
      warning("If you use 'for(){}' structures then make sure that you call sleep or yield within the innermost loop! ");
    }
  }

  public final static void warning(final String pString)
  {
    System.err.println(pString);
  }

}
