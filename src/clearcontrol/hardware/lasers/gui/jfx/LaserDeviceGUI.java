package clearcontrol.hardware.lasers.gui.jfx;

import clearcontrol.gui.variable.JFXPropertyVariable;
import clearcontrol.hardware.lasers.LaserDeviceInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.scene.layout.HBox;

/**
 * LaserDeviceGUI handles the collection of lasers to creates multiple instances
 * of LaserGauge
 */
public class LaserDeviceGUI extends HBox
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

			JFXPropertyVariable<Boolean> lBooleanPropertyVariable = new JFXPropertyVariable<Boolean>(	lLaserOnBooleanProperty,
																																																"OnOffSwitchBooleanPropertyVariable",
																																																false);

			pLaserDeviceInterface.getLaserOnVariable()
														.syncWith(lBooleanPropertyVariable);
		}

		{
			Property<Number> lTargetPowerProperty = mLaser.getTargetPowerProperty();

			JFXPropertyVariable<Number> lTargetPowerPropertyVariable = new JFXPropertyVariable<Number>(	lTargetPowerProperty,
																																																	"TargetPowerPropertyVariable",
																																																	0.0);

			pLaserDeviceInterface.getTargetPowerInMilliWattVariable()
														.syncWith(lTargetPowerPropertyVariable);
		}

		{
			Property<Number> lCurrentPowerProperty = mLaser.getCurrentPowerProperty();

			JFXPropertyVariable<Number> lCurrentPowerPropertyVariable = new JFXPropertyVariable<Number>(lCurrentPowerProperty,
																																																	"CurrentPowerPropertyVariable",
																																																	0.0);

			pLaserDeviceInterface.getCurrentPowerInMilliWattVariable()
														.syncWith(lCurrentPowerPropertyVariable);
		}

		mLaser.getPanel( this );
	}
}
