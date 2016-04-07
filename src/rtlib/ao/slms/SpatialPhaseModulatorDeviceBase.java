package rtlib.ao.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.variable.ObjectVariable;

public abstract class SpatialPhaseModulatorDeviceBase	extends
																											SignalStartableDevice	implements
																																						SpatialPhaseModulatorDeviceInterface
{

	protected ObjectVariable<Double> mMatrixWidthVariable;
	protected ObjectVariable<Double> mMatrixHeightVariable;
	protected ObjectVariable<Double> mActuatorResolutionVariable;
	protected ObjectVariable<Double> mNumberOfActuatorsVariable;

	protected ObjectVariable<DenseMatrix64F> mMatrixVariable;

	public SpatialPhaseModulatorDeviceBase(	String pDeviceName,
																					int pFullMatrixWidthHeight,
																					int pActuatorResolution)
	{
		super(pDeviceName);

		mMatrixWidthVariable = new ObjectVariable<Double>("MatrixWidth",
																											(double) pFullMatrixWidthHeight);
		mMatrixHeightVariable = new ObjectVariable<Double>(	"MatrixHeight",
																												(double) pFullMatrixWidthHeight);
		mActuatorResolutionVariable = new ObjectVariable<Double>(	"ActuatorResolution",
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
	public ObjectVariable<Double> getMatrixWidthVariable()
	{
		return mMatrixWidthVariable;
	}

	@Override
	public ObjectVariable<Double> getMatrixHeightVariable()
	{
		return mMatrixHeightVariable;
	}

	@Override
	public ObjectVariable<Double> getActuatorResolutionVariable()
	{
		return mActuatorResolutionVariable;
	}

	@Override
	public ObjectVariable<Double> getNumberOfActuatorVariable()
	{
		return mNumberOfActuatorsVariable;
	}

	@Override
	public ObjectVariable<DenseMatrix64F> getMatrixReference()
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
