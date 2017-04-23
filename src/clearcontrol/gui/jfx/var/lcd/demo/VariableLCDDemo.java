package clearcontrol.gui.jfx.var.lcd.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.lcd.VariableLCD;
import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;

/**
 * On/Off array demo
 *
 * @author royer
 */
public class VariableLCDDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root, 600, 400);
    stage.setScene(scene);
    stage.setTitle("Slider Sample");
    // scene.setFill(Color.BLACK);

    Variable<Number> lNumberVariable =
                                     new Variable<>("SomeValue", 0L);

    Lcd lLCDDisplay = LcdBuilder.create()
                                // .prefWidth(480)
                                // .prefHeight(192)
                                .styleClass(Lcd.STYLE_CLASS_STANDARD)
                                // .backgroundVisible(true)
                                .value(0)
                                .foregroundShadowVisible(true)
                                .crystalOverlayVisible(true)
                                .title("time points")
                                .titleVisible(false)
                                .batteryVisible(false)
                                .signalVisible(false)
                                .alarmVisible(false)
                                .unit("tp")
                                .unitVisible(true)
                                .decimals(0)
                                .minMeasuredValueDecimals(2)
                                .minMeasuredValueVisible(false)
                                .maxMeasuredValueDecimals(2)
                                .maxMeasuredValueVisible(false)
                                .formerValueVisible(false)
                                .threshold(26)
                                .thresholdVisible(false)
                                .trendVisible(false)
                                .trend(Lcd.Trend.RISING)
                                .numberSystemVisible(true)
                                .lowerRightTextVisible(false)
                                .valueFont(Lcd.LcdFont.LCD)
                                .animated(true)

                                .build();

    VariableLCD lVarLCD =
                        new VariableLCD(lLCDDisplay, lNumberVariable);

    root.getChildren().add(lVarLCD);

    stage.show();
  }

  /**
   * Main
   * 
   * @param args
   *          NA
   */
  public static void main(String[] args)
  {
    launch(args);
  }
}
