package clearcontrol.stack.sourcesink.source.viewer.gui.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.stack.sourcesink.source.viewer.StackSourceViewer;
import clearcontrol.stack.sourcesink.source.viewer.gui.StackSourceViewerPanel;
import clearcontrol.stack.sourcesink.synthetic.FractalStackSource;

/**
 * Stack source viewer panel demo
 *
 * @author royer
 */
public class StackSourceViewerPanelDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {

    FractalStackSource lFractalStackSource = new FractalStackSource();

    StackSourceViewer lStackSourceViewer = new StackSourceViewer();

    lStackSourceViewer.getStackSourceVariable()
                      .set(lFractalStackSource);

    StackSourceViewerPanel lStackSourceViewerPanel =
                                                   new StackSourceViewerPanel(lStackSourceViewer);

    Scene scene = new Scene(lStackSourceViewerPanel,
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
    Application.launch(StackSourceViewerPanelDemo.class);
  }

}
