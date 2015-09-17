package rtlib.microscope.lsm.component.selector;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.ConstantStave;

public class LightSheetSelector extends NamedVirtualDevice	implements
															LightSheetSelectorInterface
{

	private final DoubleVariable mLightSheetSelection = new DoubleVariable(	"SelectedLightSheet",
																			0);

	private final ConstantStave[] mBitStave;
	private int[] mStaveIndex;

	public LightSheetSelector(String pName, int pNumberOfTTLLines)
	{
		super(pName);

		reset();

		final VariableSetListener<Double> lDoubleVariableListener = (	u,
																		v) -> {
			update();
		};

		final VariableSetListener<UnivariateAffineComposableFunction> lObjectVariableListener = (	u,
																									v) -> {
			update();
		};

		mLightSheetSelection.addSetListener(lDoubleVariableListener);

		mBitStave = new ConstantStave[pNumberOfTTLLines];
		mStaveIndex = new int[pNumberOfTTLLines];

		for (int i = 0; i < mBitStave.length; i++)
		{
			mStaveIndex[i] = MachineConfiguration.getCurrentMachineConfiguration()
													.getIntegerProperty("device.lsm.selector." + getName()
																				+ i
																				+ ".index",
																		-1);
			mBitStave[i] = new ConstantStave("lightsheet.s." + i, 0);
		}

	}

	public void reset()
	{

	}

	@Override
	public DoubleVariable getSelectorVariable()
	{
		return mLightSheetSelection;
	}

	public void addStavesToBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		// Analog outputs before exposure:
		for (int i = 0; i < mBitStave.length; i++)
		{
			pBeforeExposureMovement.setStave(	mStaveIndex[i],
												mBitStave[i]);
		}
	}

	public void addStavesToExposureMovement(Movement pExposureMovement)
	{
		// Analog outputs at exposure:
		for (int i = 0; i < mBitStave.length; i++)
		{
			pExposureMovement.setStave(mStaveIndex[i], mBitStave[i]);
		}
	}

	public void update()
	{
		synchronized (this)
		{
			for (int i = 0; i < mBitStave.length; i++)
			{
				mBitStave[i].setValue(getBit(	mLightSheetSelection.getValue(),
												i));
			}
		}
	}

	private float getBit(double pValue, int pBit)
	{
		return ((((int) pValue) >> pBit) & 1);
	}
}
