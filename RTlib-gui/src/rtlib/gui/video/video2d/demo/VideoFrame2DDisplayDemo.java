package rtlib.gui.video.video2d.demo;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.imglib2.img.basictypeaccess.offheap.ByteOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.swing.JButtonBoolean;
import rtlib.gui.swing.JSliderDouble;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import coremem.ContiguousMemoryInterface;

public class VideoFrame2DDisplayDemo
{
	static volatile boolean sDisplay = true;
	static volatile double sValue = 1;

	private volatile long rnd;

	private void generateNoiseBuffer(final ContiguousMemoryInterface pContiguousMemory)
	{
		// System.out.println(rnd);

		final int lBufferLength = (int) pContiguousMemory.getSizeInBytes();
		for (int i = 0; i < lBufferLength; i++)
		{
			rnd = ((rnd % 257) * i) + 1 + (rnd << 7);
			final byte lValue = (byte) ((rnd & 0xFF) * sValue); // Math.random()
			// System.out.println(lValue);
			pContiguousMemory.setByteAligned(i, lValue);
		}
	}

	@Test
	public void demo() throws InvocationTargetException,
										InterruptedException
	{
		final Stack2DDisplay<UnsignedByteType> lVideoDisplayDevice = new Stack2DDisplay<UnsignedByteType>(new UnsignedByteType(),
																																																			512,
																																																			512);
		lVideoDisplayDevice.setVisible(true);
		lVideoDisplayDevice.start();

		final OffHeapPlanarStack<UnsignedByteType, ByteOffHeapAccess> lStack = new OffHeapPlanarStack<UnsignedByteType, ByteOffHeapAccess>(	0L,
																																																																				0L,
																																																																				new UnsignedByteType(),
																																																																				512,
																																																																				512,
																																																																				1);
		final ObjectVariable<StackInterface<UnsignedByteType, ?>> lStackVariable = lVideoDisplayDevice.getFrameReferenceVariable();

		final Runnable lRunnable = () -> {
			while (true)
			{
				if (sDisplay)
				{
					generateNoiseBuffer(lStack.getContiguousMemory((int) (lStack.getDepth() / 2)));
					lStackVariable.setReference(lStack);
				}
				ThreadUtils.sleep(1, TimeUnit.MILLISECONDS);
			}
		};

		final Thread lThread = new Thread(lRunnable);
		lThread.setName("DEMO");
		lThread.setDaemon(true);
		lThread.start();

		final JFrame lJFrame = runDemo(lVideoDisplayDevice);

		while (lVideoDisplayDevice.getDisplayOnVariable()
															.getBooleanValue() && lJFrame.isVisible())
		{
			Thread.sleep(100);
		}
	}

	public JFrame runDemo(Stack2DDisplay<UnsignedByteType> pVideoDisplayDevice)	throws InterruptedException,
																																							InvocationTargetException
	{

		final JFrame lJFrame = new JFrame("VideoFrame2DDisplayDemo");

		SwingUtilities.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					lJFrame.setBounds(100, 100, 450, 300);
					final JPanel mcontentPane = new JPanel();
					mcontentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
					mcontentPane.setLayout(new BorderLayout(0, 0));
					lJFrame.setContentPane(mcontentPane);
					lJFrame.setVisible(true);

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

					lDoubleVariable.sendUpdatesTo(new DoubleVariable(	"SliderDoubleEventHook",
																														0)
					{

						@Override
						public double setEventHook(	final double pOldValue,
																				final double pNewValue)
						{
							sValue = pNewValue;
							System.out.println(pNewValue);

							return super.setEventHook(pOldValue, pNewValue);
						}
					});

				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		return lJFrame;
	}
}
