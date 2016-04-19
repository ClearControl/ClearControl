package rtlib.hardware.stages.gui.swing;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import rtlib.core.variable.VariableListener;
import rtlib.gui.swing.JSliderDouble;
import rtlib.hardware.stages.StageDeviceInterface;
import rtlib.microscope.lightsheet.component.lightsheet.LightSheetInterface;

public class StagePanel extends JPanel
{

	public final JSliderDouble mPiezoStage;
	private final JSliderDouble mStageXSliderDouble,
			mStageYSliderDouble, mStageZSliderDouble, mStageRSliderDouble;

	public StagePanel(VariableListener<Double> pRequestUpdateVariableListener,
										StageDeviceInterface pXYZRSampleStageDevice,
										LightSheetInterface pLightSheetSignalGenerator)
	{
		super();
		setLayout(new MigLayout("",
														"[450px,grow]",
														"[grow][grow][grow][grow][grow]"));

		mPiezoStage = new JSliderDouble("Piezo Stage (microns)",
																		0,
																		250,
																		125);
		add(mPiezoStage, "cell 0 0,grow");

		mStageXSliderDouble = new JSliderDouble("Sample Stage X (microns)",
																						-10000,
																						10000,
																						0);
		mStageXSliderDouble.setWaitForMouseRelease(false);
		add(mStageXSliderDouble, "cell 0 1,grow");

		mStageYSliderDouble = new JSliderDouble("Sample Stage Y (microns)",
																						-10000,
																						10000,
																						0);
		mStageYSliderDouble.setWaitForMouseRelease(false);
		add(mStageYSliderDouble, "cell 0 2,grow");

		mStageZSliderDouble = new JSliderDouble("Sample Stage Z (microns)",
																						-10000,
																						10000,
																						0);
		mStageZSliderDouble.setWaitForMouseRelease(false);
		add(mStageZSliderDouble, "cell 0 3,grow");

		mStageRSliderDouble = new JSliderDouble("Sample Stage R (micro-degrees)",
																						-360000,
																						360000,
																						0);
		mStageRSliderDouble.setWaitForMouseRelease(false);
		add(mStageRSliderDouble, "cell 0 4,grow");

		if (pRequestUpdateVariableListener != null && pLightSheetSignalGenerator != null)
		{
			// OUTDATED:
			/*mPiezoStage.getDoubleVariable()
									.sendUpdatesTo(pLightSheetSignalGenerator.getStageYVariable());/**/
			mPiezoStage.getDoubleVariable()
									.addListener(pRequestUpdateVariableListener);

		}

		if (pXYZRSampleStageDevice != null)
		{
			try
			{
				final int lStageXIndex = pXYZRSampleStageDevice.getDOFIndexByName("Stage.X");
				mStageXSliderDouble.getDoubleVariable()
														.syncWith(pXYZRSampleStageDevice.getTargetPositionVariable(lStageXIndex));

				final int lStageYIndex = pXYZRSampleStageDevice.getDOFIndexByName("Stage.Y");
				mStageYSliderDouble.getDoubleVariable()
														.syncWith(pXYZRSampleStageDevice.getTargetPositionVariable(lStageYIndex));

				final int lStageZIndex = pXYZRSampleStageDevice.getDOFIndexByName("Stage.Z");
				mStageZSliderDouble.getDoubleVariable()
														.syncWith(pXYZRSampleStageDevice.getTargetPositionVariable(lStageZIndex));

				final int lStageRIndex = pXYZRSampleStageDevice.getDOFIndexByName("Stage.R");
				mStageRSliderDouble.getDoubleVariable()
														.syncWith(pXYZRSampleStageDevice.getTargetPositionVariable(lStageRIndex));
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}

		}
	}

	public void setCurrentValues()
	{
		mPiezoStage.getDoubleVariable().setCurrent();
		mStageXSliderDouble.getDoubleVariable().setCurrent();
		mStageYSliderDouble.getDoubleVariable().setCurrent();
		mStageZSliderDouble.getDoubleVariable().setCurrent();
		mStageRSliderDouble.getDoubleVariable().setCurrent();
	}

}
