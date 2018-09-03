package clearcontrol.devices.lasers.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import clearcontrol.core.physics.WavelengthToRGB;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.lasers.gui.icon.LaserIcon;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.custom.rbg.RadialBargraph;
import clearcontrol.gui.jfx.custom.rbg.RadialBargraphBuilder;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import eu.hansolo.enzo.common.Marker;
import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.onoffswitch.IconSwitch;

/**
 * Laser device GUI panel
 *
 * @author royer
 */
public class LaserDevicePanel extends HBox
{

  private LaserDeviceInterface mLaserDeviceInterface;

  private String mPowerUnits;
  private double mMaxPower;

  private IconSwitch mLaserOnSwitch;
  private RadialBargraph mTargetPowerGauge;
  private RadialBargraph mCurrentPowerGauge;
  private Marker mTargetPowerMarker;
  private Label mLaserLabel;
  private VBox mWavelengthColorBox;

  private VBox properties;
  private HBox pane;
  private CustomGridPane grid;

  private int mWaveLength;

  /**
   * Instanciates a laser device panel given a laser device.
   * 
   * @param pLaserDeviceInterface
   *          laser device
   */
  public LaserDevicePanel(LaserDeviceInterface pLaserDeviceInterface)
  {
    mLaserDeviceInterface = pLaserDeviceInterface;
    mWaveLength = mLaserDeviceInterface.getWavelengthInNanoMeter();
    mPowerUnits = "mW";
    mMaxPower = mLaserDeviceInterface.getMaxPowerInMilliWatt();
    init();

    // data -> GUI

    // Laser Switch update (data -> GUI)
    mLaserDeviceInterface.getLaserOnVariable()
                         .addSetListener((o, n) -> {
                           if (mLaserOnSwitch.isSelected() != n)
                             Platform.runLater(() -> {
                               mLaserOnSwitch.setSelected(n);
                             });
                         });

    // CurrentPower update (data -> GUI)
    mLaserDeviceInterface.getCurrentPowerInMilliWattVariable()
                         .addSetListener((o, n) -> {
                           if (mCurrentPowerGauge.valueProperty()
                                                 .getValue() != n)
                             Platform.runLater(() -> {
                               mCurrentPowerGauge.valueProperty()
                                                 .set(n.doubleValue());
                             });
                         });

    // Wavelength update (data -> GUI)
    mLaserDeviceInterface.getWavelengthInNanoMeterVariable()
                         .addSetListener((o, n) -> {
                           if (mWaveLength != n)
                             Platform.runLater(() -> {
                               mWaveLength = n;
                               mWavelengthColorBox.setBackground(new Background(new BackgroundFill(getWebColor(mWaveLength),
                                                                                                   CornerRadii.EMPTY,
                                                                                                   Insets.EMPTY)));
                               mLaserLabel.setText(mWaveLength
                                                   + " nm");
                             });
                         });

    // GUI -> data
    // TargetPower update (GUI -> data)
    mTargetPowerGauge.valueProperty()
                     .setValue(mLaserDeviceInterface.getTargetPowerInMilliWatt());

    EventHandler<MouseEvent> lEventHandler =
                                           new EventHandler<MouseEvent>()
                                           {
                                             @Override
                                             public void handle(MouseEvent event)
                                             {
                                               /*System.out.format("marker=%f, gauge=%f \n",
                                                                 mTargetPowerMarker.getValue(),
                                                                 mTargetPowerGauge.getValue());/**/
                                               mLaserDeviceInterface.getTargetPowerInMilliWattVariable()
                                                                    .setAsync(mTargetPowerGauge.getValue());
                                             }
                                           };
    mTargetPowerGauge.setOnMouseDragged(lEventHandler);
    mTargetPowerGauge.setOnMousePressed(lEventHandler);
    mTargetPowerGauge.setOnMouseClicked(lEventHandler);

    // Laser switch update (GUI -> data)
    Boolean lLaserOn =
                     mLaserDeviceInterface.getLaserOnVariable().get();
    mLaserOnSwitch.setSelected(lLaserOn == null ? false : lLaserOn);

    mLaserOnSwitch.setOnMouseClicked(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent event)
      {
        mLaserDeviceInterface.getLaserOnVariable()
                             .setAsync(mLaserOnSwitch.selectedProperty()
                                                     .get());
      }
    });/**/

    setBackground(null);
    // hBox.setPadding(new Insets(15, 15, 15, 15));
    setSpacing(10);
    getChildren().addAll(pane,
                         mTargetPowerGauge,
                         mCurrentPowerGauge,
                         grid);
    setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;"
             + "-fx-border-color: black");
  }

  private void init()
  {
    // Power on/off
    mLaserOnSwitch = new IconSwitch();
    mLaserOnSwitch.setSelected(false);

    mLaserOnSwitch.setSymbolType(SymbolType.POWER);
    mLaserOnSwitch.setSymbolColor(Color.web("#ffffff"));
    mLaserOnSwitch.setSwitchColor(Color.web("#34495e"));
    mLaserOnSwitch.setThumbColor(Color.web("#ff495e"));

    // Gauge bar gradient
    List<Stop> stops = new ArrayList<>();
    stops.add(new Stop(0.0, Color.BLUE));
    stops.add(new Stop(0.31, Color.CYAN));
    stops.add(new Stop(0.5, Color.LIME));
    stops.add(new Stop(0.69, Color.YELLOW));
    stops.add(new Stop(1.0, Color.RED));

    // Target gauge build

    // Marker for user input
    mTargetPowerMarker = new Marker(0, mPowerUnits);
    mTargetPowerGauge = RadialBargraphBuilder.create()
                                             .title("Target")
                                             .unit(mPowerUnits)
                                             .markers(mTargetPowerMarker)
                                             .maxValue(mMaxPower)
                                             .build();
    mTargetPowerGauge.setBarGradientEnabled(true);
    mTargetPowerGauge.setBarGradient(stops);
    mTargetPowerGauge.setAnimated(false);
    mTargetPowerGauge.setInteractive(true);

    // As soon as user changes the target value, it updates gauge value
    mTargetPowerMarker.valueProperty()
                      .bindBidirectional(mTargetPowerGauge.valueProperty());

    // Actual gauge build
    mCurrentPowerGauge = RadialBargraphBuilder.create()
                                              .title("Current")
                                              .unit(mPowerUnits)
                                              .maxValue(mMaxPower)
                                              .build();
    mCurrentPowerGauge.setAnimated(false);
    mCurrentPowerGauge.setBarGradientEnabled(true);
    mCurrentPowerGauge.setBarGradient(stops);
    mCurrentPowerGauge.setDisable(true);

    // Laser name with Wavelength
    properties = new VBox();
    // properties.setPadding(new Insets(10));
    properties.setPrefWidth(100);
    properties.setSpacing(3);

    mLaserLabel = new Label();
    String fontFamily = "Arial Black";
    mLaserLabel.setText(mWaveLength + " nm");
    mLaserLabel.setFont(new Font(fontFamily, 24));

    mWavelengthColorBox = new VBox();
    mWavelengthColorBox.setBackground(new Background(new BackgroundFill(getWebColor(mWaveLength),
                                                                        CornerRadii.EMPTY,
                                                                        Insets.EMPTY)));
    Rectangle rectangle = new Rectangle(33, 80, Color.TRANSPARENT);

    properties.widthProperty()
              .addListener(new ChangeListener<Number>()
              {
                @Override
                public void changed(ObservableValue<? extends Number> observable,
                                    Number oldValue,
                                    Number newValue)
                {
                  mLaserLabel.fontProperty()
                             .set(Font.font(fontFamily,
                                            newValue.doubleValue()
                                                        / 4.1));
                }
              });

    properties.getChildren().add(mLaserLabel);

    pane = new HBox();

    pane.widthProperty().addListener(new ChangeListener<Number>()
    {
      @Override
      public void changed(ObservableValue<? extends Number> observable,
                          Number oldValue,
                          Number newValue)
      {
        rectangle.setWidth(newValue.doubleValue() / 4.5);
      }
    });

    mWavelengthColorBox.getChildren().add(rectangle);

    VBox vBox = new VBox();
    // vBox.setPadding(new Insets(10, 10, 10, 10));

    // vBox.setBackground(new Background(new BackgroundFill( Color.web(
    // WavelengthColors.getWebColorString( waveLength ) ), CornerRadii.EMPTY,
    // Insets.EMPTY)));
    vBox.setSpacing(8);
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(properties, mLaserOnSwitch);

    pane.getChildren().addAll(mWavelengthColorBox, vBox);

    // ---------------------------------------------------------------
    int lRow = 0;
    grid = new CustomGridPane();
    Label lLabel =
                 new Label("*The tools on the left are deprecated and just kept for legacy. Future laser control comes here (under development):");
    lLabel.setTextFill(Color.web("#DD0000"));
    lLabel.setWrapText(true);
    lLabel.setMaxWidth(150);
    grid.add(lLabel, 0, lRow);
    lRow++;

    Button lLaserOffButton = new Button("Turn laser OFF");
    lLaserOffButton.setGraphic(new LaserIcon(50, 50));
    lLaserOffButton.setMaxWidth(Double.MAX_VALUE);
    lLaserOffButton.setOnAction((e) -> {
      mLaserDeviceInterface.setLaserOn(false);
      mLaserDeviceInterface.setLaserPowerOn(false);
      mLaserDeviceInterface.setTargetPowerInPercent(0);
    });
    grid.add(lLaserOffButton, 0, lRow);
    lRow++;

    Button lLaserOnButton = new Button("Turn laser on");
    lLaserOnButton.setGraphic(new LaserIcon(50, 50));
    lLaserOnButton.setMaxWidth(Double.MAX_VALUE);
    lLaserOnButton.setOnAction((e) -> {
      mLaserDeviceInterface.setLaserOn(true);
      mLaserDeviceInterface.setLaserPowerOn(true);
    });
    grid.add(lLaserOnButton, 0, lRow);
    lRow++;

    /*
    Variable<Number>
        lVariable = new Variable<Number>("", 0.0);
    lVariable.addSetListener((o,n)->{
      mLaserDeviceInterface.getTargetPowerInMilliWattVariable().set(n);
    });
    
    
    
    
    NumberVariableTextField<Number> lTargetLaserPowerField = new NumberVariableTextField<Number>("Target laser power:", lVariable, 0.0, mLaserDeviceInterface.getMaxPowerInMilliWatt(), 0.1);
    grid.add(lTargetLaserPowerField.getLabel(), 0, lRow);
    lRow++;
    grid.add(lTargetLaserPowerField.getTextField(),0 ,lRow);
    lRow++;
    */
    final Variable<Number> lTargetPowerVariable =
                                                new Variable<Number>("",
                                                                     0.0);
    mLaserDeviceInterface.getTargetPowerInMilliWattVariable()
                         .addSetListener((o, n) -> {
                           if (Math.abs(lTargetPowerVariable.get()
                                                            .doubleValue()
                                        - n.doubleValue()) > 0.1)
                           {
                             Platform.runLater(() -> {
                               lTargetPowerVariable.set(n);
                             });
                           }
                         });
    lTargetPowerVariable.addSetListener((o, n) -> {
      mLaserDeviceInterface.setTargetPowerInMilliWatt(n.doubleValue());
    });

    Variable<Number> lCurrentPowerVariable =
                                           new Variable<Number>("",
                                                                0.0);
    mLaserDeviceInterface.getCurrentPowerInMilliWattVariable()
                         .addSetListener((o, n) -> {
                           if (Math.abs(lCurrentPowerVariable.get()
                                                             .doubleValue()
                                        - n.doubleValue()) > 0.1)
                           {
                             Platform.runLater(() -> {
                               lCurrentPowerVariable.set(n);
                             });
                           }
                         });

    NumberVariableTextField<Number> lTargetLaserPowerField =
                                                           new NumberVariableTextField<Number>("Target laser power:",
                                                                                               lTargetPowerVariable,
                                                                                               0.0,
                                                                                               mLaserDeviceInterface.getMaxPowerInMilliWatt(),
                                                                                               0.1);
    grid.add(lTargetLaserPowerField.getLabel(), 0, lRow);
    lRow++;
    grid.add(lTargetLaserPowerField.getTextField(), 0, lRow);
    lRow++;

    NumberVariableTextField<Number> lCurrentLaserPowerField =
                                                            new NumberVariableTextField<Number>("Target laser power (read only):",
                                                                                                lCurrentPowerVariable,
                                                                                                0.0,
                                                                                                mLaserDeviceInterface.getMaxPowerInMilliWatt(),
                                                                                                0.1);
    grid.add(lCurrentLaserPowerField.getLabel(), 0, lRow);
    lRow++;
    grid.add(lCurrentLaserPowerField.getTextField(), 0, lRow);
    lRow++;
  }

  private Color getWebColor(int pWavelength)
  {
    Color lColor = WavelengthToRGB.waveLengthToJFXColor(pWavelength);

    return lColor;
  }

}
