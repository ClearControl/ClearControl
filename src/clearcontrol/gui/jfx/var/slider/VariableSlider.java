package clearcontrol.gui.jfx.var.slider;

import static java.lang.Math.abs;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.var.slider.customslider.Slider;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Slider that syncs its value to a variable
 *
 * @param <T>
 *          number type
 * @author royer
 */
public class VariableSlider<T extends Number> extends HBox
{

  private final Label mLabel;
  private final Slider mSlider;
  private final TextField mTextField;

  private Variable<T> mVariable;
  private Variable<T> mMin;
  private Variable<T> mMax;
  private Variable<T> mGranularity;
  private boolean mUpdateIfChanging = false;
  private double mTicks;

  /**
   * Instantiates a variable slider
   * 
   * @param pSliderLabelText
   *          slider label text
   * @param pVariable
   *          variable
   * @param pMin
   *          min
   * @param pMax
   *          max
   * @param pGranularity
   *          granularity
   * @param pTicks
   *          number of major ticks (if set to null the best tick value will be
   *          determined)
   */
  public VariableSlider(String pSliderLabelText,
                        Variable<T> pVariable,
                        T pMin,
                        T pMax,
                        T pGranularity,
                        T pTicks)
  {
    this(pSliderLabelText,
         pVariable,
         new Variable<T>("min", pMin),
         new Variable<T>("max", pMax),
         new Variable<T>("granularity", pGranularity),
         pTicks);

  }

  /**
   * Instantiates a variable slider
   * 
   * @param pSliderLabelText
   *          Slider label text
   * @param pBoundedVariable
   *          bounded variable
   * @param pTicks
   *          number of major ticks (if set to null the best tick value will be
   *          determined)
   */
  public VariableSlider(String pSliderLabelText,
                        BoundedVariable<T> pBoundedVariable,
                        T pTicks)
  {
    this(pSliderLabelText,
         pBoundedVariable,
         pBoundedVariable.getMinVariable(),
         pBoundedVariable.getMaxVariable(),
         pBoundedVariable.getGranularityVariable(),
         pTicks);
  }

  /**
   * Instantiates a variable slider
   * 
   * @param pSliderLabelText
   *          Slider label text
   * @param pVariable
   *          variable
   * @param pMin
   *          min variable
   * @param pMax
   *          max variable
   * @param pGranularity
   *          granularity variable
   * @param pTicks
   *          number of major ticks (if set to null the best tick value will be
   *          determined)
   */
  public VariableSlider(String pSliderLabelText,
                        Variable<T> pVariable,
                        Variable<T> pMin,
                        Variable<T> pMax,
                        Variable<T> pGranularity,
                        T pTicks)
  {
    super();
    mVariable = pVariable;
    mMin = pMin;
    mMax = pMax;
    mGranularity = pGranularity;

    if (pTicks == null)
    {
      mTicks =
             abs(mMax.get().doubleValue() - mMin.get().doubleValue())
               / 10;
    }
    else
      mTicks = pTicks.doubleValue();

    setMaxWidth(Double.MAX_VALUE);
    setAlignment(Pos.CENTER);
    setPadding(new Insets(10, 10, 10, 10));

    mLabel = new Label(pSliderLabelText);
    mLabel.setAlignment(Pos.CENTER);

    mSlider = new Slider();
    mSlider.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(mSlider, Priority.ALWAYS);
    HBox.setHgrow(mSlider, Priority.ALWAYS);

    mTextField = new TextField();
    mTextField.setAlignment(Pos.CENTER);
    mTextField.setPrefWidth(7 * 15);

    getChildren().add(getLabel());
    getChildren().add(getSlider());
    getChildren().add(getTextField());

    updateSliderMinMax(pMin, pMax);

    getSlider().setMajorTickUnit(mTicks);
    getSlider().setMinorTickCount(10);
    getSlider().setShowTickMarks(false);
    getSlider().setShowTickLabels(true);
    if (pGranularity != null && pGranularity.get() != null)
      getSlider().setBlockIncrement(pGranularity.get().doubleValue());

    pMin.addSetListener((o, n) -> {
      if (!o.equals(n) && n != null)
        Platform.runLater(() -> {
          updateSliderMinMax(pMin, pMax);
        });
    });

    pMax.addSetListener((o, n) -> {
      if (!o.equals(n) && n != null)
        Platform.runLater(() -> {
          updateSliderMinMax(pMin, pMax);
        });
    });

    Platform.runLater(() -> {
      updateSliderMinMax(pMin, pMax);
    });

    getTextField().textProperty().addListener((obs, o, n) -> {
      if (!o.equals(n))
        setUpdatedTextField();
    });

    getTextField().focusedProperty().addListener((obs, o, n) -> {
      if (!n)
      {
        setTextFieldValue(getTextFieldValue());
        setSliderValueFromTextField();
        setVariableValue(getSlider().getValue());
      }
    });

    getTextField().setOnKeyPressed((e) -> {
      if (e.getCode().equals(KeyCode.ENTER))
      {
        setTextFieldValue(getTextFieldValue());
        setSliderValueFromTextField();
        setVariableValue(getSlider().getValue());
      }
      ;
    });

    if (mVariable.get() instanceof Double
        || mVariable.get() instanceof Float)
    {
      setTextFieldDouble(mVariable.get());
    }
    if (mVariable.get() instanceof Integer
        || mVariable.get() instanceof Long)
    {
      setTextFieldLongValue(mVariable.get());
    }

    getSlider().setOnMouseDragged((e) -> {
      double lCorrectedSliderValue =
                                   correctValueDouble(getSlider().getValue());
      setTextFieldValue(lCorrectedSliderValue);

      if (!isUpdateIfChanging() && getSlider().isValueChanging())
        return;

      if (lCorrectedSliderValue != mVariable.get().doubleValue())
        setVariableValue(lCorrectedSliderValue);
    });

    getSlider().valueChangingProperty().addListener((obs, o, n) -> {
      if (isUpdateIfChanging())
        return;
      if (o && !n)
        setVariableValue(getSlider().getValue());
    });

    mVariable.addSetListener((o, n) -> {
      if (n != null && !n.equals(o))
        Platform.runLater(() -> {
          if (n.equals(getSlider().getValue())
              && n.equals(getTextFieldValue()))
          {
            // System.out.println("rejected");
            return;
          }

          if (mVariable.get() instanceof Double
              || mVariable.get() instanceof Float)
            setTextFieldDouble(n);
          else
            setTextFieldLongValue(n);

          setSliderValueFromTextField();

        });
    });

    Platform.runLater(() -> {
      setSliderValue(mVariable.get().doubleValue());

      if (mVariable.get() instanceof Double
          || mVariable.get() instanceof Float)
        setTextFieldDouble(mVariable.get().doubleValue());
      else
        setTextFieldLongValue(mVariable.get().longValue());
    });

  }

