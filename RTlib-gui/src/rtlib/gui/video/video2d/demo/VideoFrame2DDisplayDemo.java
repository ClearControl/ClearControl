package rtlib.gui.video.video2d.demo;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.nio.ByteBuffer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import rtlib.core.concurrent.thread.EnhancedThread;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.swing.JButtonBoolean;
import rtlib.gui.swing.JSliderDouble;
import rtlib.gui.video.video2d.VideoFrame2DDisplay;
import rtlib.stack.Stack;

public class VideoFrame2DDisplayDemo extends JFrame
{

	static volatile boolean sDisplay = true;
	static volatile double sValue = 1;

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
					final VideoFrame2DDisplayDemo VideoFrame = new VideoFrame2DDisplayDemo();
					VideoFrame.setVisible(true);
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
	 * Create the VideoFrame.
	 */
	public VideoFrame2DDisplayDemo()
	{
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		mcontentPane = new JPanel();
		mcontentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		mcontentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(mcontentPane);

		final VideoFrame2DDisplay lVideoDisplayDevice = new VideoFrame2DDisplay(512,
																																						512);
		lVideoDisplayDevice.setLinearInterpolation(true);
		lVideoDisplayDevice.setSyncToRefresh(false);
		lVideoDisplayDevice.setVisible(true);

		final ObjectVariable<Stack> lFrameVariable = lVideoDisplayDevice.getFrameReferenceVariable();

		final JSliderDouble lJSliderDouble = new JSliderDouble("gray value");
		mcontentPane.add(lJSliderDouble, BorderLayout.SOUTH);

		final JButtonBoolean lJButtonBoolean = new JButtonBoolean(false,
																															"Display",
																															"No Display");
		mcontentPane.add(lJButtonBoolean, BorderLayout.NORTH);

		final BooleanVariable lStartStopVariable = lJButtonBoolean.getBooleanVariable();

		lStartStopVariable.sendUpdatesTo(new DoubleVariable("StartStopVariableHook",
																												0)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				final boolean lBoolean = BooleanVariable.double2boolean(pNewValue);
				sDisplay = lBoolean;
				System.out.println("sDisplay=" + sDisplay);
				return super.setEventHook(pOldValue, pNewValue);
			}
		});

		final DoubleVariable lDoubleVariable = lJSliderDouble.getDoubleVariable();

		final Stack lFrame = new Stack(0L, 0L, 512, 512, 1, 1);

		lDoubleVariable.sendUpdatesTo(new DoubleVariable(	"SliderDoubleEventHook",
																											0)
		{

			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				sValue = pNewValue;
				System.out.println(pNewValue);

				// generateNoiseBuffer(lFrame.buffer, pNewValue);
				// lFrameVariable.setReference(lFrame);
				return super.setEventHook(pOldValue, pNewValue);
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
						// TODO: get teh bufer using KAM source!!
						// generateNoiseBuffer(lFrame.getByteBuffer(), sValue);
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
			pVideoByteBuffer.put(i, lValue);
		}
	}
}
