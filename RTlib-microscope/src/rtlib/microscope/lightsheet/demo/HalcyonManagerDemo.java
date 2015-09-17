package rtlib.microscope.lightsheet.demo;

import model.javafx.FxHalcyonNode;
import model.node.HalcyonNodeType;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.devices.sim.LaserDeviceSimulator;
import rtlib.lasers.gui.LaserDeviceGUI;
import rtlib.lasers.gui.LaserGauge;
import view.HalcyonFrame;
import window.console.StdOutputCaptureConsole;
import window.demo.DemoToolbarWindow;
import window.toolbar.MicroscopeStartStopToolbar;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Halcyon Manager class for microscopy
 */
public class HalcyonManagerDemo
{
	final HalcyonFrame lHalcyonFrame = new HalcyonFrame( HalcyonFrame.GUIBackend.JavaFX );

	public HalcyonManagerDemo( ArrayList<Object> deviceLists )
	{
		for(Object device : deviceLists)
		{
			if(device instanceof LaserDeviceInterface)
			{
				// null should be replaced by JPanel
				LaserDeviceGUI laserDeviceGUI = new LaserDeviceGUI( (LaserDeviceInterface)device );
				laserDeviceGUI.init();

				FxHalcyonNode node = new FxHalcyonNode( "Laser-" + ((LaserDeviceInterface) device).getName(), HalcyonNodeType.Laser, laserDeviceGUI.getPanel() );
				lHalcyonFrame.addNode( node );
			}
		}

		lHalcyonFrame.addToolbar( new DemoToolbarWindow( lHalcyonFrame.getViewManager() ) );
		lHalcyonFrame.addToolbar( new MicroscopeStartStopToolbar() );
		lHalcyonFrame.addConsole( new StdOutputCaptureConsole() );

		try
		{
			SwingUtilities.invokeAndWait( () -> {
				lHalcyonFrame.setVisible( true );
			} );
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		while (lHalcyonFrame.isVisible())
		{
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] argv)
	{
		ArrayList<Object> mAllDeviceList = new ArrayList<>();
		ArrayList<LaserDeviceInterface> mLaserDeviceList = new ArrayList<>();

		LaserDeviceSimulator laser = new LaserDeviceSimulator( "1", 1, 405, 60 );
		mAllDeviceList.add(laser);
		mLaserDeviceList.add( laser );

		laser = new LaserDeviceSimulator( "2", 1, 488, 60 );
		mAllDeviceList.add(laser);
		mLaserDeviceList.add( laser );


		laser = new LaserDeviceSimulator( "3", 1, 515, 60 );
		mAllDeviceList.add(laser);
		mLaserDeviceList.add( laser );


		laser = new LaserDeviceSimulator( "4", 1, 561, 60 );
		mAllDeviceList.add(laser);
		mLaserDeviceList.add( laser );


		laser = new LaserDeviceSimulator( "5", 1, 594, 60 );
		mAllDeviceList.add(laser);
		mLaserDeviceList.add( laser );

		HalcyonManagerDemo manager = new HalcyonManagerDemo( mAllDeviceList );
	}
}
