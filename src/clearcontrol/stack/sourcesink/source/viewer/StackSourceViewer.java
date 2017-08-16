package clearcontrol.stack.sourcesink.source.viewer;

import static java.lang.Math.max;
import static java.lang.Math.min;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.video.video3d.Stack3DDisplay;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;
import clearcontrol.stack.sourcesink.source.StackSourceInterface;

/**
 * Stack source viewer
 *
 * @author royer
 */
public class StackSourceViewer implements AutoCloseable
{
  private Variable<StackSourceInterface> mStackSourceVariable =
                                                              new Variable<>("StackSource",
                                                                             null);
  private Variable<String> mStackChannelVariable =
                                                 new Variable<>("StackChannel",
                                                                StackSinkSourceInterface.cDefaultChannel);
  private Variable<Long> mStackIndexVariable =
                                             new Variable<>("StackIndex",
                                                            0L);

  private Variable<StackInterface> mFrameReferenceVariable;

  private Stack3DDisplay mVideoFrame3DDisplay;

  /**
   * Instantiates a stack source
   * 
   */
  public StackSourceViewer()
  {
    super();
    mVideoFrame3DDisplay = new Stack3DDisplay("Test");

    mFrameReferenceVariable =
                            mVideoFrame3DDisplay.getInputStackVariable();

    mVideoFrame3DDisplay.open();
    mVideoFrame3DDisplay.setVisible(true);

    mStackSourceVariable.addSetListener((o, n) -> {

      if (n != o)
      {
        mStackChannelVariable.set(StackSinkSourceInterface.cDefaultChannel);
        mStackIndexVariable.set(0L);
      }

    });

    mStackChannelVariable.addSetListener((o, n) -> {

      if (n != o)
      {
        update();
      }

    });

    mStackIndexVariable.addSetListener((o, n) -> {

      if (n != o)
      {
        update();
      }

    });

    mStackIndexVariable.setCurrent();

  }

  private void update()
  {
    StackSourceInterface lStackSource = mStackSourceVariable.get();

    if (lStackSource == null)
      return;

    String lChannel = mStackChannelVariable.get();

    if (!lStackSource.getChannelList().contains(lChannel))
      return;

    Long lIndex = mStackIndexVariable.get();

    long lMaxNumberOfStacks =
                            lStackSource.getNumberOfStacks(lChannel);

    // clamping:
    long lStackIndex = min(max(lIndex, 0), lMaxNumberOfStacks - 1);

    if (lStackIndex < 0)
      return;

    StackInterface lStack = lStackSource.getStack(lChannel,
                                                  lStackIndex);

    mFrameReferenceVariable.set(lStack);

  }

  @Override
  public void close()
  {

    if (mVideoFrame3DDisplay != null)
    {
      mVideoFrame3DDisplay.setVisible(false);
      mVideoFrame3DDisplay.close();
    }
  }

  /**
   * Returns the variable holding the stack
   * 
   * @return stack source variable
   */
  public Variable<StackSourceInterface> getStackSourceVariable()
  {
    return mStackSourceVariable;
  }

  /**
   * Returns stack channel variable
   * 
   * @return stack channel variable
   */
  public Variable<String> getStackChannelVariable()
  {
    return mStackChannelVariable;
  }

  /**
   * Returns stack index variable
   * 
   * @return stack index variable
   */
  public Variable<Long> getStackIndexVariable()
  {
    return mStackIndexVariable;
  }

}
