package rtlib.ao.gui.demo;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

import rtlib.ao.gui.MatrixControlPanel;
import rtlib.ao.zernike.TransformMatrices;

public class MatrixControlPanelDemo
{

	@Test
	public void demo() throws InvocationTargetException,
										InterruptedException
	{

		final int lMatrixWidth = 11;

		final DenseMatrix64F lTransformMatrix = TransformMatrices.computeZernickeTransformMatrix(lMatrixWidth);
		final DenseMatrix64F lTransformMatrixForDisplay = TransformMatrices.computeZernickeTransformMatrix(lMatrixWidth * 4);

		final MatrixControlPanel lMatrixControlPanel = new MatrixControlPanel(lMatrixWidth,
																																					lMatrixWidth,
																																					lTransformMatrix,
																																					lTransformMatrixForDisplay);

		final JFrame lTestFrame = new JFrame("Demo");
		SwingUtilities.invokeAndWait(new Runnable()
		{

			@Override
			public void run()
			{
				lTestFrame.setSize(768, 768);
				lTestFrame.setLayout(new MigLayout("insets 0", "[]", "[]"));
				lTestFrame.add(lMatrixControlPanel, "cell 0 0 ");
				lTestFrame.validate();
				lTestFrame.setVisible(true);
			}
		});

		final DenseMatrix64F lInputVector = new DenseMatrix64F(	lMatrixWidth * lMatrixWidth,
																														1);
		final DenseMatrix64F lShapeVector = new DenseMatrix64F(	lMatrixWidth * lMatrixWidth,
																														1);

		/*for (int i = 0; i < 10000; i++)
		{
			final double lValue = cos(0.1 * i);
			lInputVector.set(lMatrixWidth + 1, lValue);

			lMatrixControlPanel.getInputModeVectorVariable()
													.setReference(lInputVector);
			Thread.sleep(100);
		}/**/

		while (lTestFrame.isVisible())
			Thread.sleep(10);
	}

}
