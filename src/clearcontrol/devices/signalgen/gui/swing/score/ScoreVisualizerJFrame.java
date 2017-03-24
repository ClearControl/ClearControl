package clearcontrol.devices.signalgen.gui.swing.score;

import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.signalgen.score.ScoreInterface;

public class ScoreVisualizerJFrame extends JFrame
{

  private static final long serialVersionUID = 1L;
  private final ScoreVisualizer mScoreVisualizer;

  public ScoreVisualizerJFrame(String pTitle) throws HeadlessException
  {
    super(pTitle);
    setSize(768, 768);
    setLayout(new MigLayout("insets 0",
                            "[grow,fill]",
                            "[grow,fill]"));
    mScoreVisualizer = new ScoreVisualizer();
    add(mScoreVisualizer, "cell 0 0 ");
    validate();
  }

  public Variable<ScoreInterface> getScoreVariable()
  {
    return mScoreVisualizer.getScoreVariable();
  }

  public static ScoreVisualizerJFrame visualizeAndWait(String pString,
                                                       ScoreInterface pScore)
  {
    final ScoreVisualizerJFrame lVisualize =
                                           visualize(pString, pScore);

    while (lVisualize.isVisible())
    {
      try
      {
        Thread.sleep(100);
      }
      catch (final InterruptedException e)
      {
      }
    }

    return lVisualize;
  }

  public static ScoreVisualizerJFrame visualize(String pWindowTitle,
                                                ScoreInterface pScoreInterface)
  {
    final ScoreVisualizerJFrame lScoreVisualizerJFrame =
                                                       new ScoreVisualizerJFrame(pWindowTitle);
    try
    {
      SwingUtilities.invokeAndWait(() -> {

        lScoreVisualizerJFrame.getScoreVariable()
                              .set(pScoreInterface);
        lScoreVisualizerJFrame.setVisible(true);
      });
    }
    catch (final InvocationTargetException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (final InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return lScoreVisualizerJFrame;
  }

}
