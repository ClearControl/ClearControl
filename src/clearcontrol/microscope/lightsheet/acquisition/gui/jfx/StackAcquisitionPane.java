package clearcontrol.microscope.lightsheet.acquisition.gui.jfx;


import org.controlsfx.control.RangeSlider;

import clearcontrol.microscope.lightsheet.acquisition.StackAcquisitionInterface;
import javafx.scene.layout.GridPane;

public class StackAcquisitionPane extends GridPane
{

	private StackAcquisitionInterface mStackAcquisition;

	public StackAcquisitionPane(StackAcquisitionInterface pStackAcquisition)
	{
		super();
		mStackAcquisition = pStackAcquisition;

		RangeSlider lRangeSlider = new RangeSlider(0, 1, 0.1, 0.9);

		add(lRangeSlider, 0, 0);
	}

}
