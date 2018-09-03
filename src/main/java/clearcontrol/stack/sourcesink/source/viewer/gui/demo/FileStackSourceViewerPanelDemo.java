package clearcontrol.stack.sourcesink.source.viewer.gui.demo;

import java.io.File;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.sourcesink.sink.RawFileStackSink;
import clearcontrol.stack.sourcesink.source.viewer.FileStackSourceViewer;
import clearcontrol.stack.sourcesink.source.viewer.gui.FileStackSourceViewerPanel;
import clearcontrol.stack.sourcesink.synthetic.FractalStackSource;

/**
 * Stack source viewer panel demo
 *
 * @author royer
 */
public class FileStackSourceViewerPanelDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {

    File lRootFolder = File.createTempFile(
                                           this.getClass()
                                               .getSimpleName(),
                                           "rootfolder");
    // It actually creates a temp file so we need to delete it before creating a
    // folder...
    lRootFolder.delete();

    lRootFolder.mkdirs();

    System.out.println(lRootFolder.getAbsolutePath());

    {
      RawFileStackSink lRawFileStackSink = new RawFileStackSink();

      lRawFileStackSink.setLocation(lRootFolder, "example");

      FractalStackSource lFractalStackSource =
                                             new FractalStackSource();

      for (int i = 0; i < 10; i++)
      {
        StackInterface lStack = lFractalStackSource.getStack(i);
        lRawFileStackSink.appendStack(lStack);
        lStack.release();
      }

      lFractalStackSource.close();
      lRawFileStackSink.close();
    }

    FileStackSourceViewer lFileStackSourceViewer =
                                                 new FileStackSourceViewer(lRootFolder);

    FileStackSourceViewerPanel lFileStackSourceViewerPanel =
                                                           new FileStackSourceViewerPanel(lFileStackSourceViewer);

    Scene scene = new Scene(lFileStackSourceViewerPanel,
                            javafx.scene.paint.Color.WHITE);

    pPrimaryStage.setTitle(this.getClass().getSimpleName());
    pPrimaryStage.setScene(scene);
    pPrimaryStage.show();

  }

  /**
   * Main
   * 
   * @param args
   *          NA
   */
  public static void main(String[] args)
  {
    Application.launch(FileStackSourceViewerPanelDemo.class);
  }

}
