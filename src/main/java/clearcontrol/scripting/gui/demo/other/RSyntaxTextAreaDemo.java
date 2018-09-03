package clearcontrol.scripting.gui.demo.other;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import clearcontrol.core.concurrent.thread.ThreadSleep;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.junit.Test;

public class RSyntaxTextAreaDemo
{

  public class TextEditorDemo extends JFrame
  {

    public TextEditorDemo()
    {

      final JPanel cp = new JPanel(new BorderLayout());

      final RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
      textArea.setCodeFoldingEnabled(true);
      final RTextScrollPane sp = new RTextScrollPane(textArea);
      cp.add(sp);

      setContentPane(cp);
      setTitle("Text Editor Demo");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      pack();
      setLocationRelativeTo(null);

    }

  }

  @Test
  public void test() throws InvocationTargetException,
                     InterruptedException
  {
    final TextEditorDemo lTextEditorDemo = new TextEditorDemo();

    SwingUtilities.invokeAndWait(new Runnable()
    {
      @Override
      public void run()
      {
        lTextEditorDemo.setVisible(true);
      }
    });

    while (lTextEditorDemo.isVisible())
    {
      ThreadSleep.sleep(10, TimeUnit.MILLISECONDS);
    }
  }

}
