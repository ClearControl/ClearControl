package clearcontrol.stack.sourcesink.source.viewer.app;

import java.io.File;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import clearcontrol.stack.sourcesink.source.viewer.FileStackSourceViewer;
import clearcontrol.stack.sourcesink.source.viewer.gui.FileStackSourceViewerPanel;

/**
 * Stack viewer app
 *
 * @author royer
 */
public class StackViewerApp extends Application
{

  @Override
  public void start(Stage stage)
  {

    DirectoryChooser lDirectoryChooser = new DirectoryChooser();

    lDirectoryChooser.setTitle("Select root folder");
    File lChosenRootFolder = lDirectoryChooser.showDialog(stage);

    FileStackSourceViewer lFileStackSourceViewer =
                                                 new FileStackSourceViewer(lChosenRootFolder);

    FileStackSourceViewerPanel lFileStackSourceViewerPanel =
                                                           new FileStackSourceViewerPanel(lFileStackSourceViewer);

    Scene scene = new Scene(lFileStackSourceViewerPanel, 600, 300);
    stage.setScene(scene);
    stage.setTitle(StackViewerApp.class.getSimpleName());

    stage.setOnCloseRequest(new EventHandler<WindowEvent>()
    {
      @Override
      public void handle(WindowEvent event)
      {
        try
        {
          System.out.println("Closing App.");
          lFileStackSourceViewer.close();
          System.exit(0);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });

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
