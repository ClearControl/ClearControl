package clearcontrol.devices.slm.gui.swing.demo;

import static java.lang.Math.cos;

import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import clearcontrol.devices.slm.gui.swing.MatrixPanel;
import clearcontrol.devices.slm.zernike.TransformMatrices;
import clearcontrol.gui.swing.JSliderDouble;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.junit.Test;

public class MatrixPanelDemo
{

  @Test
  public void test() throws InvocationTargetException,
                     InterruptedException
  {

    final MatrixPanel lMatrixPanel = new MatrixPanel(32, 8, 8);

    final JSliderDouble lMinRangeSlider =
                                        new JSliderDouble("Min range",
                                                          -1,
                                                          1,
                                                          -1);

    lMinRangeSlider.removeLabelAndTextField();
    final JSliderDouble lMaxRangeSlider =
                                        new JSliderDouble("Max range",
                                                          -1,
                                                          1,
                                                          1);

    lMaxRangeSlider.removeLabelAndTextField();

    lMatrixPanel.getMinRangeVariable()
                .syncWith(lMinRangeSlider.getDoubleVariable());
    lMatrixPanel.getMaxRangeVariable()
                .syncWith(lMaxRangeSlider.getDoubleVariable());

    SwingUtilities.invokeAndWait(new Runnable()
    {
      @Override
      public void run()
      {
        final JFrame lTestFrame = new JFrame("Test");
        lTestFrame.setSize(256, 400);
        lTestFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
        lTestFrame.add(lMatrixPanel);
        lTestFrame.add(lMaxRangeSlider);
        lTestFrame.add(lMinRangeSlider);

        lTestFrame.validate();
        lTestFrame.setVisible(true);
      }
    });

    final DenseMatrix64F lInputVector = new DenseMatrix64F(64, 1);
    final DenseMatrix64F lZernickeTransformMatrix =
                                                  TransformMatrices.computeCosineTransformMatrix(8);
    final DenseMatrix64F lShapeVector = new DenseMatrix64F(64, 1);

    for (int i = 0; i < 10000; i++)
    {
      final double lValue = cos(0.1 * i);
      lInputVector.set(9, lValue);

      CommonOps.mult(lZernickeTransformMatrix,
                     lInputVector,
                     lShapeVector);/**/

      lMatrixPanel.getMatrixVariable().set(lShapeVector);
      Thread.sleep(100);
    }

    Thread.sleep(1000000);
  }

}
