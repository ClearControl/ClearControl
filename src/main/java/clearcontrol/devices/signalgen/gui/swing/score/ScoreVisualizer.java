package clearcontrol.devices.signalgen.gui.swing.score;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.tan;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableListener;
import clearcontrol.devices.signalgen.measure.MeasureInterface;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.StaveInterface;
import clearcontrol.devices.signalgen.staves.ZeroStave;

public class ScoreVisualizer extends JPanel
                             implements MouseMotionListener
{

  private static final long serialVersionUID = 1L;

  private final Variable<ScoreInterface> mScoreVariable;

  private final Variable<Double> mScalingVariable;

  @SuppressWarnings("unchecked")
  public ScoreVisualizer()
  {
    super();

    final VariableListener<?> lVariableListener =
                                                new VariableListener<Object>()
                                                {

                                                  @Override
                                                  public void setEvent(Object pCurrentValue,
                                                                       Object pNewValue)
                                                  {
                                                    SwingUtilities.invokeLater(() -> {
                                                      repaint();
                                                    });
                                                  }

                                                  @Override
                                                  public void getEvent(Object pCurrentValue)
                                                  {
                                                    // TODO Auto-generated
                                                    // method stub

                                                  }
                                                };

    mScalingVariable = new Variable<Double>("ScalingVariable", 1.0);
    mScalingVariable.addListener((VariableListener<Double>) lVariableListener);

    mScoreVariable = new Variable<ScoreInterface>("ScoreVariable");
    mScoreVariable.addListener((VariableListener<ScoreInterface>) lVariableListener);

    addMouseMotionListener(this);

  }

  public Variable<Double> getScalingVariable()
  {
    return mScalingVariable;
  }

  public Variable<ScoreInterface> getScoreVariable()
  {
    return mScoreVariable;
  }

  @Override
  public void paint(Graphics g)
  {
    final Graphics2D lGraphics2D = (Graphics2D) g;

    final int lWidth = getWidth();
    final int lHeight = getHeight();

    lGraphics2D.setColor(Color.black);
    lGraphics2D.fillRect(0, 0, lWidth, lHeight);

    final ScoreInterface lScore = getScoreVariable().get();

    if (lScore == null)
      return;

    // System.out.println(lScore.getTotalNumberOfTimePoints());
    if (lScore.getNumberOfMeasures() == 0
        || lScore.getDuration(TimeUnit.NANOSECONDS) == 0)
      return;

    final float lScaling = mScalingVariable.get().floatValue();
    final int lNumberOfMeasures = lScore.getNumberOfMeasures();
    final int lMaxNumberOfStaves = lScore.getMaxNumberOfStaves();
    final double lPixelsPerStave = ((double) lHeight)
                                   / lMaxNumberOfStaves;
    final long lTotalDuration =
                              lScore.getDuration(TimeUnit.NANOSECONDS);
    // System.out.println("lMaxNumberOfStaves=" + lMaxNumberOfStaves);
    // System.out.println("lPixelsPerTimePoint=" + lPixelsPerTimePoint);
    // System.out.println("lPixelsPerStave=" + lPixelsPerStave);

    int lLastX = 0, lLastY = 0;

    double lMeasurePixelOffset = 0;
    for (int m = 0; m < lNumberOfMeasures; m++)
    {
      final MeasureInterface lMeasure = lScore.getMeasure(m);
      final double lMeasureWidthInPixels = (((lWidth)
                                              * lMeasure.getDuration(TimeUnit.NANOSECONDS))
                                             / lTotalDuration);

      for (int s = 0; s < lMeasure.getNumberOfStaves(); s++)
      {
        final StaveInterface lStave = lMeasure.getStave(s);

        if (!(lStave instanceof ZeroStave))
        {
          lLastX = round(lMeasurePixelOffset);
          lLastY = round(lPixelsPerStave * s);
          for (int i = 0; i < lMeasureWidthInPixels; i++)
          {
            final float lNormalizedTime =
                                        (float) ((i)
                                                 / lMeasureWidthInPixels);
            final float lFloatValue =
                                    lStave.getValue(lNormalizedTime);

            final float lBrightness =
                                    absclampplus(lScaling
                                                 * lFloatValue, 0.2f);
            final float lHue = 0.25f + (lFloatValue > 0f ? 0.5f : 0f);

            final float red =
                            lBrightness * (lFloatValue <= 0f ? 1 : 0);
            final float green = lBrightness * 0.1f;
            final float blue = lBrightness
                               * (lFloatValue >= 0f ? 1 : 0);

            lGraphics2D.setColor(Color.getHSBColor(lHue,
                                                   0.5f,
                                                   lBrightness));/**/
            lGraphics2D.fillRect(round(lMeasurePixelOffset + i),
                                 round(lPixelsPerStave * s),
                                 roundmin1(1),
                                 roundmin1(lPixelsPerStave));/**/

            final int lNewX = round(lMeasurePixelOffset + i);
            final int lNewY = round(
                                    lPixelsPerStave
                                    * (s + 1) - (clamp(
                                                       (1 + lScaling
                                                            * lFloatValue)
                                                       * 0.5f)
                                                 * lPixelsPerStave));

            lGraphics2D.setColor(Color.white);
            lGraphics2D.drawLine(lLastX, lLastY, lNewX, lNewY);

            lLastX = lNewX;
            lLastY = lNewY;

          }
          lGraphics2D.setColor(Color.white);
          lGraphics2D.drawString(lStave.getName(),
                                 round(lMeasurePixelOffset + 2),
                                 12 + round(lPixelsPerStave * (s)));
        }

        lGraphics2D.setColor(Color.gray.darker());
        lGraphics2D.fillRect(round(lMeasurePixelOffset),
                             round(lPixelsPerStave * s),
                             round(lMeasureWidthInPixels),
                             1);

      }

      lGraphics2D.setColor(Color.white);
      lGraphics2D.drawLine(round(lMeasurePixelOffset),
                           0,
                           round(lMeasurePixelOffset),
                           lHeight);

      lMeasurePixelOffset += lMeasureWidthInPixels;
    }

  }

  private float absclampplus(float pX, float offset)
  {
    return min(1, max(0, abs(pX) + offset));
  }

  private float clamp(float pX)
  {
    return min(1, max(0, pX));
  }

  private static final int round(double pX)
  {
    return (int) Math.round(pX);
  }

  private static final int roundmin1(double pX)
  {
    return (int) max(1, Math.round(pX));
  }

  @Override
  public void mouseDragged(MouseEvent pE)
  {
    final float lX = pE.getX();
    final float lWidth = getWidth();
    final float lNormalizedX = lX / lWidth;
    final double lScale = tan(0.5 * PI * lNormalizedX);
    getScalingVariable().set(lScale);
    // System.out.println(lScale);
  }

  @Override
  public void mouseMoved(MouseEvent pE)
  {
    // TODO Auto-generated method stub

  }

}
