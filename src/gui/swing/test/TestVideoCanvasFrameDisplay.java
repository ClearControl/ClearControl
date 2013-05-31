package gui.swing.test;

import frames.Frame;
import gui.swing.JButtonBoolean;
import gui.swing.JSliderDouble;
import gui.swing.old.VideoCanvasFrameDisplay;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.nio.ByteBuffer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utils.concurency.thread.EnhancedThread;
import variable.booleanv.BooleanVariable;
import variable.doublev.DoubleVariable;
import variable.objectv.ObjectVariable;

public class TestVideoCanvasFrameDisplay extends JFrame
{

	static volatile boolean sDisplay = true;
	static volatile double sValue = 1;
	static volatile int sImageSize = 512;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					final TestVideoCanvasFrameDisplay frame = new TestVideoCanvasFrameDisplay();
					frame.setVisible(true);
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	private final JPanel mcontentPane;

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

		final VideoCanvasFrameDisplay lVideoDisplayDevice = new VideoCanvasFrameDisplay(512,
																																										512);
		lVideoDisplayDevice.setLinearInterpolation(true);
		lVideoDisplayDevice.setSyncToRefresh(false);

		mcontentPane.add(lVideoDisplayDevice, BorderLayout.CENTER);
		final ObjectVariable<Frame> lFrameVariable = lVideoDisplayDevice.getFrameReferenceVariable();

		final JSliderDouble lGrayValueJSliderDouble = new JSliderDouble("gray value");
		final DoubleVariable lGrayValueDoubleVariable = lGrayValueJSliderDouble.getDoubleVariable();
		mcontentPane.add(lGrayValueJSliderDouble, BorderLayout.SOUTH);

		final JButtonBoolean lJButtonBoolean = new JButtonBoolean(false,
																															"Display",
																															"No Display");
		mcontentPane.add(lJButtonBoolean, BorderLayout.NORTH);

		final BooleanVariable lStartStopVariable = lJButtonBoolean.getBooleanVariable();

		lStartStopVariable.sendUpdatesTo(new DoubleVariable(0)
		{
			@Override
			public double setEventHook(final double pNewValue)
			{
				final boolean lBoolean = BooleanVariable.double2boolean(pNewValue);
				sDisplay = lBoolean;
				System.out.println("sDisplay=" + sDisplay);
				return pNewValue;
			}
		});

		final JButtonBoolean lJButtonBoolean2 = new JButtonBoolean(	"Push Button",
																																"");
		mcontentPane.add(lJButtonBoolean2, BorderLayout.EAST);

		final BooleanVariable lPushVariable = lJButtonBoolean2.getBooleanVariable();

		lPushVariable.sendUpdatesTo(new DoubleVariable(0)
		{
			@Override
			public double setEventHook(final double pNewValue)
			{
				lGrayValueDoubleVariable.setValue(0.5);
				System.out.println("push!!!");
				return pNewValue;
			}
		});

		final Frame lFrame = new Frame(0, sImageSize, sImageSize, 1);

		lGrayValueDoubleVariable.sendUpdatesTo(new DoubleVariable(0)
		{
			@Override
			public double setEventHook(final double pNewValue)
			{
				sValue = pNewValue;
				System.out.println("new gray value+ " + pNewValue);
				return pNewValue;
			}
		});

		final EnhancedThread lEnhancedThread = new EnhancedThread()
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

	private void generateNoiseBuffer(	final ByteBuffer pVideoByteBuffer,
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
