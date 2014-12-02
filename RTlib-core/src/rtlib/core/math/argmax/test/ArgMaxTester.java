package rtlib.core.math.argmax.test;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import rtlib.core.math.argmax.ArgMaxFinder1D;

import com.google.common.io.Resources;

public class ArgMaxTester
{

	public static TDoubleArrayList loadData(Class<?> pContextClass,
																					String pRessource,
																					int pColumn) throws IOException,
																											URISyntaxException
	{
		TDoubleArrayList lList = new TDoubleArrayList();

		try (BufferedReader lBufferedReader = Files.newBufferedReader(Paths.get(Resources.getResource(pContextClass,
																																																	pRessource)
																																											.toURI())))
		{
			String lLine;
			while ((lLine = lBufferedReader.readLine()) != null)
			{
				String[] lSplittedLine = lLine.split("\t");
				if (pColumn < lSplittedLine.length)
				{
					String lCell = lSplittedLine[pColumn];
					if (!lCell.trim().isEmpty())
					{
						final double lValue = Double.parseDouble(lCell);
						lList.add(lValue);
					}
				}
			}
		}
		return lList;
	}

	public static void test(ArgMaxFinder1D pArgMaxFinder1D)	throws IOException,
																													URISyntaxException
	{
		for (int i = 1; i <= 9; i++)
		{
			TDoubleArrayList lY = loadData(	ArgMaxTester.class,
																			"./benchmark/Benchmark.txt",
																			i);
			final double LArgMaxReference = lY.get(0);
			lY.remove(0, 1);
			System.out.println(lY);

			TDoubleArrayList lX = loadData(	ArgMaxTester.class,
																			"./benchmark/Benchmark.txt",
																			0);
			lX.remove(0, 1);
			if (lY.size() < lX.size())
				lX.remove(lY.size(), lX.size() - lY.size());
			System.out.println(lX);

			System.out.println("LArgMaxReference: " + LArgMaxReference);

			double lArgmax = pArgMaxFinder1D.argmax(lX.toArray(),
																							lY.toArray());

			System.out.println("class: " + pArgMaxFinder1D
													+ " argmax: "
													+ lArgmax);

		}

	}
}
