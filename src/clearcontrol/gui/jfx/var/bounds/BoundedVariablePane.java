package clearcontrol.gui.jfx.var.bounds;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;

/**
 * Univariate function pane
 *
 * @author royer
 */
public class BoundedVariablePane extends CustomGridPane
{

  private final Label mLabel;
  private final NumberVariableTextField<Number> mValueTextField,
      mMinTextField, mMaxTextField, mGranularityTextField;

  /**
   * Instantiates a univariate function pane
   * 
   * @param pLabelText
   *          label text
   * @param pBoundedVariable
   *          bounded variable
   */
  public BoundedVariablePane(String pLabelText,
                             BoundedVariable<Number> pBoundedVariable)
  {
    super();

    setMaxWidth(Double.MAX_VALUE);
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(0);
    setPadding(new Insets(0, 10, 0, 10));

    if (pLabelText != null)
      mLabel = new Label(pLabelText);
    else
      mLabel = null;

    mValueTextField = new NumberVariableTextField<Number>("value:",
                                                          pBoundedVariable,
                                                          Double.NEGATIVE_INFINITY,
                                                          Double.POSITIVE_INFINITY,
                                                          0.0);

    mMinTextField = new NumberVariableTextField<Number>("min:",
                                                        pBoundedVariable.getMinVariable(),
                                                        Double.NEGATIVE_INFINITY,
                                                        Double.POSITIVE_INFINITY,
                                                        0.0);

    mMaxTextField = new NumberVariableTextField<Number>("max:",
                                                        pBoundedVariable.getMaxVariable(),
                                                        Double.NEGATIVE_INFINITY,
                                                        Double.POSITIVE_INFINITY,
                                                        0.0);

    mGranularityTextField =
                          new NumberVariableTextField<Number>("granularity:",
                                                              pBoundedVariable.getGranularityVariable(),
                                                              Double.NEGATIVE_INFINITY,
                                                              Double.POSITIVE_INFINITY,
                                                              0.0);

    int lRow = 0;
    if (mLabel != null)
      add(getLabel(), lRow++, 0);
    add(getValueTextField().getLabel(), lRow++, 0);
    add(getValueTextField().getTextField(), lRow++, 0);
    add(getMinTextField().getLabel(), lRow++, 0);
    add(getMinTextField().getTextField(), lRow++, 0);
    add(getMaxTextField().getLabel(), lRow++, 0);
    add(getMaxTextField().getTextField(), lRow++, 0);
    add(getGranularityTextField().getLabel(), lRow++, 0);
    add(getGranularityTextField().getTextField(), lRow++, 0);

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
   * Returns value text field
   * 
   * @return value text field
   */
  public NumberVariableTextField<Number> getValueTextField()
  {
    return mValueTextField;
  }

  /**
   * Returns min text field
   * 
   * @return min text field
   */
  public NumberVariableTextField<Number> getMinTextField()
  {
    return mMinTextField;
  }

  /**
   * Returns max text field
   * 
   * @return max text field
   */
  public NumberVariableTextField<Number> getMaxTextField()
  {
    return mMaxTextField;
  }

  /**
   * Returns granularity text field
   * 
   * @return granularity text field
   */
  public NumberVariableTextField<Number> getGranularityTextField()
  {
    return mGranularityTextField;
  }

}
