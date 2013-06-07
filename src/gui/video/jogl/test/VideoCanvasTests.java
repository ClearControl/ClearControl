package gui.video.jogl.test;

import static org.junit.Assert.*;


import gui.video.jogl.old.VideoCanvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JFrame;

import org.junit.Test;

public class VideoCanvasTests
{
	static
	{
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	@Test
	public void simpleRandomDataTest() throws InterruptedException
	{
		VideoCanvas lVideoCanvas = new VideoCanvas(1, 512, 512);
		

		final JFrame jframe = new JFrame("Test");
		jframe.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowevent)
			{
				jframe.dispose();
				System.exit(0);
			}
		});

		jframe.getContentPane().add(lVideoCanvas,
																BorderLayout.CENTER);
		jframe.setSize(640, 480);
		jframe.setBackground(Color.black);
		jframe.setVisible(true);

		ByteBuffer lVideoByteBuffer = ByteBuffer.allocateDirect(lVideoCanvas.getBufferLength()).order(ByteOrder.nativeOrder());

		lVideoCanvas.setSourceBuffer(lVideoByteBuffer);

		while (true)
		{
			generateNoiseBuffer(lVideoByteBuffer);
			lVideoCanvas.notifyNewFrame();
			lVideoCanvas.display();
			Thread.sleep(10);
		}

	}

	private void generateNoiseBuffer(ByteBuffer pVideoByteBuffer)
	{
		pVideoByteBuffer.clear();

		final int lBufferLength = pVideoByteBuffer.limit();
		for (int i = 0; i < lBufferLength; i++)
		{
			final byte lValue = (byte) ((int) (Math.random() * 256) % 256);
			// System.out.print(lValue);
			pVideoByteBuffer.put(lValue);
		}
	}
}
