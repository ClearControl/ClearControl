package clearcontrol.gui.jfx.custom.gridpane;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import clearcontrol.gui.jfx.var.textfield.StringVariableTextField;

/**
 * Custom grid pane
 *
 * @author royer
 */
public class CustomGridPane extends GridPane
{
  protected int mRow;

  /**
   * Standard custom grid pane gap
   */
  public static final int cStandardGap = 5;
  /**
   * Standard custom grid pane padding
   */
  public static final int cStandardPadding = 10;

  /**
   * Instanciates a custom grid pane
   */
  public CustomGridPane()
  {
    this(cStandardPadding, cStandardGap);
  }

  /**
   * Instanciates a custom grid pane with given padding and gaps
   * 
   * @param pPadding
   *          padding
   * @param pGaps
   *          gaps
   */
  public CustomGridPane(int pPadding, int pGaps)
  {
    super();
    setAlignment(Pos.CENTER);
    setGap(pGaps);
    setPadding(pPadding);
  }

  /**
   * Sets pading
   * 
   * @param pPadding
   *          padding
   */
  public void setPadding(double pPadding)
  {
    setPadding(new Insets(pPadding, pPadding, pPadding, pPadding));
  }

  /**
   * Sets gap
   * 
   * @param pGap
   *          gap
   */
  public void setGap(double pGap)
  {
    setHgap(pGap);
    setVgap(pGap);
  }

  public void addSeparator()
  {
    {
      Separator lSeparator = new Separator();
      lSeparator.setOrientation(Orientation.HORIZONTAL);
      GridPane.setColumnSpan(lSeparator, 4);
      add(lSeparator, 0, mRow);
      mRow++;
    }
  }

  public int getLastUsedRow()
  {
    return mRow;
  }

  @Override
  public void add(Node child, int columnIndex, int rowIndex)
  {
    super.add(child, columnIndex, rowIndex);
    if (rowIndex > mRow)
    {
      mRow = rowIndex;
    }
  }

  public void addIntegerField(BoundedVariable<Integer> variable,
                              int pRow)
  {
    NumberVariableTextField<Integer> lField =
                                            new NumberVariableTextField<Integer>(variable.getName(),
                                                                                 variable,
                                                                                 variable.getMin(),
                                                                                 variable.getMax(),
                                                                                 variable.getGranularity());
    this.add(lField.getLabel(), 0, pRow);
    this.add(lField.getTextField(), 1, pRow);

  }

  public void addDoubleField(BoundedVariable<Double> variable,
                             int pRow)
  {
    NumberVariableTextField<Double> lField =
                                           new NumberVariableTextField<Double>(variable.getName(),
                                                                               variable,
                                                                               variable.getMin(),
                                                                               variable.getMax(),
                                                                               variable.getGranularity());
    this.add(lField.getLabel(), 0, pRow);
    this.add(lField.getTextField(), 1, pRow);
  }

  public void addCheckbox(Variable<Boolean> pBooleanVariable,
                          int pRow)
  {
    VariableCheckBox lCheckBox =
                               new VariableCheckBox("",
                                                    pBooleanVariable);

    Label lLabel = new Label(pBooleanVariable.getName());

    GridPane.setHalignment(lCheckBox.getCheckBox(), HPos.RIGHT);
    GridPane.setColumnSpan(lCheckBox.getCheckBox(), 1);

    add(lLabel, 0, pRow);
    add(lCheckBox.getCheckBox(), 1, pRow);
  }

  public void addStringField(Variable<String> pStringVariable,
                             int pRow)
  {
    StringVariableTextField lTextField =
                                       new StringVariableTextField(pStringVariable.getName(),
                                                                   pStringVariable);

    add(lTextField.getLabel(), 0, pRow);
    add(lTextField.getTextField(), 1, pRow);
  }
}
