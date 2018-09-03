package clearcontrol.gui.jfx.sandbox;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ListViewContextMenuExample extends Application
{

  @Override
  public void start(Stage primaryStage)
  {
    ListView<String> listView = new ListView<>();
    listView.getItems().addAll("One", "Two", "Three");

    listView.setCellFactory(lv -> {

      ListCell<String> cell = new ListCell<>();

      ContextMenu contextMenu = new ContextMenu();

      MenuItem editItem = new MenuItem();
      editItem.textProperty()
              .bind(Bindings.format("Edit \"%s\"",
                                    cell.itemProperty()));
      editItem.setOnAction(event -> {
        String item = cell.getItem();
        // code to edit item...
      });
      MenuItem deleteItem = new MenuItem();
      deleteItem.textProperty()
                .bind(Bindings.format("Delete \"%s\"",
                                      cell.itemProperty()));
      deleteItem.setOnAction(event -> listView.getItems()
                                              .remove(cell.getItem()));
      contextMenu.getItems().addAll(editItem, deleteItem);

      cell.textProperty().bind(cell.itemProperty());

      cell.emptyProperty()
          .addListener((obs, wasEmpty, isNowEmpty) -> {
            if (isNowEmpty)
            {
              cell.setContextMenu(null);
            }
            else
            {
              cell.setContextMenu(contextMenu);
            }
          });
      return cell;
    });

    BorderPane root = new BorderPane(listView);
    primaryStage.setScene(new Scene(root, 250, 400));
    primaryStage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
