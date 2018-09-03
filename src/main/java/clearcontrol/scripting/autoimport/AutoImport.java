package clearcontrol.scripting.autoimport;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoImport
{
  private final static Pattern sClassPattern =
                                             Pattern.compile("\\W(\\p{Upper}[\\p{Alpha}\\p{Digit}]+)\\W");

  public static String generateImportStatements(String pScriptText)
  {
    return generateImportStatements(new String[]
    { "clearcontrol", "coremem" }, pScriptText);
  }

  public static String generateImportStatements(String[] pBasePackages,
                                                String pScriptText)
  {
    final HashSet<String> lClassNames =
                                      extractClassNames(pScriptText);

    final StringBuilder lImportStatements = new StringBuilder();

    for (final String lClassName : lClassNames)
    {

      final HashSet<String> lFullyQualifiedNames =
                                                 new HashSet<>(10000);

      for (String lBasePackage : pBasePackages)
      {

        lFullyQualifiedNames.addAll(ClassPathResolver.getFullyQualifiedNames(lBasePackage,
                                                                             lClassName));

      }

      if (lFullyQualifiedNames.size() >= 1)
      {
        lImportStatements.append(importStatement(lFullyQualifiedNames.iterator()
                                                                     .next()));
        if (lFullyQualifiedNames.size() > 1)
          System.err.format("Warning: could not resolve %s to a single class!\n found these: %s, picking first. \n",
                            lClassName,
                            lFullyQualifiedNames);
      }
      else if (lFullyQualifiedNames.size() == 0)
      {
        System.err.format("Could not resolve %s !\n",
                          lClassName,
                          lFullyQualifiedNames);
      }
    }

    return lImportStatements.toString();
  }

  private static String importStatement(String pFullyQualifiedClassName)
  {
    return String.format("import %s;\n", pFullyQualifiedClassName);
  }

  private static HashSet<String> extractClassNames(String pScriptText)
  {
    final HashSet<String> lClassesNames = new HashSet<String>();
    final String[] lScriptSplitIntoLines = pScriptText.split("\\n");
    for (final String lLine : lScriptSplitIntoLines)
    {
      final String lTrimmedLine = lLine.trim();
      if (!(lTrimmedLine.startsWith("//")
            || lTrimmedLine.startsWith("/*")))
      {
        final Matcher lMatcher = sClassPattern.matcher(lLine);

        while (lMatcher.find())
        {
          final String lClassName = lMatcher.group(1);
          lClassesNames.add(lClassName);
        }
      }
    }

    return lClassesNames;
  }
}
