package clearcontrol.gui.jfx.var.combo.demo;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.combo.ClassComboBoxVariable;

/**
 * Enum combo box demo
 *
 * @author royer
 */
public class ClassComboBoxDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    GridPane root = new GridPane();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    Variable<Class<?>> lVariable = new Variable<>("var", null);
    lVariable.addSetListener((o, n) -> {
      System.out.println(n);
    });

    ArrayList<Class<?>> lClassList = new ArrayList<>();

    lClassList.add(Double.class);
    lClassList.add(Integer.class);

    ClassComboBoxVariable lClassComboBox =
                                         new ClassComboBoxVariable(lVariable,
                                                                   lClassList,
                                                                   100);

    root.add(lClassComboBox, 0, 2);

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
