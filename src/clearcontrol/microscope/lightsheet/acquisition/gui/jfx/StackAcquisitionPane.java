package clearcontrol.microscope.lightsheet.acquisition.gui.jfx;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.gui.jfx.rangeslider.VariableRangeSlider;
import clearcontrol.gui.jfx.textfield.VariableNumberTextField;
import clearcontrol.microscope.lightsheet.acquisition.StackAcquisitionInterface;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class StackAcquisitionPane extends StandardGridPane
{

	private StackAcquisitionInterface mStackAcquisition;
	private VariableRangeSlider<Number> mZRangeSlider;

	public StackAcquisitionPane(StackAcquisitionInterface pStackAcquisition)
	{
		super();
		mStackAcquisition = pStackAcquisition;

		Variable<Number> lZMin = pStackAcquisition.getStackZMinVariable();
		Variable<Number> lZMax = pStackAcquisition.getStackZMaxVariable();

		Variable<Number> lZLow = pStackAcquisition.getStackZLowVariable();
		Variable<Number> lZHigh = pStackAcquisition.getStackZHighVariable();

		Variable<Number> lZStep = pStackAcquisition.getStackZStepVariable();

		mZRangeSlider = new VariableRangeSlider<>("Z-range",
																							lZLow,
																							lZHigh,
																							lZMin,
																							lZMax,
																							lZStep,
																							5);


		VariableNumberTextField<Number> lZStepTextField = new VariableNumberTextField<Number>("Z-step",
																																													lZStep,
																																													0,
																																													Double.POSITIVE_INFINITY,
																																													0);
		lZStepTextField.getTextField().setPrefWidth(50);

		add(mZRangeSlider.getLabel(), 0, 0);
		add(mZRangeSlider.getLowTextField(), 1, 0);
		add(mZRangeSlider.getRangeSlider(), 2, 0);
		add(mZRangeSlider.getHighTextField(), 3, 0);

		add(lZStepTextField.getLabel(), 5, 0);
		add(lZStepTextField.getTextField(), 6, 0);

	}

}
