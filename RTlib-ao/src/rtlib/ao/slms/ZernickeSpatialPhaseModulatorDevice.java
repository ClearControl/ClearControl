package rtlib.ao.slms;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import rtlib.ao.zernike.TransformMatrices;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;

public class ZernickeSpatialPhaseModulatorDevice extends
																								SpatialPhaseModulatorDeviceBase	implements
																																								SpatialPhaseModulatorDeviceInterface
{

	final DenseMatrix64F mZernickeTransformMatrix;

	protected SpatialPhaseModulatorDeviceInterface mDelegatedSpatialPhaseModulatorDeviceInterface;

	public ZernickeSpatialPhaseModulatorDevice(SpatialPhaseModulatorDeviceInterface pSpatialPhaseModulatorDeviceInterface)
	{
		super("Zernicke" + pSpatialPhaseModulatorDeviceInterface.getDeviceName(),
					pSpatialPhaseModulatorDeviceInterface.getMatrixHeight(),
					pSpatialPhaseModulatorDeviceInterface.getActuatorResolution());
		mDelegatedSpatialPhaseModulatorDeviceInterface = pSpatialPhaseModulatorDeviceInterface;

		final int lMatrixWidth = mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixWidth();
		final int lMatrixHeight = mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixHeight();

		mZernickeTransformMatrix = TransformMatrices.computeZernickeTransformMatrix(lMatrixHeight);

		mMatrixVariable = new ObjectVariable<DenseMatrix64F>(	"Matrix",
																													new DenseMatrix64F(	lMatrixHeight * lMatrixWidth,
																																							1))
		{

			@Override
			public DenseMatrix64F setEventHook(	DenseMatrix64F pOldValue,
																					DenseMatrix64F pNewValue)
			{
				final int lMatrixWidth = mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixWidth();
				final int lMatrixHeight = mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixHeight();
				final DenseMatrix64F lTransformedVector = new DenseMatrix64F(	lMatrixWidth * lMatrixHeight,
																																			1);

				CommonOps.mult(	mZernickeTransformMatrix,
												pNewValue,
												lTransformedVector);

				mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixReference()
																											.set(lTransformedVector);

				// System.out.println(lTransformedVector);

				return super.setEventHook(pOldValue, pNewValue);
			}
		};

	}

	@Override
	public int getMatrixWidth()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixWidth();
	}

	@Override
	public int getMatrixHeight()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixHeight();
	}

	@Override
	public DoubleVariable getMatrixWidthVariable()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixWidthVariable();
	}

	@Override
	public DoubleVariable getMatrixHeightVariable()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.getMatrixHeightVariable();
	}

	@Override
	public DoubleVariable getActuatorResolutionVariable()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.getActuatorResolutionVariable();
	}

	@Override
	public DoubleVariable getNumberOfActuatorVariable()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.getNumberOfActuatorVariable();
	}

	@Override
	public void zero()
	{
		getMatrixReference().get().zero();
	}

	@Override
	public long getRelaxationTimeInMilliseconds()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.getRelaxationTimeInMilliseconds();
	}


	@Override
	public boolean start()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.start();
	}

	@Override
	public boolean stop()
	{
		return mDelegatedSpatialPhaseModulatorDeviceInterface.stop();
	}

	public static ZernickeSpatialPhaseModulatorDevice wrap(SpatialPhaseModulatorDeviceInterface pSpatialPhaseModulatorDeviceInterface)
	{
		return new ZernickeSpatialPhaseModulatorDevice(pSpatialPhaseModulatorDeviceInterface);
	}

}
