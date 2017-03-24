package clearcontrol.devices.slm.gui.swing;

import static java.lang.Math.sqrt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.gui.swing.JSliderDouble;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public class MatrixControlPanel extends JPanel
{

  private static final long serialVersionUID = 1L;

  JPanel mModesMatrixPanel;
  MatrixPanel[][] mMatrixPanels;

  private final int mMatrixWidth, mMatrixHeight;

  private volatile int mCurrentX, mCurrentY;

  Variable<DenseMatrix64F> mInputModeVectorVariable,
      mOutputMatrixVariable;

  private final DenseMatrix64F mTransformMatrix;
  private final DenseMatrix64F mTransformMatrixForDisplay;

  private final DenseMatrix64F mModeVector;

  private JSliderDouble mSymetricRangeSlider;
  private JButton mZeroAllModesButton;
  private JButton mZeroCurrentModeButton;

  public MatrixControlPanel(int pMatrixWidth,
                            int pMatrixHeight,
                            DenseMatrix64F pTransformMatrix,
                            DenseMatrix64F pTransformMatrixForDisplay)
  {
    mMatrixWidth = pMatrixWidth;
    mMatrixHeight = pMatrixHeight;
    mTransformMatrix = pTransformMatrix;
    mTransformMatrixForDisplay = pTransformMatrixForDisplay;

    mModeVector = new DenseMatrix64F(mTransformMatrix.numRows, 1);

    mInputModeVectorVariable =
                             new Variable<DenseMatrix64F>("InputMatrix",
                                                          new DenseMatrix64F(mMatrixWidth
                                                                             * mMatrixHeight,
                                                                             1))
                             {
                               @Override
                               public DenseMatrix64F setEventHook(DenseMatrix64F pOldValue,
                                                                  DenseMatrix64F pNewValue)
                               {

                                 mModeVector.set(pNewValue);
                                 updateOutputMatrix();

                                 for (int y =
                                            0; y < pMatrixHeight; y++)
                                   for (int x =
                                              0; x < pMatrixWidth; x++)
                                   {
                                     final double lValue =
                                                         pNewValue.get(x
                                                                       * mMatrixWidth
                                                                       + y,
                                                                       0);

                                     setNewValueForModeMatrix(x,
                                                              y,
                                                              lValue,
                                                              .5f);
                                   }

                                 return super.setEventHook(pOldValue,
                                                           pNewValue);
                               }
                             };

    mOutputMatrixVariable =
                          new Variable<DenseMatrix64F>("OutputMatrix",
                                                       new DenseMatrix64F(mMatrixWidth
                                                                          * mMatrixHeight,
                                                                          1));

    final JSliderDouble lModeSlider = new JSliderDouble("Mode",
                                                        -1,
                                                        1,
                                                        0);
    lModeSlider.removeLabelAndTextField();
    lModeSlider.getDoubleVariable()
               .addSetListener(new VariableSetListener<Double>()
               {

                 @Override
                 public void setEvent(Double pCurrentValue,
                                      Double pNewValue)
                 {

                   mModeVector.set(mCurrentX
                                   + pMatrixWidth * mCurrentY,
                                   pNewValue);

                   updateOutputMatrix();

                   setNewValueForModeMatrix(mCurrentX,
                                            mCurrentY,
                                            pNewValue,
                                            0.5f);

                 }

               });

    mSymetricRangeSlider = new JSliderDouble("MaxRange", 0, 1, 1);
    mSymetricRangeSlider.removeLabelAndTextField();

    mModesMatrixPanel = new JPanel();

    String lColumnString = "[128px]";
    String lRowString = "[128px]";

    for (int i = 0; i < pMatrixWidth; i++)
      lColumnString += lColumnString;

    for (int i = 0; i < pMatrixHeight; i++)
      lRowString += lRowString;

    // mModesMatrixPanel.setBackground(Color.BLACK);
    mModesMatrixPanel.setLayout(new MigLayout("insets 0",
                                              "[grow]",
                                              "[grow]"));

    mMatrixPanels = new MatrixPanel[pMatrixWidth][pMatrixHeight];

    for (int y = 0; y < pMatrixHeight; y++)
      for (int x = 0; x < pMatrixWidth; x++)
      {
        final MatrixPanel lMatrixPanel =
                                       MatrixPanel.getMatrixForMatrixEntry(1,
                                                                           (int) sqrt(mTransformMatrixForDisplay.numCols),
                                                                           (int) sqrt(mTransformMatrixForDisplay.numRows),
                                                                           mTransformMatrixForDisplay,
                                                                           x,
                                                                           y);
        lMatrixPanel.setSaturation(0.2f);
        lMatrixPanel.setSymetricRange(true);

        mSymetricRangeSlider.getDoubleVariable()
                            .syncWith(lMatrixPanel.getMaxRangeVariable());

        final int fx = x;
        final int fy = y;
        lMatrixPanel.addMouseListener(new MouseAdapter()
        {
          @Override
          public void mouseClicked(MouseEvent pE)
          {
            mCurrentX = fx;
            mCurrentY = fy;

            final double lCurrentValue = mModeVector.get(
                                                         mCurrentY
                                                         * mMatrixWidth
                                                         + mCurrentX,
                                                         0);
            lModeSlider.getDoubleVariable().set(lCurrentValue);
          }
        });

        mMatrixPanels[x][y] = lMatrixPanel;
        mModesMatrixPanel.add(lMatrixPanel,
                              String.format("cell %d %d, grow",
                                            x,
                                            y));
      }

    final MatrixPanel lMatrixPanel = new MatrixPanel(32,
                                                     pMatrixWidth,
                                                     pMatrixHeight);
    lMatrixPanel.setSymetricRange(true);

    getOutputMatrixVariable().syncWith(lMatrixPanel.getMatrixVariable());

    getSymetricRangeVariable().syncWith(lMatrixPanel.getMaxRangeVariable());

    setLayout(new MigLayout("insets 0",
                            "[grow][grow]",
                            "[][grow][grow][grow]"));

    mZeroAllModesButton = new JButton("Zero all modes");
    mZeroAllModesButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent pE)
      {
        mModeVector.set(mCurrentX + pMatrixWidth * mCurrentY, 0);
        updateOutputMatrix();
        zeroModeMatrix();
      }
    });
    add(mZeroAllModesButton, "cell 0 0,growx,aligny center");

    mZeroCurrentModeButton = new JButton("Zero current mode");
    mZeroCurrentModeButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent pE)
      {
        mModeVector.zero();
        updateOutputMatrix();
        setNewValueForModeMatrix(mCurrentX, mCurrentY, 1, 0.2f);
      }
    });
    add(mZeroCurrentModeButton, "cell 1 0,growx,aligny center");

    add(lModeSlider, "cell 0 1 2 1,grow"); // , "cell 0 1");

    add(mModesMatrixPanel, "cell 0 2");
    add(lMatrixPanel, "cell 1 2");

    add(mSymetricRangeSlider, "cell 0 3 2 1,grow"); // , "cell 0 1");

    validate();

  }

  private void setNewValueForModeMatrix(int pX,
                                        int pY,
                                        double pNewValue,
                                        float pSaturation)
  {
    final DenseMatrix64F mTempVectorForDisplay =
                                               new DenseMatrix64F(mTransformMatrixForDisplay.numRows,
                                                                  1);

    mTempVectorForDisplay.zero();
    mTempVectorForDisplay.set((int) (pX
                                     + sqrt(mTransformMatrixForDisplay.numCols)
                                       * pY),
                              pNewValue);

    final DenseMatrix64F lShapeVectorForDisplay =
                                                new DenseMatrix64F(mTransformMatrixForDisplay.numRows,
                                                                   1);

    CommonOps.mult(mTransformMatrixForDisplay,
                   mTempVectorForDisplay,
                   lShapeVectorForDisplay);

    final MatrixPanel lMatrixPanel = mMatrixPanels[pX][pY];
    lMatrixPanel.setSaturation(pSaturation);

    lMatrixPanel.getMatrixVariable().set(lShapeVectorForDisplay);
  }

  private void zeroModeMatrix()
  {
    for (int y = 0; y < mMatrixHeight; y++)
      for (int x = 0; x < mMatrixWidth; x++)
      {
        setNewValueForModeMatrix(x, y, 1, 0.2f);
      }
  }

  private void updateOutputMatrix()
  {
    final DenseMatrix64F lOutputMatrix = mOutputMatrixVariable.get();
    CommonOps.mult(mTransformMatrix, mModeVector, lOutputMatrix);

    mOutputMatrixVariable.setCurrent();
  }

  public Variable<DenseMatrix64F> getOutputMatrixVariable()
  {
    return mOutputMatrixVariable;
  }

  public Variable<DenseMatrix64F> getInputModeVectorVariable()
  {
    return mInputModeVectorVariable;
  }

  public Variable<Double> getSymetricRangeVariable()
  {
    return mSymetricRangeSlider.getDoubleVariable();
  }

  /*@Override
  public Dimension getPreferredSize()
  {
  	return new Dimension(	mMatrixWidth * mMatrixWidth
  														* 2
  														+ mMatrixWidth,
  												mMatrixHeight * mMatrixHeight
  														* 2
  														+ mMatrixHeight);
  }/**/

  /*@Override
  public Dimension getMinimumSize()
  {
  	return getPreferredSize();
  }/**/

}