  private void setTextFieldValue(Number n)
  {
    if (mVariable.get() instanceof Double
        || mVariable.get() instanceof Float)
      setTextFieldDouble(n);

    else if (mVariable.get() instanceof Integer
             || mVariable.get() instanceof Long)
      setTextFieldLongValue(n);
  }

  private void updateSliderMinMax(Variable<T> pMin, Variable<T> pMax)
  {

    if (!Double.isInfinite(mMin.get().doubleValue())
        && !Double.isInfinite(mMax.get().doubleValue())
        && mMin.get().doubleValue() != mMax.get().doubleValue())
    {
      mTicks =
             abs(mMax.get().doubleValue() - mMin.get().doubleValue())
               / 10;

      getSlider().setMajorTickUnit(mTicks);
    }

    if (Double.isInfinite(mMin.get().doubleValue())
        || Double.isNaN(mMin.get().doubleValue()))
      getSlider().setMin(-1717);
    else
      getSlider().setMin(mMin.get().doubleValue());

    if (Double.isInfinite(mMax.get().doubleValue())
        || Double.isNaN(mMax.get().doubleValue()))
      getSlider().setMax(1717);
    else
      getSlider().setMax(mMax.get().doubleValue());
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
      if (mVariable.get() instanceof Double)
        mVariable.set((T) new Double(lCorrectedValueDouble));
      else if (mVariable.get() instanceof Float)
        mVariable.set((T) new Float(lCorrectedValueDouble));
      else if (mVariable.get() instanceof Long)
        mVariable.set((T) new Long(lCorrectedValueLong));
      else if (mVariable.get() instanceof Integer)
        mVariable.set((T) new Integer((int) lCorrectedValueLong));
      else if (mVariable.get() instanceof Short)
        mVariable.set((T) new Short((short) lCorrectedValueLong));
      else if (mVariable.get() instanceof Byte)
        mVariable.set((T) new Byte((byte) lCorrectedValueLong));
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
    getTextField().setText(String.format("%.3f", lCorrectedValue));
    getTextField().setStyle("-fx-text-fill: black");
  }

  private void setTextFieldLongValue(Number n)
  {
    getTextField().setText(String.format("%d", n.longValue()));
    getTextField().setStyle("-fx-text-fill: black");
  }

  private void setSliderValueFromTextField()
  {
    try
    {
      double lCorrectedValue = getTextFieldValue();
      mSlider.setValue(lCorrectedValue);
      getTextField().setStyle("-fx-text-fill: black");
    }
    catch (NumberFormatException e)
    {
      getTextField().setStyle("-fx-text-fill: red");
      // e.printStackTrace();
    }
  }

  private void setUpdatedTextField()
  {
    getTextField().setStyle("-fx-text-fill: orange");
  }

  private double getTextFieldValue()
  {
    Double lDoubleValue = Double.parseDouble(mTextField.getText());

    double lCorrectedValue = correctValueDouble(lDoubleValue);
    return lCorrectedValue;
  }

  private void setSliderValue(double pValue)
  {
    double lCorrectedValue = correctValueDouble(pValue);
    mSlider.setValue(lCorrectedValue);
    getTextField().setStyle("-fx-text-fill: black");
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
   * Returns slider
   * 
   * @return slider
   */
  public Slider getSlider()
  {
    return mSlider;
  }

  /**
   * Returns text field
   * 
   * @return text field
   */
  public TextField getTextField()
  {
    return mTextField;
  }

  /**
   * Returns if the variable value should b updated while the slider is dragged.
   * 
   * @return true -> update while changing
   */
  public boolean isUpdateIfChanging()
  {
    return mUpdateIfChanging;
  }

  /**
   * Sets whether the variable value should b updated while the slider is
   * dragged.
   * 
   * @param pUpdateIfChanging
   *          true -> update while changing
   */
  public void setUpdateIfChanging(boolean pUpdateIfChanging)
  {
    mUpdateIfChanging = pUpdateIfChanging;
  }

}
