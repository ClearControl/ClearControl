package rtlib.microscope.lsm.gui.halcyon;

import model.javafx.FxHalcyonNode;
import model.node.HalcyonNodeType;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.gui.LaserDeviceGUI;
import rtlib.microscope.lsm.LightSheetMicroscopeDeviceLists;
import rtlib.microscope.lsm.LightSheetMicroscopeInterface;
import rtlib.stages.StageDeviceInterface;
import rtlib.stages.gui.StageDeviceGUI;
import view.HalcyonFrame;
import window.console.StdOutputCaptureConsole;
import window.demo.DemoToolbarWindow;
import window.toolbar.MicroscopeStartStopToolbar;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

public class HalcyonMicroscopeGUI
{
	final HalcyonFrame lHalcyonFrame = new HalcyonFrame( HalcyonFrame.GUIBackend.JavaFX );

	public HalcyonMicroscopeGUI( LightSheetMicroscopeInterface lightSheetMicroscopeInterface )
	{
		LightSheetMicroscopeDeviceLists deviceLists = lightSheetMicroscopeInterface.getDeviceLists();

		// Laser Device list
		for(int i = 0; i < deviceLists.getNumberOfLaserDevices(); i++)
		{
			LaserDeviceInterface laserDevice = deviceLists.getLaserDevice( i );

			LaserDeviceGUI laserDeviceGUI = new LaserDeviceGUI( laserDevice );
			laserDeviceGUI.init();

			FxHalcyonNode node = new FxHalcyonNode( "Laser-" + i, HalcyonNodeType.Laser, laserDeviceGUI.getPanel() );
			lHalcyonFrame.addNode( node );
		}

		// Stage Device List
		for(int i =0; i < deviceLists.getNumberOfStageDevices(); i++)
		{
			StageDeviceInterface stageDevice = deviceLists.getStageDevice( i );

			//Stage
			StageDeviceGUI stageDeviceGUI = new StageDeviceGUI( stageDevice );
			stageDeviceGUI.init();

			FxHalcyonNode node = new FxHalcyonNode( "Stage-" + i, HalcyonNodeType.Stage, stageDeviceGUI.getPanel() );
			lHalcyonFrame.addNode( node );
		}

		// Utility interfaces are added
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
}
