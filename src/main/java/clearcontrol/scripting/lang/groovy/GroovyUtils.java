package clearcontrol.scripting.lang.groovy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import clearcontrol.scripting.autoimport.AutoImport;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

public class GroovyUtils
{

  static final CompilerConfiguration cCompilerConfiguration =
                                                            new CompilerConfiguration();

  static final ImportCustomizer cImportCustomizer =
                                                  new ImportCustomizer();

  static
  {
    cCompilerConfiguration.addCompilationCustomizers(cImportCustomizer);
    /*cCompilerConfiguration.getOptimizationOptions().put("indy", true);
    cCompilerConfiguration.addCompilationCustomizers(new ASTTransformationCustomizer(CompileStatic.class));/**/
  }

  public static CompilerConfiguration getCompilerConfiguration()
  {
    return cCompilerConfiguration;
  }

  public static void addImports(final String... pClassNames)
  {
    cImportCustomizer.addImports(pClassNames);
  }

  public static void addStaticStarImport(final String... pClassNames)
  {
    cImportCustomizer.addStaticStars(pClassNames);
  }

  public static Object runScript(final String pScriptName,
                                 final String pPreambleString,
                                 final String pScriptString,
                                 final String pPostambleString,
                                 final Map<String, Object> pMap,
                                 final OutputStream pOutputStream,
                                 final boolean pDebugMode) throws IOException
  {
    return runScript(pScriptName,
                     pPreambleString,
                     pScriptString,
                     pPostambleString,
                     new Binding(pMap),
                     pOutputStream,
                     pDebugMode);
  }

  public static Object runScript(final String pScriptName,
                                 final String pScriptString,
                                 final Map<String, Object> pMap,
                                 final OutputStream pOutputStream,
                                 final boolean pDebugMode) throws IOException
  {
    return runScript(pScriptName,
                     pScriptString,
                     new Binding(pMap),
                     pOutputStream,
                     pDebugMode);
  }

  public static Object runScript(final String pScriptName,
                                 final String pScriptString,
                                 final Binding pBinding,
                                 final OutputStream pOutputStream,
                                 final boolean pDebugMode) throws IOException
  {
    return runScript(pScriptName,
                     null,
                     pScriptString,
                     null,
                     pBinding,
                     pOutputStream,
                     pDebugMode);
  }

  public static Object runScript(final String pScriptName,
                                 final InputStream pPreambleInputStream,
                                 final InputStream pScriptInputStream,
                                 final InputStream pPostambleInputStream,
                                 final Binding pBinding,
                                 final OutputStream pOutputStream,
                                 final boolean pDebugMode) throws IOException,
                                                           CompilationFailedException
  {
    final String lPreambleString =
                                 IOUtils.toString(pPreambleInputStream);
    final String lScriptString = IOUtils.toString(pScriptInputStream);
    final String lPostambleString =
                                  IOUtils.toString(pPostambleInputStream);

    return runScript(pScriptName,
                     lPreambleString,
                     lScriptString,
                     lPostambleString,
                     pBinding,
                     pOutputStream,
                     pDebugMode);
  }

  public static Object runScript(final String pScriptName,
                                 final String pPreambleString,
                                 final String pScriptString,
                                 final String pPostambleString,
                                 Binding pBinding,
                                 final OutputStream pOutputStream,
                                 final boolean pDebugMode) throws IOException
  {
    if (pBinding == null)
      pBinding = new Binding();

    if (pOutputStream != null)
    {
      pBinding.setProperty("out", new PrintStream(pOutputStream));
      pBinding.setProperty("err", new PrintStream(pOutputStream));
    }

    final GroovyShell lGroovyShell =
                                   new GroovyShell(GroovyUtils.class.getClassLoader(),
                                                   pBinding,
                                                   cCompilerConfiguration);

    final String lPreamble = pPreambleString != null ? pPreambleString
                                                     : "";

    final String lPostamble =
                            pPostambleString != null ? pPostambleString
                                                     : "";

    final String lPreambleAndScriptCombined = "////Preamble begin\n"
                                              + lPreamble
                                              + "////Preamble end\n\n"
                                              + "////Script begin\n"
                                              + pScriptString
                                              + "\n////Script end\n\n"
                                              + "////Postamble begin\n"
                                              + lPostamble
                                              + "\n////Postamble end\n\n";

    final String lImportsStatements =
                                    AutoImport.generateImportStatements(lPreambleAndScriptCombined);

    final String lPreambleAndScriptCombinedWithImports =
                                                       "////AutoImports begin\n"
                                                         + lImportsStatements
                                                         + "////AutoImports end\n\n"
                                                         + lPreambleAndScriptCombined;

    // System.out.println(lPreambleAndScriptCombinedWithImports);

    final Object lObject =
                         lGroovyShell.evaluate(lPreambleAndScriptCombinedWithImports,
                                               pScriptName);

    return lObject;

  }

}
