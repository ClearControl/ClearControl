package rtlib.scripting.lang.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

public class GroovyUtils
{

	static final CompilerConfiguration cCompilerConfiguration = new CompilerConfiguration();

	static final ImportCustomizer cImportCustomizer = new ImportCustomizer();

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

	public static void runScript(	final InputStream pPreambleInputStream,
																final String pScriptName,
																final InputStream pScriptInputStream,
																final Binding pBinding)	throws IOException,
																												CompilationFailedException
	{

		final GroovyShell lGroovyShell = new GroovyShell(	GroovyUtils.class.getClassLoader(),
																											pBinding,
																											cCompilerConfiguration);

		final String preamble = IOUtils.toString(pPreambleInputStream);
		final String script = IOUtils.toString(pScriptInputStream);
		final String combined = preamble + "\n" + script;
		lGroovyShell.evaluate(combined, pScriptName);

	}

	public static Object runScript(	final String pScriptName,
																	final String pScriptString,
																	final Map<String, Object> pMap,
																	final OutputStream pOutputStream,
																	final boolean pDebugMode) throws IOException
	{
		return runScript(	pScriptName,
											pScriptString,
											new Binding(pMap),
											pOutputStream,
											pDebugMode);
	}

	public static Object runScript(	final String pScriptName,
																	final String pScriptString,
																	final Binding pBinding,
																	final OutputStream pOutputStream,
																	final boolean pDebugMode) throws IOException
	{
		return runScript(	null,
											pScriptName,
											pScriptString,
											pBinding,
											pOutputStream,
											pDebugMode);
	}

	public static Object runScript(	final InputStream pPreambleInputStream,
																	final String pScriptName,
																	final String pScriptString,
																	Binding pBinding,
																	final OutputStream pOutputStream,
																	final boolean pDebugMode) throws IOException
	{
		if (pBinding == null)
			pBinding = new Binding();

		pBinding.setProperty("out", new PrintStream(pOutputStream));
		pBinding.setProperty("err", new PrintStream(pOutputStream));

		final GroovyShell lGroovyShell = new GroovyShell(	GroovyUtils.class.getClassLoader(),
																											pBinding,
																											cCompilerConfiguration);

		final String preamble = pPreambleInputStream != null ? IOUtils.toString(pPreambleInputStream)
																												: "";

		final String combined = preamble + "\n" + pScriptString;

		final Object lObject = lGroovyShell.evaluate(	combined,
																									pScriptName);
		return lObject;

	}

}
