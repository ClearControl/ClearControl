package rtlib.scripting.autoimport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class ClassPathResolver
{

	static HashSet<String> sPackages;

	public static List<String> getFullyQualifiedNames(String pSimpleName)
	{
		return getFullyQualifiedNames("rtlib", pSimpleName);
	}

	public static List<String> getFullyQualifiedNames(String pBasePackage,
																										String pSimpleName)
	{
		if (sPackages == null)
		{
			sPackages = getPackagesFromClassPath(pBasePackage);
			sPackages.addAll(getPackagesFromCurrentClassLoader());
		}

		final List<String> lFullyQualifiedNames = new ArrayList<String>();
		for (final String aPackage : sPackages)
		{
			final String lCandidateFullyQualifiedName = aPackage + "."
																									+ pSimpleName;
			try
			{
				Class.forName(lCandidateFullyQualifiedName);
				lFullyQualifiedNames.add(lCandidateFullyQualifiedName);
			}
			catch (final Exception e)
			{
				/*System.out.format("package '%s' does not exist. \n",
													lCandidateFullyQualifiedName);/**/
			}
		}
		return lFullyQualifiedNames;
	}

	public static HashSet<String> getPackagesFromCurrentClassLoader()
	{
		final HashSet<String> lPackages = new HashSet<String>();
		for (final Package lPackage : Package.getPackages())
		{
			lPackages.add(lPackage.getName());
		}
		return lPackages;
	}

	public static HashSet<String> getPackagesFromClassPath(String pBasePackage)
	{
		final Reflections lReflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(pBasePackage))
																																								.setScanners(new SubTypesScanner(false)));

		final HashSet<String> lPackages = new HashSet<String>();
		for (final String lType : lReflections.getAllTypes())
		{
			try
			{
				final Class<?> lClass = Class.forName(lType);
				final Package lPackage = lClass.getPackage();
				final String lName = lPackage.getName();
				lPackages.add(lName);
			}
			catch (final Throwable e)
			{

			}
		}
		return lPackages;
	}

}
