package clearcontrol.devices.stages.gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.StageType;

/**
 * Stage 3D Control
 */
public class StageDevicePanel extends ScrollPane
{

  private static final double cPrefWidth = 300;
  private static final double cPrefHeight = 300;

  private StageDeviceInterface mStageDeviceInterface;

  enum Stage
  {
   R, X, Y, Z
  }

  /**
   * Instantiates generic stage device panel
   * 
   * @param pStageDeviceInterface
   *          stage device
   */
  public StageDevicePanel(StageDeviceInterface pStageDeviceInterface)
  {
    mStageDeviceInterface = pStageDeviceInterface;

    setMaxWidth(Double.MAX_VALUE);
    setPrefHeight(StageDevicePanel.cPrefHeight * 1.5);

    setVbarPolicy(ScrollBarPolicy.ALWAYS);
    setHbarPolicy(ScrollBarPolicy.NEVER);
    setFitToWidth(true);
    setVmax(StageDevicePanel.cPrefHeight * 1.5);

    if (mStageDeviceInterface.getStageType() == StageType.XYZR)
      setContent(createXYZRControls());
    else
      setContent(createGenericControls());

  }

  private VBox createGenericControls()
  {
    VBox lStageDOFsPanel = new VBox(10);
    lStageDOFsPanel.setMaxWidth(Double.MAX_VALUE);

    int lNumberOfDOFs = mStageDeviceInterface.getNumberOfDOFs();

    for (int i = 0; i < lNumberOfDOFs; i++)
    {
      StageDOFPanel lDOFPanel =
                              new StageDOFPanel(mStageDeviceInterface,
                                                i,
                                                null);
      VBox.setVgrow(lDOFPanel, Priority.ALWAYS);
      lStageDOFsPanel.getChildren().add(lDOFPanel);
    }

    lStageDOFsPanel.setPadding(new Insets(10));

    return lStageDOFsPanel;
  }

  private VBox createXYZRControls()
  {

    Node lStageControlX = createStageControl(Stage.X,
                                             "Stage X (microns)");
    Node lStageControlY = createStageControl(Stage.Y,
                                             "Stage Y (microns)");
    Node lStageControlZ = createStageControl(Stage.Z,
                                             "Stage Z (microns)");
    Node lStageControlR =
                        createStageControl(Stage.R,
                                           "Stage R (micro-degree)");

    VBox lStageDOFsPanel = new VBox(10,
                                    lStageControlX,
                                    lStageControlY,
                                    lStageControlZ,
                                    lStageControlR);
    lStageDOFsPanel.setMaxWidth(Double.MAX_VALUE);
    lStageDOFsPanel.setPadding(new Insets(10));
    return lStageDOFsPanel;
  }

  private Node createStageControl(Stage pStage, String pLabelString)
  {

    int lDOFIndex =
                  mStageDeviceInterface.getDOFIndexByName(pStage.name());

    if (lDOFIndex < 0)
      return new HBox();

    Node lStageDOFPanel = new StageDOFPanel(mStageDeviceInterface,
                                            lDOFIndex,
                                            pLabelString);

    return lStageDOFPanel;
  }

}
