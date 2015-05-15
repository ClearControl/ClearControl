package rtlib.microscope.lightsheetmicroscope.illumination.demo;

import org.junit.Test;

import rtlib.microscope.lightsheetmicroscope.illumination.LightSheet;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.devices.sim.SignalGeneratorSimulatorDevice;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.ScoreInterface;

public class LightSheetDemo
{

	@Test
	public void demo()
	{
		final LightSheet lLightSheet = new LightSheet(null, 9.4, 512, 2);

		final Movement lBeforeExposureMovement = new Movement("BeforeExposure");
		final Movement lExposureMovement = new Movement("Exposure");

		lLightSheet.addStavesToBeforeExposureMovement(lBeforeExposureMovement);
		lLightSheet.addStavesToExposureMovement(lExposureMovement);

		SignalGeneratorInterface lSignalGeneratorDevice = new SignalGeneratorSimulatorDevice();

		ScoreInterface lStagingScore = lSignalGeneratorDevice.getStagingScore();

		lStagingScore.addMovement(lBeforeExposureMovement);
		lStagingScore.addMovement(lExposureMovement);

		lSignalGeneratorDevice.addCurrentStateToQueue();

		lSignalGeneratorDevice.playQueue();

	}

}
