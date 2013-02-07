package gui.swing.test;

import frames.Frame;
import gui.swing.JSliderDouble;
import gui.swing.VideoCanvasFrameDisplay;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.nio.ByteBuffer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utils.concurency.thread.EnhancedThread;
import variable.booleanv.BooleanVariable;
import variable.doublev.DoubleInputVariableInterface;
import variable.doublev.DoubleVariable;
import variable.objectv.ObjectVariable;
import gui.swing.JButtonBoolean;

public class TestVideoCanvasFrameDisplay extends JFrame
{

	static volatile boolean sDisplay = true;
	static volatile double sValue = 1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					TestVideoCanvasFrameDisplay frame = new TestVideoCanvasFrameDisplay();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	private JPanel mcontentPane;

	/**
	 * Create the frame.
	 */
	public TestVideoCanvasFrameDisplay()
	{
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		mcontentPane = new JPanel();
		mcontentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		mcontentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(mcontentPane);

		VideoCanvasFrameDisplay lVideoDisplayDevice = new VideoCanvasFrameDisplay(512, 512);
		lVideoDisplayDevice.setLinearInterpolation(true);
		lVideoDisplayDevice.setSyncToRefresh(false);

		mcontentPane.add(lVideoDisplayDevice, BorderLayout.CENTER);
		final ObjectVariable<Frame> lFrameVariable = lVideoDisplayDevice.getFrameReferenceVariable();

		JSliderDouble lJSliderDouble = new JSliderDouble("gray value");
		mcontentPane.add(lJSliderDouble, BorderLayout.SOUTH);

		JButtonBoolean lJButtonBoolean = new JButtonBoolean(false,
																												"Display",
																												"No Display");
		mcontentPane.add(lJButtonBoolean, BorderLayout.NORTH);

		BooleanVariable lStartStopVariable = lJButtonBoolean.getBooleanVariable();

		lStartStopVariable.sendUpdatesTo(new DoubleInputVariableInterface()
		{
			@Override
			public void setValue(Object pDoubleEventSource, double pNewValue)
			{
				final boolean lBoolean = BooleanVariable.double2boolean(pNewValue);
				sDisplay = lBoolean;
				System.out.println("sDisplay=" + sDisplay);
			}
		});

		DoubleVariable lDoubleVariable = lJSliderDouble.getDoubleVariable();

		final Frame lFrame = new Frame(0, 512, 512, 1);

		lDoubleVariable.sendUpdatesTo(new DoubleInputVariableInterface()
		{

			@Override
			public void setValue(Object pDoubleEventSource, double pNewValue)
			{
				sValue = pNewValue;
				System.out.println(pNewValue);

				// generateNoiseBuffer(lFrame.buffer, pNewValue);
				// lFrameVariable.setReference(lFrame);

			}
		});

		EnhancedThread lEnhancedThread = new EnhancedThread()
		{
			@Override
			public boolean initiate()
			{
				while (true)
				{
					if (sDisplay)
					{
						generateNoiseBuffer(lFrame.buffer, sValue);
						lFrameVariable.setReference(lFrame);
					}
					EnhancedThread.sleep(1);
				}
			}
		};

		lEnhancedThread.start();

	}

	private void generateNoiseBuffer(	ByteBuffer pVideoByteBuffer,
																		final double pAmplitude)
	{
		pVideoByteBuffer.clear();

		final int lBufferLength = pVideoByteBuffer.limit();
		for (int i = 0; i < lBufferLength; i++)
		{
			final byte lValue = (byte) ((int) (Math.random() * 256 * pAmplitude) % 256);
			// System.out.print(lValue);
			pVideoByteBuffer.put(lValue);
		}
	}
}
