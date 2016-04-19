package rtlib.hardware.slm.gui;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableSetListener;

public class MatrixPanel extends JPanel
{

	private final int mDisplayTileSize;
	private final int mMatrixWidth, mMatrixHeight;
	private volatile boolean mAutoNormalize, mSymetricRange;
	Variable<DenseMatrix64F> mMatrixVariable;
	Variable<Double> mMinRangeVariable, mMaxRangeVariable;
	private float mSaturation = 0.5f, mBrightness = 0.95f;

	public static MatrixPanel getMatrixForMatrixEntry(int pDisplayTileSize,
																										int pMatrixWidth,
																										int pMatrixHeight,
																										DenseMatrix64F pTransformMatrix,
																										int x,
																										int y)
	{
		final MatrixPanel lMatrixPanel = new MatrixPanel(	pDisplayTileSize,
																											pMatrixWidth,
																											pMatrixHeight);

		final DenseMatrix64F lInputVector = new DenseMatrix64F(	pTransformMatrix.numRows,
																														1);

		final DenseMatrix64F lShapeVector = new DenseMatrix64F(	pTransformMatrix.numRows,
																														1);

		lInputVector.set(x + pMatrixWidth * y, 1);

		CommonOps.mult(pTransformMatrix, lInputVector, lShapeVector);

		lMatrixPanel.getMatrixVariable().set(lShapeVector);

		return lMatrixPanel;
	}

	public MatrixPanel()
	{
		this(32, 8, 8);

	}

	public MatrixPanel(int pMatrixWidth, int pMatrixHeight)
	{
		this(32, pMatrixWidth, pMatrixHeight);
	}

	public MatrixPanel(	int pDisplayTileSize,
											int pMatrixWidth,
											int pMatrixHeight)
	{
		super(true);
		mDisplayTileSize = pDisplayTileSize;
		mMatrixWidth = pMatrixWidth;
		mMatrixHeight = pMatrixHeight;

		mMatrixVariable = new Variable<DenseMatrix64F>("MatrixVariable")
		{

			@Override
			public DenseMatrix64F setEventHook(	DenseMatrix64F pOldValue,
																					DenseMatrix64F pNewValue)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						MatrixPanel.this.repaint();
					}
				});

				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mMinRangeVariable = new Variable<Double>("MinRange", -1.0);
		mMaxRangeVariable = new Variable<Double>("MaxRange", 1.0);

		final VariableSetListener<Double> lVariableListener = new VariableSetListener<Double>()
		{
			@Override
			public void setEvent(Double pCurrentValue, Double pNewValue)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						MatrixPanel.this.repaint();
					}
				});
			}
		};
		mMinRangeVariable.addSetListener(lVariableListener);
		mMaxRangeVariable.addSetListener(lVariableListener);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}/**/

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(	mMatrixWidth * mDisplayTileSize,
													mMatrixHeight * mDisplayTileSize);
	}

	@Override
	public void paint(Graphics g)
	{
		final Graphics2D lGraphics2D = (Graphics2D) g;

		final int lWidth = getWidth();
		final int lHeight = getHeight();

		final int lTileWidth = (int) round(1.0 * lWidth / mMatrixWidth);
		final int lTileHeight = (int) round(1.0 * lHeight / mMatrixHeight);

		final DenseMatrix64F lDenseMatrix64F = mMatrixVariable.get();
		if (lDenseMatrix64F == null)
			return;

		final double lMax = mAutoNormalize ? CommonOps.elementMax(lDenseMatrix64F)
																			: mMaxRangeVariable.get();
		final double lMin = mAutoNormalize ? CommonOps.elementMin(lDenseMatrix64F)
																			: (mSymetricRange	? -lMax
																												: mMinRangeVariable.get());

		for (int y = 0; y < mMatrixHeight; y++)
			for (int x = 0; x < mMatrixWidth; x++)
			{
				final int lTileX = x * lTileWidth;
				final int lTileY = y * lTileHeight;

				final double lValue = lDenseMatrix64F.get(y * mMatrixWidth
																									+ x);
				final Color lColor = getRBColorForValue(lMin, lMax, lValue);

				lGraphics2D.setColor(lColor);
				lGraphics2D.fillRect(lTileX, lTileY, lTileWidth, lTileHeight);
			}

	}

	private Color getBWColorForValue(	double lMin,
																		double lMax,
																		double pValue)
	{
		final float lNormalizedValue = (float) ((pValue - lMin) / (lMax - lMin));
		return new Color(	clamp(lNormalizedValue, 0, 1),
											clamp(lNormalizedValue, 0, 1),
											clamp(lNormalizedValue, 0, 1));
	}

	private Color getRBColorForValue(	double lMin,
																		double lMax,
																		double pValue)
	{

		final float u = (float) ((pValue - lMin) / (lMax - lMin));

		return Color.getHSBColor(u * (4.f / 6), mSaturation, mBrightness);
	}

	private float clamp(float pValue, int pMin, int pMax)
	{
		return max(min(pValue, pMax), pMin);
	}

	public Variable<DenseMatrix64F> getMatrixVariable()
	{
		return mMatrixVariable;
	}

	public Variable<Double> getMinRangeVariable()
	{
		return mMinRangeVariable;
	}

	public Variable<Double> getMaxRangeVariable()
	{
		return mMaxRangeVariable;
	}

	public boolean isAutoNormalize()
	{
		return mAutoNormalize;
	}

	public void setAutoNormalize(boolean pAutoNormalize)
	{
		mAutoNormalize = pAutoNormalize;
	}

	public boolean isSymetricRange()
	{
		return mSymetricRange;
	}

	public void setSymetricRange(boolean pSymetricRange)
	{
		mSymetricRange = pSymetricRange;
	}

	public float getSaturation()
	{
		return mSaturation;
	}

	public void setSaturation(float pSaturation)
	{
		mSaturation = pSaturation;
	}

	public float getBrightness()
	{
		return mBrightness;
	}

	public void setBrightness(float pBrightness)
	{
		mBrightness = pBrightness;
	}

}
