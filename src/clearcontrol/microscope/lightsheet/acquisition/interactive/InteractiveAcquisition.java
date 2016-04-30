package clearcontrol.microscope.lightsheet.acquisition.interactive;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.device.signal.SignalStartableLoopTaskDevice;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;

public class InteractiveAcquisition	extends
																		SignalStartableLoopTaskDevice
{

	private LightSheetMicroscope mLightSheetMicroscope;
	
	private volatile InteractiveAcquisitionModes mRequestedAcquisitionMode = InteractiveAcquisitionModes.None;
	private volatile InteractiveAcquisitionModes mCurrentAcquisitionMode = InteractiveAcquisitionModes.None;


	public InteractiveAcquisition(String pDeviceName,
																LightSheetMicroscope pLightSheetMicroscope)
	{
		super(pDeviceName, false);
		mLightSheetMicroscope = pLightSheetMicroscope;
	}

	@Override
	protected boolean loop()
	{

		try
		{
			try
			{
				if(mRequestedAcquisitionMode!=mCurrentAcquisitionMode)
				{
					// prepare queue
					System.out.println("Preparing Queue...");
					
				
					mCurrentAcquisitionMode = mRequestedAcquisitionMode;
				}
				
				
				//play queue
				System.out.println("Playing Queue...");
				mLightSheetMicroscope.playQueueAndWait(10, TimeUnit.SECONDS);
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}
		catch (TimeoutException e)
		{
			e.printStackTrace();
		}

		return true;
	}

	public void start2DAcquisition()
	{
		System.out.println("Starting 2D Acquisition...");
		mRequestedAcquisitionMode = InteractiveAcquisitionModes.Acquisition2D;
		start();
	}


	public void start3DAcquisition()
	{
		System.out.println("Starting 3D Acquisition...");
		mRequestedAcquisitionMode = InteractiveAcquisitionModes.Acquisition2D;
		start();
	}
	
	public void stopAcquisition()
	{
		System.out.println("Stopping Acquisition...");
		stop();
	}

}
