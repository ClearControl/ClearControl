package clearcontrol.gui.swing.demo;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import clearcontrol.core.variable.VariableListener;
import clearcontrol.gui.swing.JTextFieldDouble;

import org.junit.Test;

public class TextFieldDoubleDemo
{

  @Test
  public void demo() throws InvocationTargetException,
                     InterruptedException
  {

    final JFrame lJFrame = runDemo();

    while (lJFrame.isVisible())
    {
      Thread.sleep(100);
    }

  }

  public JFrame runDemo() throws InterruptedException,
                          InvocationTargetException
  {

    final JFrame lJFrame = new JFrame("Demo");

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

          final JTextFieldDouble lJTextFieldDouble =
                                                   new JTextFieldDouble();

          lJTextFieldDouble.getDoubleVariable()
                           .addListener(new VariableListener<Double>()
                           {

                             @Override
                             public void setEvent(Double pCurrentValue,
                                                  Double pNewValue)
                             {
                               System.out.format("setEvent(%s,%s) \n",
                                                 pCurrentValue,
                                                 pNewValue);

                             }

                             @Override
                             public void getEvent(Double pCurrentValue)
                             {
                               System.out.format("getEvent(%s,%s) \n",
                                                 pCurrentValue);
                             }
                           });

          lJFrame.add(lJTextFieldDouble);

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
