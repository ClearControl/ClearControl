package rtlib.symphony.gui;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import rtlib.core.variable.VariableListener;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.symphony.movement.MovementInterface;
import rtlib.symphony.score.ScoreInterface;
import rtlib.symphony.staves.StaveInterface;

public class ScoreVisualizer extends JPanel
{

	private static final long serialVersionUID = 1L;

	private final ObjectVariable<ScoreInterface> mScoreVariable;

	private final DoubleVariable mScalingVariable;

	public ScoreVisualizer()
	{
		super();

		mScalingVariable = new DoubleVariable("ScalingVariable", 1);

		mScoreVariable = new ObjectVariable<ScoreInterface>("ScoreVariable");

		mScoreVariable.addListener(new VariableListener<ScoreInterface>()
		{

			@Override
			public void setEvent(	ScoreInterface pCurrentValue,
														ScoreInterface pNewValue)
			{
				SwingUtilities.invokeLater(() -> {
					repaint();
				});
			}

			@Override
			public void getEvent(ScoreInterface pCurrentValue)
			{
				// TODO Auto-generated method stub

			}
		});

	}

	public DoubleVariable getScalingVariable()
	{
		return mScalingVariable;
	}

	public ObjectVariable<ScoreInterface> getScoreVariable()
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
		if (lScore.getTotalNumberOfTimePoints() == 0)
			return;

		final float lScaling = (float) mScalingVariable.getValue();
		final int lNumberOfMovements = lScore.getNumberOfMovements();
		final long lTotalNumberOfTimePoints = lScore.getTotalNumberOfTimePoints();
		final int lMaxNumberOfStaves = lScore.getMaxNumberOfStaves();
		final double lPixelsPerTimePoint = ((double) lWidth) / lTotalNumberOfTimePoints;
		final double lPixelsPerStave = ((double) lHeight) / lMaxNumberOfStaves;

		System.out.println("lMaxNumberOfStaves=" + lMaxNumberOfStaves);
		// System.out.println("lPixelsPerTimePoint=" + lPixelsPerTimePoint);
		// System.out.println("lPixelsPerStave=" + lPixelsPerStave);

		double lMovementPixelOffset = 0;
		for (int m = 0; m < lNumberOfMovements; m++)
		{
			final MovementInterface lMovement = lScore.getMovement(m);

			for (int s = 0; s < lMovement.getNumberOfStaves(); s++)
			{
				final StaveInterface lStave = lMovement.getStave(s);
				final short[] lStaveArray = lStave.getStaveArray();

				for (int i = 0; i < lStaveArray.length; i++)
				{
					final short lValue = lStaveArray[i];
					// System.out.println(lValue);
					final float lFloatValue = (((float) lValue) / (Short.MAX_VALUE));
					// System.out.println(lFloatValue);
					final float lBrightness = absclamp(lScaling * lFloatValue);
					// final float lHue = 0.25f + (lFloatValue > 0f ? 0.5f : 0f);

					final float red = lBrightness * (lFloatValue <= 0f ? 1 : 0);
					final float green = lBrightness * 0.1f;
					final float blue = lBrightness * (lFloatValue >= 0f ? 1 : 0);

					lGraphics2D.setColor(new Color(red, green, blue, 1));
					/*lGraphics2D.setColor(Color.getHSBColor(	lHue,
																									0.5f,
																									lBrightness));/**/
					lGraphics2D.fillRect(	round(lMovementPixelOffset + lPixelsPerTimePoint
																			* i),
																round(lPixelsPerStave * s),
																roundmin1(lPixelsPerTimePoint),
																roundmin1(lPixelsPerStave));
				}

			}

			lMovementPixelOffset += lMovement.getNumberOfTimePoints() * lPixelsPerTimePoint;
		}

	}

	private float absclamp(float pX)
	{
		return min(1, max(0, abs(pX)));
	}

	private static final int round(double pX)
	{
		return (int) Math.round(pX);
	}

	private static final int roundmin1(double pX)
	{
		return (int) max(1, Math.round(pX));
	}

}
