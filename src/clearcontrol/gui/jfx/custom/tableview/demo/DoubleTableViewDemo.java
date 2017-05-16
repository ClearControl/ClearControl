package clearcontrol.gui.jfx.custom.tableview.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import clearcontrol.gui.jfx.custom.tableview.DoubleRow;
import clearcontrol.gui.jfx.custom.tableview.DoubleTableView;

/**
 * Double table view demo
 *
 * @author royer
 */
public class DoubleTableViewDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    HBox root = new HBox();
    Scene scene = new Scene(root, 600, 600);
    stage.setScene(scene);
    stage.setTitle(DoubleTableViewDemo.class.getSimpleName());

    {
      DoubleTableView lDoubleTableView = new DoubleTableView(50);

      lDoubleTableView.addColumn("First Column", false, false);
      lDoubleTableView.addColumn("Second Column", false, true);
      lDoubleTableView.addColumn("A  .", true, false);
      lDoubleTableView.addColumn("B  .", true, true);
      // lDoubleTableView.addColumn("B");

      ObservableList<DoubleRow> lTableData =
                                           FXCollections.observableArrayList();
      lTableData.add(new DoubleRow(0, 0.1, 0.5, 1));
      lTableData.add(new DoubleRow(1, 0.2, 0.6, 2));
      lTableData.add(new DoubleRow(2, 0.3, 0.7, 3));
      lTableData.add(new DoubleRow(3, 0.4, 0.8, 4));

      lDoubleTableView.setItems(lTableData);

      root.getChildren().add(lDoubleTableView);
    }

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
