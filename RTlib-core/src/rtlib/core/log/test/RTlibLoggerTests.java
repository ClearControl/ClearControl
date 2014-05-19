package rtlib.core.log.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import rtlib.core.log.RTlibLogger;

public class RTlibLoggerTests
{

	private static final int cMaxWrites = 1000;

	@Test
	public void testWriteTests() throws IOException
	{
		RTlibLogger lLogger = RTlibLogger.getLogger("testsystem");

		for (int i = 1; i <= cMaxWrites; i++)
		{
			String lMessage = "testMAGIC" + i;
			lLogger.logInfo(RTlibLoggerTests.class, lMessage);
			lLogger.logWarning(RTlibLoggerTests.class, lMessage);
			lLogger.logError(RTlibLoggerTests.class, lMessage);
		}

		lLogger.flush();

		lLogger.waitForCompletion();
		lLogger.close();

		File logFile = lLogger.getLogDataFile();
		System.out.println(logFile);

		String lString = "_";
		InputStream lInputStream = new FileInputStream(logFile);
		try
		{
			 lString = IOUtils.toString(lInputStream);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			System.err.println(logFile.getAbsolutePath());
			fail();
		}
		finally
		{
			IOUtils.closeQuietly(lInputStream);
		}
		
	  //System.out.println(lString);
		assertTrue(lString.contains("MAGIC"+cMaxWrites));
	}

}
