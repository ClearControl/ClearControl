package clearcontrol.hardware.slm.slms;

import org.ejml.data.DenseMatrix64F;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.VirtualDevice;

public abstract class SpatialPhaseModulatorDeviceBase	extends
																											VirtualDevice	implements
																																		SpatialPhaseModulatorDeviceInterface
{

	protected Variable<Double> mMatrixWidthVariable;
	protected Variable<Double> mMatrixHeightVariable;
	protected Variable<Double> mActuatorResolutionVariable;
	protected Variable<Double> mNumberOfActuatorsVariable;

	protected Variable<DenseMatrix64F> mMatrixVariable;

	public SpatialPhaseModulatorDeviceBase(	String pDeviceName,
																					int pFullMatrixWidthHeight,
																					int pActuatorResolution)
	{
		super(pDeviceName);

		mMatrixWidthVariable = new Variable<Double>("MatrixWidth",
																								(double) pFullMatrixWidthHeight);
		mMatrixHeightVariable = new Variable<Double>(	"MatrixHeight",
																									(double) pFullMatrixWidthHeight);
		mActuatorResolutionVariable = new Variable<Double>(	"ActuatorResolution",
																												(double) pActuatorResolution);

	}

	@Override
	public int getMatrixWidth()
	{
		return mMatrixWidthVariable.get().intValue();
	}

	@Override
	public int getMatrixHeight()
	{
		return mMatrixHeightVariable.get().intValue();
	}

	@Override
	public int getActuatorResolution()
	{
		return mActuatorResolutionVariable.get().intValue();
	}

	@Override
	public Variable<Double> getMatrixWidthVariable()
	{
		return mMatrixWidthVariable;
	}

	@Override
	public Variable<Double> getMatrixHeightVariable()
	{
		return mMatrixHeightVariable;
	}

	@Override
	public Variable<Double> getActuatorResolutionVariable()
	{
		return mActuatorResolutionVariable;
	}

	@Override
	public Variable<Double> getNumberOfActuatorVariable()
	{
		return mNumberOfActuatorsVariable;
	}

	@Override
	public Variable<DenseMatrix64F> getMatrixReference()
	{
		return mMatrixVariable;
	}

	@Override
	public void setMode(int pU, int pV, double pValue)
	{
		mMatrixVariable.get().set(pU + getMatrixWidth() * pV, 0, pValue);
		mMatrixVariable.setCurrent();
	}

	@Override
	public abstract void zero();

	@Override
	public abstract long getRelaxationTimeInMilliseconds();

}
