package rtlib.scripting.autoimport;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoImport
{
	private final static Pattern sClassPattern = Pattern.compile("\\W(\\p{Upper}[\\p{Alpha}\\p{Digit}]+)\\W");

	public static String generateImportStatements(String pScriptText)
	{
		return generateImportStatements("rtlib", pScriptText);
	}

	public static String generateImportStatements(	String pBasePackage,
													String pScriptText)
	{
		final HashSet<String> lClassNames = extractClassNames(pScriptText);

		final StringBuilder lImportStatements = new StringBuilder();

		for (final String lClassName : lClassNames)
		{
			final List<String> lFullyQualifiedNames = ClassPathResolver.getFullyQualifiedNames(	pBasePackage,
																								lClassName);

			if (lFullyQualifiedNames.size() == 1)
			{
				lImportStatements.append(importStatement(lFullyQualifiedNames.get(0)));
			}
			else if (lFullyQualifiedNames.size() > 1)
			{
				System.err.format(	"Could not resolve %s to a single class!\n found these: %s\n",
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
			if (!(lTrimmedLine.startsWith("//") || lTrimmedLine.startsWith("/*")))
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
