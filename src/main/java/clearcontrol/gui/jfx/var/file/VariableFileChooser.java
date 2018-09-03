package clearcontrol.gui.jfx.var.file;

import java.io.File;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;

/**
 * File chooser that syncs the file to a variable
 *
 * @author royer
 */
public class VariableFileChooser extends HBox
                                 implements LoggingFeature
{

  private final Variable<File> mFileVariable;

  private final Label mLabel;
  private final TextField mFileTextField;
  private final Button mChooseFileButton;

  /**
   * Instantiates a variable file chooser
   * 
   * @param pLabelString
   *          label string
   * @param pFileVariable
   *          file variable
   * @param pOnlyFolders
   *          true -> choose only folders
   */
  public VariableFileChooser(String pLabelString,
                             Variable<File> pFileVariable,
                             boolean pOnlyFolders)
  {
    super();
    mFileVariable = pFileVariable;

    setAlignment(Pos.CENTER_LEFT);

    mLabel = new Label(pLabelString);

    File lFile = pFileVariable.get();

    mFileTextField = new TextField(getTextFieldFromFile(lFile));

    mChooseFileButton = new Button("Browse");

    this.getChildren().addAll(mLabel,
                              mFileTextField,
                              mChooseFileButton);

    getTextField().textProperty().addListener((obs, o, n) -> {
      if (o != null && !o.equals(n))
        setUpdatedTextField();
    });

    getTextField().focusedProperty().addListener((obs, o, n) -> {
      if (!n)
        setVariableValueFromTextField();
    });

    getTextField().setOnKeyPressed((e) -> {
      if (e.getCode().equals(KeyCode.ENTER))
        setVariableValueFromTextField();
    });

    if (pOnlyFolders)
    {
      final DirectoryChooser lFolderChooser = new DirectoryChooser();
      lFolderChooser.setTitle("Choose Folder");

      mChooseFileButton.setOnAction((e) -> {
        if (lFile != null)
          lFolderChooser.setInitialDirectory(lFile);
        setFile(lFolderChooser.showDialog(null));
        getTextField().setStyle("-fx-text-fill: black");
      });
    }
    else
    {
      final FileChooser lFileChooser = new FileChooser();
      lFileChooser.setTitle("Choose File");

      mChooseFileButton.setOnAction((e) -> {
        if (lFile != null)
          lFileChooser.setInitialDirectory(lFile);
        setFile(lFileChooser.showOpenDialog(null));
        getTextField().setStyle("-fx-text-fill: black");
      });
    }
  }

  /**
   * Returns label
   * 
   * @return label
   */
  public Label getLabel()
  {
    return mLabel;
  }

  /**
   * Returns textfield
   * 
   * @return textfield
   */
  public TextField getTextField()
  {
    return mFileTextField;
  }

  /**
   * Returns button
   * 
   * @return button
   */
  public Button getButton()
  {
    return mChooseFileButton;
  }

  private void setUpdatedTextField()
  {
    getTextField().setStyle("-fx-text-fill: orange");
  }

  private void setVariableValueFromTextField()
  {
    setFile(new File(mFileTextField.getText()));
  }

  protected String getTextFieldFromFile(File pFile)
  {
    try
    {
      return pFile == null ? "" : pFile.getCanonicalPath();
    }
    catch (Exception e)
    {
      warning("Problem while obtaining the canonical path for file '%s': %s",
              pFile,
              e);
      return "";
    }
  }

  private void setFile(File pFile)
  {
    if (pFile != null)
      Platform.runLater(() -> {
        mFileTextField.setText(getTextFieldFromFile(pFile));
        if (pFile.exists())
          getTextField().setStyle("-fx-text-fill: black");
        else
          getTextField().setStyle("-fx-text-fill: red");
        mFileVariable.setAsync(pFile);

      });
  }

}
