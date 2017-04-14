package clearcontrol.gui.jfx.var.combo.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.combo.EnumComboBoxVariable;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;

/**
 * Enum combo box demo
 *
 * @author royer
 */
public class EnumComboBoxDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    GridPane root = new GridPane();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    Variable<TimeUnitEnum> lVariable =
                                     new Variable<>("var",
                                                    TimeUnitEnum.Hours);
    lVariable.addSetListener((o, n) -> {
      System.out.println(n);
    });

    EnumComboBoxVariable<TimeUnitEnum> lEnumComboBox =
                                                     new EnumComboBoxVariable<>(lVariable,
                                                                                TimeUnitEnum.values());

    root.add(lEnumComboBox, 0, 2);

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
