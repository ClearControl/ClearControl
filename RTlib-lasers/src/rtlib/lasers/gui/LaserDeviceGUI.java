package rtlib.lasers.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.component.RunnableFX;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.gui.demo.LaserGauge;
import utils.RunFX;

/**
 * LaserDeviceGUI handles the collection of lasers to creates multiple instances of LaserGauge
 */
public class LaserDeviceGUI implements RunnableFX
{
	public final LaserGauge mLaser;

	public LaserDeviceGUI()
	{
		mLaser = new LaserGauge( "488" );
	}

	public LaserDeviceGUI( LaserDeviceInterface pLaserDeviceInterface )
	{
		mLaser = new LaserGauge( "" + pLaserDeviceInterface.getWavelengthInNanoMeter() );

		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		//		setLayout(new MigLayout("",
		//														"[][grow][grow]",
		//														"[][grow][grow][grow][grow][grow][grow][]"));
		//
		//		mLaserSetPowerLabel = new JLabel("target power (mW)");
		//		add(mLaserSetPowerLabel, "cell 1 0");
		//
		//		mLaserCurrentPowerLabel = new JLabel("current power (mW)");
		//		add(mLaserCurrentPowerLabel, "cell 2 0");
		//
		//		m405OnOffSwitch = new JCheckBoxBoolean((String) null);
		//		add(m405OnOffSwitch, "cell 0 1");
		//
		//		mLaser405TargetPower = new JSliderDouble("405 nm", 0, 100.0, 0.0);
		//		add(mLaser405TargetPower, "cell 1 1,grow");
		//		mLaser405TargetPower.setBackground(new Color(73, 0, 188).brighter()
		//																														.brighter());
		//
		//		mLaser405CurrentPower = new JSliderDouble("405 nm",
		//																							0.0,
		//																							100.0,
		//																							0.0);
		//		mLaser405CurrentPower.setPlusMinusButtonsVisible(false);
		//		add(mLaser405CurrentPower, "cell 2 1,grow");
		//		mLaser405CurrentPower.setBackground(new Color(73, 0, 188).brighter()
		//																															.brighter());
		//		// mLaser405CurrentPower.setForeground(Color.white);
		//
		//		m488OnOffSwitch = new JCheckBoxBoolean((String) null);
		//		add(m488OnOffSwitch, "cell 0 2");
		//
		//		mLaser488TargetPower = new JSliderDouble("488 nm", 0, 100, 0);
		//		add(mLaser488TargetPower, "cell 1 2,grow");
		//		mLaser488TargetPower.setBackground(new Color(0, 244, 255));
		//
		//		mLaser488CurrentPower = new JSliderDouble("488 nm",
		//																							0.0,
		//																							100.0,
		//																							0.0);
		//		mLaser488CurrentPower.setPlusMinusButtonsVisible(false);
		//		add(mLaser488CurrentPower, "cell 2 2,grow");
		//		mLaser488CurrentPower.setBackground(new Color(0, 244, 255));
		//
		//		m515OnOffSwitch = new JCheckBoxBoolean((String) null);
		//		add(m515OnOffSwitch, "cell 0 3");
		//
		//		mLaser515TargetPower = new JSliderDouble("515 nm", 0, 80, 0);
		//		add(mLaser515TargetPower, "cell 1 3,grow");
		//		mLaser515TargetPower.setBackground(new Color(18, 255, 0));
		//
		//		mLaser515CurrentPower = new JSliderDouble("515 nm",
		//																							0.0,
		//																							80.0,
		//																							0.0);
		//		mLaser515CurrentPower.setPlusMinusButtonsVisible(false);
		//		add(mLaser515CurrentPower, "cell 2 3,grow");
		//		mLaser515CurrentPower.setBackground(new Color(18, 255, 0));
		//
		//		m561OnOffSwitch = new JCheckBoxBoolean((String) null);
		//		add(m561OnOffSwitch, "cell 0 4");
		//
		//		mLaser561TargetPower = new JSliderDouble("561 nm", 0, 100, 0);
		//		add(mLaser561TargetPower, "cell 1 4,grow");
		//		mLaser561TargetPower.setBackground(new Color(185, 255, 0));
		//
		//		mLaser561CurrentPower = new JSliderDouble("561 nm",
		//																							0.0,
		//																							100.0,
		//																							0.0);
		//		mLaser561CurrentPower.setPlusMinusButtonsVisible(false);
		//		add(mLaser561CurrentPower, "cell 2 4,grow");
		//		mLaser561CurrentPower.setBackground(new Color(185, 255, 0));
		//
		//		m594OnOffSwitch = new JCheckBoxBoolean((String) null);
		//		add(m594OnOffSwitch, "cell 0 5");
		//
		//		mLaser594TargetPower = new JSliderDouble("594 nm", 0, 100, 0);
		//		add(mLaser594TargetPower, "cell 1 5,grow");
		//		mLaser594TargetPower.setBackground(new Color(255, 200, 0));
		//
		//		mLaser594CurrentPower = new JSliderDouble("594 nm",
		//																							0.0,
		//																							100.0,
		//																							0.0);
		//		mLaser594CurrentPower.setPlusMinusButtonsVisible(false);
		//		add(mLaser594CurrentPower, "cell 2 5,grow");
		//		mLaser594CurrentPower.setBackground(new Color(255, 200, 0));

		//		final ArrayList<String> lFilterList = lCurrentMachineConfiguration.getList("filterwheel.fli.1");
		//
		//		mDetectionFilterWheelLabel = new JLabel("Detection filter wheel");
		//		add(mDetectionFilterWheelLabel, "cell 1 6");
		//		mFilterWheelPosition = new JSliderIndexedStrings(	"",
		//																											lFilterList,
		//																											0);
		//		add(mFilterWheelPosition, "cell 1 7,grow");
		//
		//		mFilterWheelSpeed = new JSliderDouble("speed",
		//																					0.0,
		//																					4,
		//																					2);
		//		add(mFilterWheelSpeed, "cell 2 7,grow");

		//		if (pLasertHubDevice != null && pFilterWheelDeviceInterface != null)
		//		{
		//			mFilterWheelPosition.getDoubleVariable()
		//													.syncWith(pFilterWheelDeviceInterface.getPositionVariable());
		//			mFilterWheelSpeed.getDoubleVariable()
		//												.syncWith(pFilterWheelDeviceInterface.getSpeedVariable());
		//
		//			m405OnOffSwitch.getBooleanVariable()
		//											.sendUpdatesTo(pLasertHubDevice.getOnVariableByWavelength(405));
		//			m488OnOffSwitch.getBooleanVariable()
		//											.sendUpdatesTo(pLasertHubDevice.getOnVariableByWavelength(488));
		//			m515OnOffSwitch.getBooleanVariable()
		//											.sendUpdatesTo(pLasertHubDevice.getOnVariableByWavelength(515));
		//			m561OnOffSwitch.getBooleanVariable()
		//											.sendUpdatesTo(pLasertHubDevice.getOnVariableByWavelength(561));
		//			m594OnOffSwitch.getBooleanVariable()
		//											.sendUpdatesTo(pLasertHubDevice.getOnVariableByWavelength(594));
		//
		//			mLaser405TargetPower.getDoubleVariable()
		//													.sendUpdatesTo(pLasertHubDevice.getTargetPowerInMilliWattVariableByWavelength(405));
		//			mLaser488TargetPower.getDoubleVariable()
		//													.sendUpdatesTo(pLasertHubDevice.getTargetPowerInMilliWattVariableByWavelength(488));
		//			mLaser515TargetPower.getDoubleVariable()
		//													.sendUpdatesTo(pLasertHubDevice.getTargetPowerInMilliWattVariableByWavelength(515));
		//			mLaser561TargetPower.getDoubleVariable()
		//													.sendUpdatesTo(pLasertHubDevice.getTargetPowerInMilliWattVariableByWavelength(561));
		//			mLaser594TargetPower.getDoubleVariable()
		//													.sendUpdatesTo(pLasertHubDevice.getTargetPowerInMilliWattVariableByWavelength(594));
		//
		//			mLaser405CurrentPower.getDoubleVariable()
		//														.syncWith(pLasertHubDevice.getCurrentPowerInMilliWattVariableByWavelength(405));
		//			mLaser488CurrentPower.getDoubleVariable()
		//														.syncWith(pLasertHubDevice.getCurrentPowerInMilliWattVariableByWavelength(488));
		//			mLaser515CurrentPower.getDoubleVariable()
		//														.syncWith(pLasertHubDevice.getCurrentPowerInMilliWattVariableByWavelength(515));
		//			mLaser561CurrentPower.getDoubleVariable()
		//														.syncWith(pLasertHubDevice.getCurrentPowerInMilliWattVariableByWavelength(561));
		//			mLaser594CurrentPower.getDoubleVariable()
		//														.syncWith(pLasertHubDevice.getCurrentPowerInMilliWattVariableByWavelength(594));
		//		}

	}

