package clearcontrol.devices.stages.gui;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.StageType;
import clearcontrol.gui.jfx.other.recycler.RecyclerPanel;

/**
 * Stage 3D Control
 */
public class StageDevicePanel extends ScrollPane
{

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

    setPrefSize(RecyclerPanel.cPrefWidth,
                RecyclerPanel.cPrefHeight * 1.5);
    setVbarPolicy(ScrollBarPolicy.ALWAYS);
    setVmax(RecyclerPanel.cPrefHeight * 1.5);
    VBox.setVgrow(this, Priority.ALWAYS);

    if (mStageDeviceInterface.getStageType() == StageType.XYZR)
      setContent(createXYZRControls());
    else
      setContent(createGenericControls());

  }

  private VBox createGenericControls()
  {
    VBox lStageDOFsPanel = new VBox(10);

    int lNumberOfDOFs = mStageDeviceInterface.getNumberOfDOFs();

    for (int i = 0; i < lNumberOfDOFs; i++)
    {
      StageDOFPanel lDOFPanel =
                              new StageDOFPanel(mStageDeviceInterface,
                                                i,
                                                null);
      lStageDOFsPanel.getChildren().add(lDOFPanel);
    }

    lStageDOFsPanel.setPadding(new Insets(10));

    return lStageDOFsPanel;
  }

  private VBox createXYZRControls()
  {

    VBox lStageDOFsPanel =
                         new VBox(10,
                                  createStageControl(Stage.X,
                                                     "Stage X (microns)"),
                                  createStageControl(Stage.Y,
                                                     "Stage Y (microns)"),
                                  createStageControl(Stage.Z,
                                                     "Stage Z (microns)"),
                                  createStageControl(Stage.R,
                                                     "Stage R (micro-degree)"));

    lStageDOFsPanel.setPadding(new Insets(10));
    return lStageDOFsPanel;
  }

  private StageDOFPanel createStageControl(Stage pStage,
                                           String pLabelString)
  {

    int lDOFIndex =
                  mStageDeviceInterface.getDOFIndexByName(pStage.name());

    StageDOFPanel lStageDOFPanel =
                                 new StageDOFPanel(mStageDeviceInterface,
                                                   lDOFIndex,
                                                   pLabelString);

    return lStageDOFPanel;
  }

}
