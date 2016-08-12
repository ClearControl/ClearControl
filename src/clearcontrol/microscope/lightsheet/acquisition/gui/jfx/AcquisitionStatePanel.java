package clearcontrol.microscope.lightsheet.acquisition.gui.jfx;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.gui.jfx.rangeslider.VariableRangeSlider;
import clearcontrol.gui.jfx.textfield.VariableNumberTextField;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;

public class AcquisitionStatePanel extends StandardGridPane
{

	public static final double cPrefWidth = 0;
	public static final double cPrefHeight = 0;
	
	
	private VariableRangeSlider<Number> mXRangeSlider, mYRangeSlider,
			mZRangeSlider;

	public AcquisitionStatePanel(InterpolatedAcquisitionState pAcquisitionState)
	{
		super();

		// Collecting variables:

		BoundedVariable<Number> lXLow = pAcquisitionState.getStackXLowVariable();
		BoundedVariable<Number> lXHigh = pAcquisitionState.getStackXHighVariable();

		BoundedVariable<Number> lYLow = pAcquisitionState.getStackYLowVariable();
		BoundedVariable<Number> lYHigh = pAcquisitionState.getStackYHighVariable();

		BoundedVariable<Number> lZLow = pAcquisitionState.getStackZLowVariable();
		BoundedVariable<Number> lZHigh = pAcquisitionState.getStackZHighVariable();

		Variable<Number> lZStep = pAcquisitionState.getStackZStepVariable();

		// Creating elements:

		mXRangeSlider = new VariableRangeSlider<>("X-range",
																							lXLow,
																							lXHigh,
																							lXLow.getMinVariable(),
																							lXHigh.getMaxVariable(),
																							0,
																							5);

		mYRangeSlider = new VariableRangeSlider<>("Y-range",
																							lYLow,
																							lYHigh,
																							lYLow.getMinVariable(),
																							lYHigh.getMaxVariable(),
																							0,
																							5);

		mZRangeSlider = new VariableRangeSlider<>("Z-range",
																							lZLow,
																							lZHigh,
																							lZLow.getMinVariable(),
																							lZHigh.getMaxVariable(),
																							lZStep,
																							5);

		VariableNumberTextField<Number> lZStepTextField = new VariableNumberTextField<Number>("Z-step",
																																													lZStep,
																																													0,
																																													Double.POSITIVE_INFINITY,
																																													0);
		lZStepTextField.getTextField().setPrefWidth(50);

		// Laying out components:

		add(mXRangeSlider.getLabel(), 0, 0);
		add(mXRangeSlider.getLowTextField(), 1, 0);
		add(mXRangeSlider.getRangeSlider(), 2, 0);
		add(mXRangeSlider.getHighTextField(), 3, 0);

		add(mYRangeSlider.getLabel(), 0, 1);
		add(mYRangeSlider.getLowTextField(), 1, 1);
		add(mYRangeSlider.getRangeSlider(), 2, 1);
		add(mYRangeSlider.getHighTextField(), 3, 1);

		add(mZRangeSlider.getLabel(), 0, 2);
		add(mZRangeSlider.getLowTextField(), 1, 2);
		add(mZRangeSlider.getRangeSlider(), 2, 2);
		add(mZRangeSlider.getHighTextField(), 3, 2);

		add(lZStepTextField.getLabel(), 5, 0);
		add(lZStepTextField.getTextField(), 6, 0);

	}
}
