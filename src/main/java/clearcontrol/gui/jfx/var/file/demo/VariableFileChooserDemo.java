package clearcontrol.gui.jfx.var.file.demo;

import java.io.File;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.file.VariableFileChooser;

/**
 * Univariate affine function pane demo
 *
 * @author royer
 */
public class VariableFileChooserDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getName());

    Variable<File> lFileVariable = new Variable<>("File",
                                                  new File("."));

    lFileVariable.addSetListener((o, n) -> {
      System.out.println("new file: " + n);
    });

    VariableFileChooser lVariableFileChooser =
                                             new VariableFileChooser("File: ",
                                                                     lFileVariable,
                                                                     false);

    Variable<File> lFolderVariable = new Variable<>("Folder",
                                                    new File("."));

    lFolderVariable.addSetListener((o, n) -> {
      System.out.println("new folder: " + n);
    });

    VariableFileChooser lVariableFolderChooser =
                                               new VariableFileChooser("Folder: ",
                                                                       lFileVariable,
                                                                       true);

    root.getChildren()
        .add(new VBox(lVariableFileChooser, lVariableFolderChooser));

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
