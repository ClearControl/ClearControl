package clearcontrol.gui.jfx.var.textfield;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;

/**
 * Text field that is synced to a Number
 *
 * @param <N>
 *          number
 * @author royer
 */
public class NumberVariableTextField<N extends Number> extends HBox
{

  private final Label mLabel;
  private final TextField mTextField;

  private Variable<N> mVariable;
  private Variable<N> mMin;
  private Variable<N> mMax;
  private Variable<N> mGranularity;

  private int mPrecision = 6;

  /**
   * Instantiates a number variable text field.
   * 
   * @param pTextFieldLabel
   *          text field label
   * @param pVariable
   *          variable to sync with
   * @param pMin
   *          min value
   * @param pMax
   *          max value
   * @param pGranularity
   *          granularity
   */
  public NumberVariableTextField(String pTextFieldLabel,
                                 Variable<N> pVariable,
                                 N pMin,
                                 N pMax,
                                 N pGranularity)
  {
    this(pTextFieldLabel,
         pVariable,
         new Variable<N>("min", pMin),
         new Variable<N>("max", pMax),
         new Variable<N>("granularity", pGranularity));

  }

  /**
   * Instantiates a number variable text field.
   * 
   * @param pTextFieldLabel
   *          text field label
   * @param pBoundedVariable
   *          bounded variable to sync with
   */
  public NumberVariableTextField(String pTextFieldLabel,
                                 BoundedVariable<N> pBoundedVariable)
  {
    this(pTextFieldLabel,
         pBoundedVariable,
         pBoundedVariable.getMinVariable(),
         pBoundedVariable.getMaxVariable(),
         pBoundedVariable.getGranularityVariable());
  }

  /**
   * Instantiates a number variable text field.
   * 
   * @param pTextFieldLabel
   *          text field
   * @param pVariable
   *          variable to sync with
   * @param pMin
   *          min
   * @param pMax
   *          max
   * @param pGranularity
   *          granularity
   */
  public NumberVariableTextField(String pTextFieldLabel,
                                 Variable<N> pVariable,
                                 Variable<N> pMin,
                                 Variable<N> pMax,
                                 N pGranularity)
  {
    this(pTextFieldLabel,
         pVariable,
         pMin,
         pMax,
         new Variable<N>("granularity", pGranularity));

  }

