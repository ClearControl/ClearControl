package rtlib.microscope.lsm.gui.halcyon.demo;


import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;
import model.node.HalcyonNode;
import rtlib.gui.halcyon.ConfigWindow;
import rtlib.gui.halcyon.NodeType;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.devices.sim.LaserDeviceSimulator;
import rtlib.lasers.gui.LaserDeviceGUI;
import view.FxFrame;
import window.console.StdOutputCaptureConsole;
import window.toolbar.MicroscopeStartStopToolbar;

/**
 * Halcyon Manager class for microscopy
 */
public class HalcyonManagerDemo extends Application
{
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		final FxFrame lHalcyonFrame = new FxFrame( new ConfigWindow() );
		lHalcyonFrame.start( primaryStage );
		primaryStage.setOnCloseRequest( event -> System.exit( 0 ) );

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

		for(Object device : mAllDeviceList)
		{
			if(device instanceof LaserDeviceInterface)
			{
				// null should be replaced by JPanel
				LaserDeviceGUI laserDeviceGUI = new LaserDeviceGUI( (LaserDeviceInterface)device );
				laserDeviceGUI.init();

				HalcyonNode node = new HalcyonNode(	"Laser-" + ((LaserDeviceInterface) device).getName(),
																						NodeType.Laser,
																						laserDeviceGUI.getPanel());
				lHalcyonFrame.addNode( node );
			}
		}

//		lHalcyonFrame.addToolbar( new DemoToolbarWindow( lHalcyonFrame.getViewManager() ) );
		lHalcyonFrame.addToolbar( new MicroscopeStartStopToolbar() );
		lHalcyonFrame.addConsole( new StdOutputCaptureConsole() );
	}


	public static void main(String[] args)
	{
		launch(args);
	}
}
