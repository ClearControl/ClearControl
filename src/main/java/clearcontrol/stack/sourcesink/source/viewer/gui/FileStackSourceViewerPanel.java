package clearcontrol.stack.sourcesink.source.viewer.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import clearcontrol.gui.jfx.var.file.VariableFileChooser;
import clearcontrol.gui.jfx.var.textfield.StringVariableTextField;
import clearcontrol.stack.sourcesink.source.viewer.FileStackSourceViewer;

/**
 * Laser device GUI panel
 *
 * @author royer
 */
public class FileStackSourceViewerPanel extends StackSourceViewerPanel
{

  private VariableFileChooser mVariableFileChooser;
  private StringVariableTextField mDatasetNameTextField;

  /**
   * Instantiates a stack source viewer panel
   * 
   * @param pFileStackSourceViewer
   *          stack source viewer
   */
  public FileStackSourceViewerPanel(FileStackSourceViewer pFileStackSourceViewer)
  {
    super();

    super.init(pFileStackSourceViewer);

    mDatasetNameTextField =
                          new StringVariableTextField("name: ",
                                                      pFileStackSourceViewer.getDatasetNameVariable());

    mVariableFileChooser = new VariableFileChooser("Root folder: ",
                                                   pFileStackSourceViewer.getRootFolderVariable(),
                                                   true);

    add(mVariableFileChooser.getLabel(), 0, 0);
    add(mVariableFileChooser.getTextField(), 1, 0);
    add(mVariableFileChooser.getButton(), 2, 0);

    GridPane.setHgrow(mDatasetNameTextField.getTextField(),
                      Priority.SOMETIMES);
    add(mDatasetNameTextField.getLabel(), 0, 1);
    add(mDatasetNameTextField.getTextField(), 1, 1);

    GridPane.setHgrow(mChannelComboBox, Priority.ALWAYS);
    add(new Label("Channel: "), 0, 2);
    add(mChannelComboBox, 1, 2);

    mIndexSlider.setMaxWidth(Double.POSITIVE_INFINITY);
    GridPane.setHgrow(mIndexSlider, Priority.ALWAYS);
    GridPane.setColumnSpan(mIndexSlider, 3);
    add(mIndexTextField.getTextField(), 0, 3);
    add(mIndexSlider, 1, 3);

  }

}
