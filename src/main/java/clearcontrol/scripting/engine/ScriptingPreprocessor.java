package clearcontrol.scripting.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

public class ScriptingPreprocessor
{

  public static String process(final Class pClassInPackageWhereScriptsCanBeFound,
                               final String pPathForFindingScripts,
                               final String pScriptString)
  {
    return process(pClassInPackageWhereScriptsCanBeFound,
                   pPathForFindingScripts,
                   pScriptString,
                   new HashSet<String>());
  }

  public static String process(final Class pClassForFindingScripts,
                               final String pPathForFindingScripts,
                               final String pScriptString,
                               final HashSet<String> pAllreadyIncludedFiles)
  {
    final StringBuilder lStringBuilder = new StringBuilder();

    final Scanner lScanner = new Scanner(pScriptString);
    lScanner.useDelimiter("\n");

    while (lScanner.hasNext())
    {
      final String lLine = lScanner.nextLine();
      if (lLine.startsWith("//include"))
      {
        String lFileName = lLine.substring(9);
        lFileName = lFileName.trim();
        final File lIncludedFile = new File(lFileName);

        try
        {
          if (lIncludedFile.exists()
              && !pAllreadyIncludedFiles.contains(lFileName))
          {
            pAllreadyIncludedFiles.add(lFileName);
            final FileInputStream lFileInputStream =
                                                   new FileInputStream(lIncludedFile);
            final String lIncludedFileString =
                                             IOUtils.toString(lFileInputStream);
            final String lPreProcessedIncludedFileString =
                                                         process(pClassForFindingScripts,
                                                                 pPathForFindingScripts,
                                                                 lIncludedFileString,
                                                                 pAllreadyIncludedFiles);
            lStringBuilder.append(lPreProcessedIncludedFileString);
            lStringBuilder.append("\n");
          }
        }
        catch (final IOException e)
        {
          System.err.format("File not included!! Cannot read file: %s \n",
                            lFileName);
        }
      }
      else if (lLine.startsWith("//coreinclude"))
      {
        String lFileName = lLine.substring(13);
        lFileName = lFileName.trim();

        if (!pAllreadyIncludedFiles.contains(lFileName))
        {
          pAllreadyIncludedFiles.add(lFileName);
          try
          {
            final InputStream lResourceAsStream =
                                                pClassForFindingScripts.getResourceAsStream(pPathForFindingScripts
                                                                                            + lFileName);

            final String lIncludedFileString =
                                             IOUtils.toString(lResourceAsStream);
            final String lPreProcessedIncludedFileString =
                                                         process(pClassForFindingScripts,
                                                                 pPathForFindingScripts,
                                                                 lIncludedFileString,
                                                                 pAllreadyIncludedFiles);
            lStringBuilder.append(lPreProcessedIncludedFileString);
            lStringBuilder.append("\n");
          }
          catch (final Throwable e)
          {
            System.err.format("File not included!! Cannot read file: %s \n",
                              pPathForFindingScripts + lFileName);
          }
        }

      }
      else if (lLine.startsWith("package"))
      {
        lStringBuilder.append("// commented line: ");
        lStringBuilder.append(lLine);
        lStringBuilder.append("\n");
      }
      else
      {
        lStringBuilder.append(lLine);
        lStringBuilder.append("\n");
      }
    }

    return lStringBuilder.toString();
  }
}
