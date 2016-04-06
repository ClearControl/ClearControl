package rtlib.lasers.gui.jfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.HBox;
import rtlib.core.variable.javafx.BooleanPropertyVariable;
import rtlib.core.variable.javafx.DoublePropertyVariable;
import rtlib.lasers.LaserDeviceInterface;

/**
 * LaserDeviceGUI handles the collection of lasers to creates multiple instances
 * of LaserGauge
 */
public class LaserDeviceGUI
{
	public final LaserGauge mLaser;

	public LaserDeviceGUI()
	{
		mLaser = new LaserGauge();
	}

	public LaserDeviceGUI(LaserDeviceInterface pLaserDeviceInterface)
	{
		double lMaxPowerInMilliWatt = pLaserDeviceInterface.getMaxPowerInMilliWatt();

		mLaser = new LaserGauge(""	+ pLaserDeviceInterface.getWavelengthInNanoMeter(),
														"mW",
														lMaxPowerInMilliWatt);

		{
			BooleanProperty lLaserOnBooleanProperty = mLaser.getLaserOnBooleanProperty();

			BooleanPropertyVariable lBooleanPropertyVariable = new BooleanPropertyVariable(	lLaserOnBooleanProperty,
																																											"OnOffSwitchBooleanPropertyVariable",
																																											false);

			pLaserDeviceInterface.getLaserOnVariable()
														.syncWith(lBooleanPropertyVariable);
		}

		{
			DoubleProperty lTargetPowerProperty = mLaser.getTargetPowerProperty();

			DoublePropertyVariable lTargetPowerPropertyVariable = new DoublePropertyVariable(	lTargetPowerProperty,
																																												"TargetPowerPropertyVariable",
																																												0);

			pLaserDeviceInterface.getTargetPowerInMilliWattVariable()
														.syncWith(lTargetPowerPropertyVariable);
		}

		{
			DoubleProperty lCurrentPowerProperty = mLaser.getCurrentPowerProperty();

			DoublePropertyVariable lCurrentPowerPropertyVariable = new DoublePropertyVariable(lCurrentPowerProperty,
																																												"CurrentPowerPropertyVariable",
																																												0);

			pLaserDeviceInterface.getCurrentPowerInMilliWattVariable()
														.syncWith(lCurrentPowerPropertyVariable);
		}
	}

	public HBox getPanel()
	{
		return mLaser.getPanel();
	}

}
