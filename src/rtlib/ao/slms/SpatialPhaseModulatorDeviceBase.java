package rtlib.ao.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;

public abstract class SpatialPhaseModulatorDeviceBase	extends
														SignalStartableDevice	implements
																				SpatialPhaseModulatorDeviceInterface
{

	protected DoubleVariable mMatrixWidthVariable;
	protected DoubleVariable mMatrixHeightVariable;
	protected DoubleVariable mActuatorResolutionVariable;
	protected DoubleVariable mNumberOfActuatorsVariable;

	protected ObjectVariable<DenseMatrix64F> mMatrixVariable;

	public SpatialPhaseModulatorDeviceBase(	String pDeviceName,
											int pFullMatrixWidthHeight,
											int pActuatorResolution)
	{
		super(pDeviceName);

		mMatrixWidthVariable = new DoubleVariable(	"MatrixWidth",
													pFullMatrixWidthHeight);
		mMatrixHeightVariable = new DoubleVariable(	"MatrixHeight",
													pFullMatrixWidthHeight);
		mActuatorResolutionVariable = new DoubleVariable(	"ActuatorResolution",
															pActuatorResolution);

	}

	@Override
	public int getMatrixWidth()
	{
		return (int) mMatrixWidthVariable.getValue();
	}

	@Override
	public int getMatrixHeight()
	{
		return (int) mMatrixHeightVariable.getValue();
	}

	@Override
	public int getActuatorResolution()
	{
		return (int) mActuatorResolutionVariable.getValue();
	}

	@Override
	public DoubleVariable getMatrixWidthVariable()
	{
		return mMatrixWidthVariable;
	}

	@Override
	public DoubleVariable getMatrixHeightVariable()
	{
		return mMatrixHeightVariable;
	}

	@Override
	public DoubleVariable getActuatorResolutionVariable()
	{
		return mActuatorResolutionVariable;
	}

	@Override
	public DoubleVariable getNumberOfActuatorVariable()
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
		mMatrixVariable.get().set(	pU + getMatrixWidth() * pV,
									0,
									pValue);
		mMatrixVariable.setCurrent();
	}

	@Override
	public abstract void zero();

	@Override
	public abstract long getRelaxationTimeInMilliseconds();

}
