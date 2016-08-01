package clearcontrol.microscope.stacks.gui;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.gui.jfx.customvarpanel.CustomVariablePane;
import clearcontrol.gui.recycler.RecyclerPane;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class StackRecyclerManagerPanel extends VBox
{

	public StackRecyclerManagerPanel(StackRecyclerManager pStackRecyclerManager)
	{
		super();

		pStackRecyclerManager.addListener((map) -> {
			updateRecyclerPanels(map);
		});

	}

	private void updateRecyclerPanels(ConcurrentHashMap<String, RecyclerInterface<StackInterface, StackRequest>> pMap)
	{
		StackRecyclerManagerPanel lStackRecyclerManagerPanel = this;

		Platform.runLater(() -> {

			lStackRecyclerManagerPanel.getChildren().clear();

			Set<Entry<String, RecyclerInterface<StackInterface, StackRequest>>> lEntrySet = pMap.entrySet();

			for (Entry<String, RecyclerInterface<StackInterface, StackRequest>> lEntry : lEntrySet)
			{
				RecyclerInterface<StackInterface, StackRequest> lRecycler = lEntry.getValue();

				RecyclerPane lRecyclerPane = new RecyclerPane(lRecycler);
				lStackRecyclerManagerPanel.getChildren().add(lRecyclerPane);
			}

		});

	}

}
