package clearcontrol.devices.slm.slms.demo;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import clearcontrol.devices.slm.slms.DeformableMirrorDevice;

public class DeformableMirrorDeviceDemoHelper
{
  public static void sweepModes(final DeformableMirrorDevice pDeformableMirrorDevice,
                                final DenseMatrix64F pTransformMatrix)
  {
    final DenseMatrix64F lInputVector = new DenseMatrix64F(64, 1);
    final DenseMatrix64F lShapeVector = new DenseMatrix64F(64, 1);

    final double lStep = 0.05;
    for (int r = 0; r < 100; r++)
      for (int m = 0; m < lInputVector.getNumElements(); m++)
        for (double x = -1; x < 1; x += lStep)
        {
          double lValue = 0.1 * x;
          System.out.format("r=%d, m=%d, x=%g, value=%g \n",
                            r,
                            m,
                            x,
                            lValue);
          lInputVector.set(m, lValue);
          CommonOps.mult(pTransformMatrix,
                         lInputVector,
                         lShapeVector);
          pDeformableMirrorDevice.getMatrixReference()
                                 .set(lShapeVector);
          try
          {
            Thread.sleep(100);
          }
          catch (InterruptedException e)
          {
          }
        }
  }

  public static void playRandomShapes(final DeformableMirrorDevice pDeformableMirrorDevice,
                                      final DenseMatrix64F pTransformMatrix,
                                      int pNumberOfShapes)
  {
    final DenseMatrix64F lInputVector = new DenseMatrix64F(64, 1);
    final DenseMatrix64F lShapeVector = new DenseMatrix64F(64, 1);

    for (int i = 0; i < pNumberOfShapes; i++)
    {
      generateRandomVector(lInputVector, 0.1);
      CommonOps.mult(pTransformMatrix, lInputVector, lShapeVector);
      pDeformableMirrorDevice.getMatrixReference().set(lShapeVector);
    }
  }

  private static void generateRandomVector(DenseMatrix64F pMatrix,
                                           double pAmplitude)
  {
    for (int i = 0; i < pMatrix.getNumElements(); i++)
      pMatrix.set(i, pAmplitude * (2 * Math.random() - 1));
  }
}
