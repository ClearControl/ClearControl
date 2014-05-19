package rtlib.core.file.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Formatter;

import org.junit.Test;

import rtlib.core.file.FileEventNotifier;
import rtlib.core.file.FileEventNotifierListener;
import rtlib.core.file.FileEventNotifier.FileEventKind;

public class FileEventNotifierTests
{

	protected int lEventCounter;

	@Test
	public void test1() throws Exception
	{
		final File lTestFile = File.createTempFile(	"FileEventNotifierTests",
																								"test");
		final File lOtherFile = File.createTempFile("FileEventNotifierTests",
																								"other");

		final FileEventNotifier lFileEventNotifier = new FileEventNotifier(lTestFile);

		lFileEventNotifier.addFileEventListener(new FileEventNotifierListener()
		{

			@Override
			public void fileEvent(final FileEventNotifier pThis,
														final File pFile,
														final FileEventKind pEventKind)
			{
				lEventCounter++;
				System.out.format("Received Event: %s %s %s \n",
													pThis.toString(),
													pFile.toString(),
													pEventKind.toString());
			}
		});

		lEventCounter = 0;
		lFileEventNotifier.startMonitoring();

		final Formatter lTestFileFormatter = new Formatter(lTestFile);

		lTestFileFormatter.format("test1\n");
		lTestFileFormatter.flush();
		Thread.sleep(1000);
		assertTrue(lEventCounter == 1);

		lTestFileFormatter.format("test2\n");
		lTestFileFormatter.flush();
		Thread.sleep(1000);
		assertTrue(lEventCounter == 2);

		final Formatter lOtherFileFormatter = new Formatter(lOtherFile);
		lOtherFileFormatter.format("test3\n");
		lOtherFileFormatter.flush();
		Thread.sleep(1000);
		assertTrue(lEventCounter == 2);

		lTestFile.delete();
		Thread.sleep(1000);
		assertTrue(lEventCounter == 3);

		lFileEventNotifier.stopMonitoring();

		lOtherFileFormatter.close();
		lTestFileFormatter.close();
		lFileEventNotifier.close();
	}
}
