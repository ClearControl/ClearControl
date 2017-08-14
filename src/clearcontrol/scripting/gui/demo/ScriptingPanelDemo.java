package clearcontrol.scripting.gui.demo;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.gui.ScriptingPanel;
import clearcontrol.scripting.lang.groovy.GroovyScripting;
import clearcontrol.scripting.lang.jython.JythonScripting;

import org.junit.Test;

public class ScriptingPanelDemo
{

  @Test
  public void testPython() throws InvocationTargetException,
                           InterruptedException
  {
    final JFrame lJFrame = new JFrame();

    final JythonScripting lJythonScripting = new JythonScripting();

    final ScriptingEngine lScriptingEngine =
                                           new ScriptingEngine(lJythonScripting,
                                                               null);

    final ScriptingPanel lScriptingPanel =
                                         new ScriptingPanel("testPython",
                                                            lScriptingEngine);
    lScriptingPanel.loadLastLoadedScriptFile();
    lJFrame.add(lScriptingPanel);

    SwingUtilities.invokeAndWait(new Runnable()
    {
      @Override
      public void run()
      {
        lJFrame.setSize(512, 512);
        lJFrame.setVisible(true);
      }
    });

    while (lJFrame.isVisible())
    {
      ThreadSleep.sleep(10, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void testGroovy() throws InvocationTargetException,
                           InterruptedException
  {
    final JFrame lJFrame = new JFrame();

    final GroovyScripting lGroovyScripting = new GroovyScripting();

    final ScriptingEngine lScriptingEngine =
                                           new ScriptingEngine(lGroovyScripting,
                                                               null);

    final ScriptingPanel lScriptingPanel =
                                         new ScriptingPanel("testGroovy",
                                                            lScriptingEngine);
    lScriptingPanel.loadLastLoadedScriptFile();
    lJFrame.add(lScriptingPanel);

    SwingUtilities.invokeAndWait(new Runnable()
    {
      @Override
      public void run()
      {
        lJFrame.setSize(512, 512);
        lJFrame.setVisible(true);
      }
    });

    while (lJFrame.isVisible())
    {
      ThreadSleep.sleep(10, TimeUnit.MILLISECONDS);
    }
  }

}
