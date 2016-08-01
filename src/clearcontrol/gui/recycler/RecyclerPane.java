package clearcontrol.gui.recycler;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import coremem.recycling.RecyclableInterface;
import coremem.recycling.RecyclerInterface;
import coremem.recycling.RecyclerRequestInterface;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.Stage;

public class RecyclerPane extends StandardGridPane
{

	public RecyclerPane(RecyclerInterface<?, ?> pRecycler)
	{
		super(0, StandardGridPane.cStandardGap);

		Label lLiveObjectsLabel = new Label("Live Objects: ");
		Label lAvailableObjectsLabel = new Label("Available Objects: ");
		Label lFailedRequestsLabel = new Label("Failed Requests: ");

		int lMaxLive = pRecycler.getMaxNumberOfAvailableObjects();
		int lMaxAvailable = pRecycler.getMaxNumberOfAvailableObjects();
		long lFailedRequests = pRecycler.getNumberOfFailedRequests();

		Label lNumberLiveObjectsLabel = new Label(String.format("%d/%d ",
																														0,
																														lMaxLive));
		Label lNumberAvailableObjectsLabel = new Label(String.format(	"%d/%d ",
																																	0,
																																	lMaxAvailable));

		Label lNumberOfFailedRequestsLabel = new Label(String.format(	"%d ",
																																	0,
																																	lFailedRequests));

		ProgressBar lFillFactorBarLiveObjectsBar = new ProgressBar(0);
		ProgressBar lFillFactorBarAvailableObjectsBar = new ProgressBar(0);
		ProgressBar lFailedRequestsBar = new ProgressBar(0);
		lFailedRequestsBar.setStyle("-fx-accent: red");

		// lLiveObjectsLabel.setFont(new Font(16.0));
		// lAvailableObjectsLabel.setFont(new Font(16.0));

		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints(70);
		ColumnConstraints col3 = new ColumnConstraints();
		getColumnConstraints().addAll(col1, col2, col3);

		add(lLiveObjectsLabel, 0, 0);
		add(lNumberLiveObjectsLabel, 1, 0);
		add(lFillFactorBarLiveObjectsBar, 2, 0);

		add(lAvailableObjectsLabel, 0, 1);
		add(lNumberAvailableObjectsLabel, 1, 1);
		add(lFillFactorBarAvailableObjectsBar, 2, 1);

		add(lFailedRequestsLabel, 0, 2);
		add(lNumberOfFailedRequestsLabel, 1, 2);
		add(lFailedRequestsBar, 2, 2);

		pRecycler.addListener((live, available, failed) -> {

			double lLiveObjectsFillFactor = ((double) live) / lMaxLive;
			double lAvailableObjectsFillFactor = ((double) available) / lMaxAvailable;
			double lFailedRequestsFillFactor = 1 - Math.exp(-failed / 10.0);

			Platform.runLater(() -> {
				lNumberLiveObjectsLabel.textProperty()
																.set(String.format(	"%d/%d ",
																										live,
																										lMaxLive));
				lNumberAvailableObjectsLabel.textProperty()
																		.set(String.format(	"%d/%d ",
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
			});

		});

	}

	public static void openPaneInWindow(String pWindowTitle,
																			RecyclerInterface<?, ?> pRecycler)
	{
		try
		{
			final CountDownLatch lCountDownLatch = new CountDownLatch(1);
			SwingUtilities.invokeLater(new Runnable()
			{
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
			Scene scene = new Scene(root, 600, 100);
			lStage.setScene(scene);
			lStage.setTitle(pWindowTitle);

			RecyclerPane lInstrumentedRecyclerPane = new RecyclerPane(pRecycler);

			root.getChildren().add(lInstrumentedRecyclerPane);

			lStage.show();
		});

	}
}