  /**
   * Instantiates a number variable text field.
   * 
   * @param pTextFieldLabel
   *          text field
   * @param pVariable
   *          variable
   * @param pMin
   *          min
   * @param pMax
   *          max
   * @param pGranularity
   *          granularity
   */
  public NumberVariableTextField(String pTextFieldLabel,
                                 Variable<N> pVariable,
                                 Variable<N> pMin,
                                 Variable<N> pMax,
                                 Variable<N> pGranularity)
  {
    super();
    mVariable = pVariable;
    mMin = pMin;
    mMax = pMax;
    mGranularity = pGranularity;

    setAlignment(Pos.CENTER);
    // setPadding(new Insets(10, 10, 10, 10));

    mLabel = new Label(pTextFieldLabel);
    mLabel.setAlignment(Pos.CENTER);

    mTextField = new TextField();
    mTextField.setAlignment(Pos.CENTER);

    getTextField().setPrefWidth(7 * 15);

    getTextField().textProperty().addListener((obs, o, n) -> {
      if (!o.equals(n))
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

    getChildren().add(getLabel());
    getChildren().add(getTextField());

    if (mMin.get() instanceof Double || mMin.get() instanceof Float)
    {
      setTextFieldDouble(pVariable.get());
    }
    if (mMin.get() instanceof Integer || mMin.get() instanceof Long)
    {
      setTextFieldLongValue(pVariable.get());
    }

    mVariable.addSetListener((o, n) -> {
      if (!n.equals(o) && n != null)
        Platform.runLater(() -> {
          if (n.equals(getTextFieldValue()))
            return;

          if (mMin.get() instanceof Double
              || mMin.get() instanceof Float)
            setTextFieldDouble(n);
          else
            setTextFieldLongValue(n);

        });
    });

    Platform.runLater(() -> {
      if (mMin.get() instanceof Double || mMin.get() instanceof Float)
        setTextFieldDouble(mVariable.get().doubleValue());
      else
        setTextFieldLongValue(mVariable.get().longValue());
    });

  }

  /**
   * Returns min variable
   * 
   * @return min variable
   */
  public Variable<N> getMinVariable()
  {
    return mMin;
  }

  /**
   * Returns max variable
   * 
   * @return max variable
   */
  public Variable<N> getMaxVariable()
  {
    return mMax;
  }

  /**
   * Returns granularity variable
   * 
   * @return granularity variable
   */
  public Variable<N> getGranularityVariable()
  {
    return mGranularity;
  }

  /**
   * Returns number format precision
   * 
   * @return number format precision
   */
  public int getNumberFormatPrecision()
  {
    return mPrecision;
  }

  /**
   * Sets number format precision
   * 
   * @param pPrecision
   *          number format precision
   */
  public void setNumberFormatPrecision(int pPrecision)
  {
    mPrecision = pPrecision;
  }

  private void setTextFieldValue(Number n)
  {
    if (mMin.get() instanceof Double || mMin.get() instanceof Float)
      setTextFieldDouble(n);

    else if (mMin.get() instanceof Integer
             || mMin.get() instanceof Long)
      setTextFieldLongValue(n);
  }

  @SuppressWarnings("unchecked")
  private void setVariableValue(Number pNewValue)
  {
    if (!mVariable.get().equals(pNewValue))
    {
      double lCorrectedValueDouble =
                                   correctValueDouble(pNewValue.doubleValue());
      long lCorrectedValueLong =
                               correctValueLong(pNewValue.longValue());
      if (mMin.get() instanceof Double)
        mVariable.setAsync((N) new Double(lCorrectedValueDouble));
      if (mMin.get() instanceof Float)
        mVariable.setAsync((N) new Float(lCorrectedValueDouble));
      if (mMin.get() instanceof Long)
        mVariable.setAsync((N) new Long(lCorrectedValueLong));
      if (mMin.get() instanceof Integer)
        mVariable.setAsync((N) new Integer((int) lCorrectedValueLong));
      if (mMin.get() instanceof Short)
        mVariable.setAsync((N) new Short((short) lCorrectedValueLong));
      if (mMin.get() instanceof Byte)
        mVariable.setAsync((N) new Byte((byte) lCorrectedValueLong));
    }
  }

  private double correctValueDouble(double pValue)
  {
    if (pValue < mMin.get().doubleValue())
      return mMin.get().doubleValue();
    if (pValue > mMax.get().doubleValue())
      return mMax.get().doubleValue();

    if (mGranularity.get() != null)
    {
      double lGranularity = mGranularity.get().doubleValue();

      if (lGranularity == 0)
        return pValue;

      double lCorrectedValue = lGranularity
                               * Math.round(pValue / lGranularity);

      return lCorrectedValue;
    }

    return pValue;
  }

  private long correctValueLong(long pValue)
  {
    if (pValue < mMin.get().longValue())
      return mMin.get().longValue();
    if (pValue > mMax.get().longValue())
      return mMax.get().longValue();

    if (mGranularity.get() != null)
    {
      long lGranularity = mGranularity.get().longValue();

      if (lGranularity == 0)
        return pValue;

      long lCorrectedValue =
                           lGranularity * Math.round(1.0 * pValue
                                                     / lGranularity);
      return lCorrectedValue;
    }

    return pValue;
  }

  private void setTextFieldDouble(Number pDoubleValue)
  {
    double lCorrectedValue =
                           correctValueDouble(pDoubleValue.doubleValue());

    String lString;

    if (mVariable.get() == null)
      lString = "null";
    else if (mVariable.get() instanceof Long
             || mVariable.get() instanceof Integer
             || mVariable.get() instanceof Short
             || mVariable.get() instanceof Byte)
      lString = String.format("%d", Math.round(lCorrectedValue));
    else
      lString = String.format("%." + getNumberFormatPrecision()
                              + "g",
                              lCorrectedValue);

    getTextField().setText(lString);
    getTextField().setStyle("-fx-text-fill: black");
  }

  private void setTextFieldLongValue(Number n)
  {
    getTextField().setText(String.format("%d", n.longValue()));
    getTextField().setStyle("-fx-text-fill: black");
  }

  private void setUpdatedTextField()
  {
    getTextField().setStyle("-fx-text-fill: orange");
  }

  private void setVariableValueFromTextField()
  {
    try
    {
      double lCorrectedValue = getTextFieldValue();
      setVariableValue(lCorrectedValue);
      setTextFieldValue(lCorrectedValue);
      getTextField().setStyle("-fx-text-fill: black");
    }
    catch (NumberFormatException e)
    {
      getTextField().setStyle("-fx-text-fill: red");
      // e.printStackTrace();
    }

  }

  private double getTextFieldValue()
  {
    Double lDoubleValue = Double.parseDouble(mTextField.getText());

    double lCorrectedValue = correctValueDouble(lDoubleValue);
    return lCorrectedValue;
  }

  /**
   * Returns the label
   * 
   * @return label
   */
  public Label getLabel()
  {
    return mLabel;
  }

  /**
   * Returns the text field
   * 
   * @return text field
   */
  public TextField getTextField()
  {
    return mTextField;
  }

}
