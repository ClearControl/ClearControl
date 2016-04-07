package rtlib.gui.video.video2d.demo;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.junit.Test;

import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.gui.swing.JButtonBoolean;
import rtlib.gui.swing.JSliderDouble;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;

public class VideoFrame2DDisplayDemo
{
	static volatile boolean sDisplay = true;
	static volatile double sValue = 1;

	private volatile long rnd;

	private void generateNoiseBuffer(	double pIntensity,
																		final ContiguousMemoryInterface pContiguousMemory)
	{
		// System.out.println(rnd);

		final int lBufferLength = (int) pContiguousMemory.getSizeInBytes();
		final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(pContiguousMemory);
		for (int i = 0; i < lBufferLength; i++)
		{
			rnd = ((rnd % 257) * i) + 1 + (rnd << 7);
			final byte lValue = (byte) ((rnd & 0xFF) * pIntensity * sValue); // Math.random()
			lContiguousBuffer.writeByte(lValue);
			// System.out.println(lValue);
		}
	}

	@Test
	public void demo() throws InvocationTargetException,
										InterruptedException
	{
		final Stack2DDisplay lVideoDisplayDevice = new Stack2DDisplay(512,
																																	512);

		lVideoDisplayDevice.getManualMinMaxIntensityOnVariable()
												.setValue(true);
		lVideoDisplayDevice.open();

		final int lSizeX = 256;
		final int lSizeY = lSizeX;
		final int lSizeZ = 16;

		@SuppressWarnings("unchecked")
		final OffHeapPlanarStack lStack = OffHeapPlanarStack.createStack(false,
																																													lSizeX,
																																													lSizeY,
																																													lSizeZ);

		final ObjectVariable<StackInterface> lStackVariable = lVideoDisplayDevice.getInputStackVariable();

		final Runnable lRunnable = () -> {
			while (true)
			{
				if (sDisplay)
				{
					for (int i = 0; i < lStack.getDepth(); i++)
						generateNoiseBuffer(1 + i, lStack.getContiguousMemory(i));

					lStackVariable.setReference(lStack);
					// System.out.println(lStack);
				}
				ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);
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

		lVideoDisplayDevice.close();
	}

	public JFrame runDemo(Stack2DDisplay pVideoDisplayDevice)	throws InterruptedException,
																														InvocationTargetException
	{

		final JFrame lJFrame = new JFrame("TextFieldDoubleDemo");

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

					final JSliderDouble lJSliderDouble = new JSliderDouble("gray size");
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
						public Double setEventHook(	final Double pOldValue,
																				final Double pNewValue)
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
						public Double setEventHook(	final Double pOldValue,
																				final Double pNewValue)
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
