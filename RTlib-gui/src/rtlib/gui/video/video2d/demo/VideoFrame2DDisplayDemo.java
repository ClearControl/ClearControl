package rtlib.gui.video.video2d.demo;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class VideoFrame2DDisplayDemo
{

	@Test
	public void test() throws InvocationTargetException,
										InterruptedException
	{
		EventQueue.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					final VideoFrame2DDisplay lVideoFrame2DDisplay = new VideoFrame2DDisplay();
					lVideoFrame2DDisplay.setVisible(true);

					while (lVideoFrame2DDisplay.isVisible())
					{
						Thread.sleep(100);
					}
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

}
