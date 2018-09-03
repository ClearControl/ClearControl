package clearcontrol.gui.video.video2d.demo;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.swing.JButtonBoolean;
import clearcontrol.gui.swing.JSliderDouble;
import clearcontrol.gui.video.video2d.Stack2DDisplay;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;

import org.junit.Test;

public class VideoFrame2DDisplayDemo
{
  static volatile boolean sDisplay = true;
  static volatile double sValue = 1;

  private volatile long rnd;

  private void generateNoiseBuffer(double pIntensity,
                                   final ContiguousMemoryInterface pContiguousMemory,
                                   final int pWidth,
                                   final int pHeight)
  {
    // System.out.println(rnd);

    final ContiguousBuffer lContiguousBuffer =
                                             new ContiguousBuffer(pContiguousMemory);

    for (int y = 0; y < pHeight; y++)
      for (int x = 0; x < pWidth; x++)
      {
        rnd = ((rnd % 257)) + 1 + (rnd << 7);
        final char lValue = (char) (((rnd & 0xFFFF) * pIntensity
                                     * x
                                     * sValue)
                                    / pWidth); // Math.random()
        lContiguousBuffer.writeChar(lValue);
      }
  }

  @Test
  public void demo() throws InvocationTargetException,
                     InterruptedException
  {
    final Stack2DDisplay lVideoDisplayDevice =
                                             new Stack2DDisplay(512,
                                                                512);

    lVideoDisplayDevice.getManualMinMaxIntensityOnVariable()
                       .set(true);
    lVideoDisplayDevice.open();
    lVideoDisplayDevice.setVisible(true);

    final int lSizeX = 256;
    final int lSizeY = lSizeX;
    final int lSizeZ = 16;

    @SuppressWarnings("unchecked")
    final OffHeapPlanarStack lStack =
                                    OffHeapPlanarStack.createStack(lSizeX,
                                                                   lSizeY,
                                                                   lSizeZ);

    System.out.println(lStack.getSizeInBytes());

    final Variable<StackInterface> lStackVariable =
                                                  lVideoDisplayDevice.getInputStackVariable();

    final Runnable lRunnable = () -> {
      while (true)
      {
        if (sDisplay)
        {
          for (int i = 0; i < lStack.getDepth(); i++)
            generateNoiseBuffer(0.5 + (1.0 + i) / (2 * lSizeZ),
                                lStack.getContiguousMemory(i),
                                lSizeX,
                                lSizeY);

          lStackVariable.set(lStack);
          // System.out.println(lStack);
        }
        ThreadSleep.sleep(10, TimeUnit.MILLISECONDS);
      }
    };

    final Thread lThread = new Thread(lRunnable);
    lThread.setName("DEMO");
    lThread.setDaemon(true);
    lThread.start();

    final JFrame lJFrame = runDemo(lVideoDisplayDevice);

    while (lVideoDisplayDevice.getDisplayOnVariable().get()
           && lJFrame.isVisible())
    {
      Thread.sleep(100);
    }

    lVideoDisplayDevice.close();
  }

  public JFrame runDemo(Stack2DDisplay pVideoDisplayDevice) throws InterruptedException,
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

          final JSliderDouble lJSliderDouble =
                                             new JSliderDouble("gray value");
          mcontentPane.add(lJSliderDouble, BorderLayout.SOUTH);

          final JButtonBoolean lJButtonBoolean =
                                               new JButtonBoolean(false,
                                                                  "Display",
                                                                  "No Display");
          mcontentPane.add(lJButtonBoolean, BorderLayout.NORTH);

          final Variable<Boolean> lStartStopVariable =
                                                     lJButtonBoolean.getBooleanVariable();
          lStartStopVariable.set(true);

          lStartStopVariable.addSetListener((o, n) -> {
            if (!n.equals(o))
              sDisplay = n;
          });

          final Variable<Double> lDoubleVariable =
                                                 lJSliderDouble.getDoubleVariable();

          lDoubleVariable.sendUpdatesTo(new Variable<Double>("SliderDoubleEventHook",
                                                             0.0)
          {

            @Override
            public Double setEventHook(final Double pOldValue,
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
