package clearcontrol.gui.jfx.other.recycler;

import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;

import clearcontrol.core.string.MemorySizeFormat;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import coremem.recycling.RecyclerInterface;

/**
 * Recycler panel
 *
 * @author royer
 */
public class RecyclerPanel extends CustomGridPane
{

  /**
   * Prefered panel width
   */
  public static final double cPrefWidth = 500;

  /**
   * Preferred panel height
   */
  public static final double cPrefHeight = 100;

  /**
   * Instanciates a recycler panel given a recycler
   * 
   * @param pRecycler
   *          recycler
   */
  public RecyclerPanel(RecyclerInterface<?, ?> pRecycler)
  {
    super(0, CustomGridPane.cStandardGap);

    setMaxWidth(Double.POSITIVE_INFINITY);

    Label lLiveObjectsLabel = new Label("Live Objects: ");
    Label lAvailableObjectsLabel = new Label("Available Objects: ");
    Label lFailedRequestsLabel = new Label("Failed Requests: ");

    int lMaxLive = pRecycler.getMaxNumberOfAvailableObjects();
    int lMaxAvailable = pRecycler.getMaxNumberOfAvailableObjects();
    long lFailedRequests = pRecycler.getNumberOfFailedRequests();

    Label lNumberLiveObjectsLabel = new Label(String.format("%d/%d ",
                                                            0,
                                                            lMaxLive));
    Label lNumberAvailableObjectsLabel =
                                       new Label(String.format("%d/%d ",
                                                               0,
                                                               lMaxAvailable));

    Label lNumberOfFailedRequestsLabel =
                                       new Label(String.format("%d ",
                                                               0,
                                                               lFailedRequests));

    ProgressBar lFillFactorBarLiveObjectsBar = new ProgressBar(0);
    ProgressBar lFillFactorBarAvailableObjectsBar =
                                                  new ProgressBar(0);
    ProgressBar lFailedRequestsBar = new ProgressBar(0);

    lFillFactorBarLiveObjectsBar.setMaxWidth(Double.MAX_VALUE);
    lFillFactorBarAvailableObjectsBar.setMaxWidth(Double.MAX_VALUE);
    lFailedRequestsBar.setMaxWidth(Double.MAX_VALUE);

    lFailedRequestsBar.setStyle("-fx-accent: red");

    Label lLiveMemorySizeLabel = new Label("0");
    Label lAvailableMemorySizeLabel = new Label("0");

    Button lClearLiveObjectsButton = new Button("Clear Live");
    lClearLiveObjectsButton.setOnAction((e) -> {
      pRecycler.clearLive();
    });
    Button lClearAvailableObjectsButton =
                                        new Button("Clear Available");
    lClearAvailableObjectsButton.setOnAction((e) -> {
      pRecycler.clearReleased();
    });

    // lLiveObjectsLabel.setFont(new Font(16.0));
    // lAvailableObjectsLabel.setFont(new Font(16.0));

    ColumnConstraints col1 = new ColumnConstraints();
    ColumnConstraints col2 = new ColumnConstraints(70);
    ColumnConstraints col3 = new ColumnConstraints();
    ColumnConstraints col4 = new ColumnConstraints(70);
    ColumnConstraints col5 = new ColumnConstraints();

    col3.setFillWidth(true);
    col3.setHgrow(Priority.ALWAYS);
    col3.setMaxWidth(Double.POSITIVE_INFINITY);

    getColumnConstraints().addAll(col1, col2, col3, col4, col5);

    GridPane.setHgrow(lFillFactorBarLiveObjectsBar, Priority.ALWAYS);
    GridPane.setHgrow(lFillFactorBarAvailableObjectsBar,
                      Priority.ALWAYS);
    GridPane.setHgrow(lFailedRequestsBar, Priority.ALWAYS);

    add(lLiveObjectsLabel, 0, 0);
    add(lNumberLiveObjectsLabel, 1, 0);
    add(lFillFactorBarLiveObjectsBar, 2, 0);
    add(lLiveMemorySizeLabel, 3, 0);
    add(lClearLiveObjectsButton, 4, 0);

    add(lAvailableObjectsLabel, 0, 1);
    add(lNumberAvailableObjectsLabel, 1, 1);
    add(lFillFactorBarAvailableObjectsBar, 2, 1);
    add(lAvailableMemorySizeLabel, 3, 1);
    add(lClearAvailableObjectsButton, 4, 1);

    add(lFailedRequestsLabel, 0, 2);
    add(lNumberOfFailedRequestsLabel, 1, 2);
    add(lFailedRequestsBar, 2, 2);

    pRecycler.addListener((live, available, failed) -> {

      final double lLiveObjectsFillFactor =
                                          ((double) live) / lMaxLive;
      final double lAvailableObjectsFillFactor = ((double) available)
                                                 / lMaxAvailable;
      final double lFailedRequestsFillFactor = 1 - Math.exp(-failed
                                                            / 10.0);

      final double lTotalLiveMemoryInBytes =
                                           pRecycler.computeLiveMemorySizeInBytes();
      final double lTotalAvailableMemoryInBytes =
                                                pRecycler.computeAvailableMemorySizeInBytes();

      Platform.runLater(() -> {
        lNumberLiveObjectsLabel.textProperty()
                               .set(String.format("%d/%d ",
                                                  live,
                                                  lMaxLive));
        lNumberAvailableObjectsLabel.textProperty()
                                    .set(String.format("%d/%d ",
                                                       available,
                                                       lMaxAvailable));
        lNumberOfFailedRequestsLabel.textProperty()
                                    .set(String.format("%d", failed));

        lFillFactorBarLiveObjectsBar.progressProperty()
                                    .set(lLiveObjectsFillFactor);
        lFillFactorBarAvailableObjectsBar.progressProperty()
                                         .set(lAvailableObjectsFillFactor);
        lFailedRequestsBar.progressProperty()
                          .set(lFailedRequestsFillFactor);

        lLiveMemorySizeLabel.textProperty()
                            .set(MemorySizeFormat.format(lTotalLiveMemoryInBytes,
                                                         true));

        lAvailableMemorySizeLabel.textProperty()
                                 .set(MemorySizeFormat.format(lTotalAvailableMemoryInBytes,
                                                              true));
      });

    });

  }

  /**
   * Opens panel in separate window
   * 
   * @param pWindowTitle
   *          window
   * @param pRecycler
   *          recycler
   */
  public static void openPaneInWindow(String pWindowTitle,
                                      RecyclerInterface<?, ?> pRecycler)
  {
    try
    {
      final CountDownLatch lCountDownLatch = new CountDownLatch(1);
      SwingUtilities.invokeLater(new Runnable()
      {
        @Override
        public void run()
        {
          new JFXPanel(); // initializes JavaFX environment
          lCountDownLatch.countDown();
        }
      });
      lCountDownLatch.await();
    }
    catch (InterruptedException e)
    {
    }

    Platform.runLater(() -> {
      Stage lStage = new Stage();
      Group root = new Group();
      Scene scene = new Scene(root, cPrefWidth, cPrefHeight);
      lStage.setScene(scene);
      lStage.setTitle(pWindowTitle);

      RecyclerPanel lInstrumentedRecyclerPane =
                                              new RecyclerPanel(pRecycler);

      root.getChildren().add(lInstrumentedRecyclerPane);

      lStage.show();
    });

  }
}
