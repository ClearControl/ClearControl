package clearcontrol.gui.jfx.var.textfield;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

public class VariableNumberTextField<T extends Number> extends HBox
{

  private final Label mLabel;
  private final TextField mTextField;

  private Variable<T> mVariable;
  private Variable<T> mMin;
  private Variable<T> mMax;
  private Variable<T> mGranularity;

  public VariableNumberTextField(String pSliderName,
                                 Variable<T> pVariable,
                                 T pMin,
                                 T pMax,
                                 T pGranularity)
  {
    this(pSliderName,
         pVariable,
         new Variable<T>("min", pMin),
         new Variable<T>("max", pMax),
         new Variable<T>("granularity", pGranularity));

  }

  public VariableNumberTextField(String pSliderName,
                                 BoundedVariable<T> pBoundedVariable)
  {
    this(pSliderName,
         pBoundedVariable,
         pBoundedVariable.getMinVariable(),
         pBoundedVariable.getMaxVariable(),
         pBoundedVariable.getGranularityVariable());
  }

  public VariableNumberTextField(String pSliderName,
                                 Variable<T> pVariable,
                                 Variable<T> pMin,
                                 Variable<T> pMax,
                                 T pGranularity)
  {
    this(pSliderName,
         pVariable,
         pMin,
         pMax,
         new Variable<T>("granularity", pGranularity));

  }

  public VariableNumberTextField(String pSliderName,
                                 Variable<T> pVariable,
                                 Variable<T> pMin,
                                 Variable<T> pMax,
                                 Variable<T> pGranularity)
  {
    super();
    mVariable = pVariable;
    mMin = pMin;
    mMax = pMax;
    mGranularity = pGranularity;

    setAlignment(Pos.CENTER);
    setPadding(new Insets(25, 25, 25, 25));

    mLabel = new Label(pSliderName);
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

    if (pMin.get() instanceof Double || pMin.get() instanceof Float)
    {
      setTextFieldDouble(pVariable.get());
    }
    if (pMin.get() instanceof Integer || pMin.get() instanceof Long)
    {
      setTextFieldLongValue(pVariable.get());
    }

    mVariable.addSetListener((o, n) -> {
      if (!n.equals(o) && n != null)
        Platform.runLater(() -> {
          if (n.equals(getTextFieldValue()))
            return;

          if (pMin.get() instanceof Double
              || pMin.get() instanceof Float)
            setTextFieldDouble(n);
          else
            setTextFieldLongValue(n);

        });
    });

    Platform.runLater(() -> {
      if (pMin.get() instanceof Double || pMin.get() instanceof Float)
        setTextFieldDouble(mVariable.get().doubleValue());
      else
        setTextFieldLongValue(mVariable.get().longValue());
    });

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
        mVariable.setAsync((T) new Double(lCorrectedValueDouble));
      if (mMin.get() instanceof Float)
        mVariable.setAsync((T) new Float(lCorrectedValueDouble));
      if (mMin.get() instanceof Long)
        mVariable.setAsync((T) new Long(lCorrectedValueLong));
      if (mMin.get() instanceof Integer)
        mVariable.setAsync((T) new Integer((int) lCorrectedValueLong));
      if (mMin.get() instanceof Short)
        mVariable.setAsync((T) new Short((short) lCorrectedValueLong));
      if (mMin.get() instanceof Byte)
        mVariable.setAsync((T) new Byte((byte) lCorrectedValueLong));
    }
  }

  @SuppressWarnings("unchecked")
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

  @SuppressWarnings("unchecked")
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
    getTextField().setText(String.format("%.3f", lCorrectedValue));
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

  public Label getLabel()
  {
    return mLabel;
  }

  public TextField getTextField()
  {
    return mTextField;
  }

}
