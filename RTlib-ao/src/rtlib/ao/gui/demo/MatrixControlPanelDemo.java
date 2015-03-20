package rtlib.ao.gui.demo;

import static java.lang.Math.cos;

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
	public void test() throws InvocationTargetException,
										InterruptedException
	{

		final DenseMatrix64F lTransformMatrix = TransformMatrices.computeZernickeTransformMatrix(8);
		final DenseMatrix64F lTransformMatrixForDisplay = TransformMatrices.computeZernickeTransformMatrix(32);

		final MatrixControlPanel lMatrixControlPanel = new MatrixControlPanel(8,
																																					8,
																																					lTransformMatrix,
																																					lTransformMatrixForDisplay);



		SwingUtilities.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				final JFrame lTestFrame = new JFrame("Test");
				lTestFrame.setSize(768, 768);
				lTestFrame.setLayout(new MigLayout("insets 0", "[]", "[]"));
				lTestFrame.add(lMatrixControlPanel, "cell 0 0 ");
				lTestFrame.validate();
				lTestFrame.setVisible(true);
			}
		});

		final DenseMatrix64F lInputVector = new DenseMatrix64F(64, 1);
		final DenseMatrix64F lShapeVector = new DenseMatrix64F(64, 1);

		for (int i = 0; i < 10000; i++)
		{
			final double lValue = cos(0.1 * i);
			lInputVector.set(9, lValue);

			lMatrixControlPanel.getInputModeVectorVariable()
													.setReference(lInputVector);/**/
			Thread.sleep(100);
		}

		Thread.sleep(1000000);
	}

}