	public void setCurrentValues()
	{
		//		mLaser405TargetPower.getDoubleVariable().setCurrent();
		//		mLaser488TargetPower.getDoubleVariable().setCurrent();
		//		mLaser515TargetPower.getDoubleVariable().setCurrent();
		//		mLaser561TargetPower.getDoubleVariable().setCurrent();

		//		mFilterWheelPosition.getDoubleVariable().setCurrent();
		//		mFilterWheelSpeed.getDoubleVariable().setValue(2);
		// mFilterWheelSpeed.getDoubleVariable().setCurrent();

		//		m405OnOffSwitch.getBooleanVariable().setCurrent();
		//		m488OnOffSwitch.getBooleanVariable().setCurrent();
		//		m515OnOffSwitch.getBooleanVariable().setCurrent();
		//		m561OnOffSwitch.getBooleanVariable().setCurrent();
	}

	//	public static void main(String[] args)
	//	{
	//		final JFrame lTestFrame = new JFrame("Demo");
	//		final LaserDeviceGUI lPanel = new LaserDeviceGUI( null );
	//		try
	//		{
	//			SwingUtilities.invokeAndWait( () -> {
	//				lTestFrame.setSize(768, 768);
	//				lTestFrame.setLayout(new MigLayout(	"insets 0",
	//						"[]",
	//						"[]"));
	//				lTestFrame.add(lPanel, "cell 0 0 ");
	//				lTestFrame.validate();
	//				lTestFrame.setVisible(true);
	//			} );
	//		} catch (InterruptedException e)
	//		{
	//			e.printStackTrace();
	//		} catch (InvocationTargetException e)
	//		{
	//			e.printStackTrace();
	//		}
	//	}

	@Override public void init()
	{
		mLaser.init();
	}

	@Override public void start(Stage stage) {
		VBox pane = new VBox();

		pane.getChildren().addAll( mLaser.getPanel() );

		Scene scene = new Scene(pane, javafx.scene.paint.Color.WHITE);

		stage.setTitle("Laser");
		stage.setScene(scene);
		stage.show();
	}

	@Override public void stop()
	{
	}

	public HBox getPanel()
	{
		return mLaser.getPanel();
	}

	public static void main(String[] args)
	{
		RunFX.start( new LaserDeviceGUI() );
	}
}
