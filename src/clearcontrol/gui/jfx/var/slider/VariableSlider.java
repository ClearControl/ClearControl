package clearcontrol.gui.jfx.var.slider;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.signum;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.var.slider.customslider.Slider;

/**
 * Variable slider.
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
   * Instanciates a variable slider
   * 
   * @param pSliderName
   *          slider name
   * @param pVariable
   *          variable
   * @param pMin
   *          min
   * @param pMax
   *          max
   * @param pGranularity
   *          granularity
   * @param pTicks
   *          number of major ticks
   */
  public VariableSlider(String pSliderName,
                        Variable<T> pVariable,
                        T pMin,
                        T pMax,
                        T pGranularity,
                        T pTicks)
  {
    this(pSliderName,
         pVariable,
         new Variable<T>("min", pMin),
         new Variable<T>("max", pMax),
         new Variable<T>("granularity", pGranularity),
         pTicks);

  }

  public VariableSlider(String pSliderName,
                        BoundedVariable<T> pBoundedVariable,
                        T pTicks)
  {
    this(pSliderName,
         pBoundedVariable,
         pBoundedVariable.getMinVariable(),
         pBoundedVariable.getMaxVariable(),
         pBoundedVariable.getGranularityVariable(),
         pTicks);
  }

  public VariableSlider(String pSliderName,
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

    setAlignment(Pos.CENTER);
    setPadding(new Insets(25, 25, 25, 25));

    mLabel = new Label(pSliderName);
    mLabel.setAlignment(Pos.CENTER);

    mSlider = new Slider();

    mTextField = new TextField();
    mTextField.setAlignment(Pos.CENTER);

    getTextField().setPrefWidth(7 * 15);
    getSlider().setPrefWidth(14 * 15);

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

    getChildren().add(getLabel());
    getChildren().add(getSlider());
    getChildren().add(getTextField());

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

    double lTicksInterval = mTicks;
    if (lTicksInterval == 0)
    {
      double lRange = abs(pMax.get().doubleValue()
                          - pMin.get().doubleValue());

      if (lRange > 1)
        lTicksInterval = max(1, ((int) lRange) / 100);
      else
        lTicksInterval = lRange / 100;
    }

    double lEffectiveSliderMin = lTicksInterval
                                 * Math.floor(pMin.get().doubleValue()
                                              / lTicksInterval);
    double lEffectiveSliderMax = lTicksInterval
                                 * Math.ceil(pMax.get().doubleValue()
                                             / lTicksInterval);

    double lRelativeAbsoluteDistance = 2
                                       * (abs(lEffectiveSliderMin)
                                          - abs(lEffectiveSliderMax))
                                       / (abs(lEffectiveSliderMin)
                                          + abs(lEffectiveSliderMax));

    if (lRelativeAbsoluteDistance < 0.1)
    {
      double lRadius = max(abs(lEffectiveSliderMin),
                           abs(lEffectiveSliderMax));
      lEffectiveSliderMin = signum(lEffectiveSliderMin) * lRadius;
      lEffectiveSliderMax = signum(lEffectiveSliderMax) * lRadius;
    }

    if (Double.isInfinite(mMin.get().doubleValue())
        || Double.isNaN(mMin.get().doubleValue()))
      getSlider().setMin(-10 * mTicks);
    else
      getSlider().setMin(lEffectiveSliderMin);

    if (Double.isInfinite(mMax.get().doubleValue())
        || Double.isNaN(mMax.get().doubleValue()))
      getSlider().setMax(10 * mTicks);
    else
      getSlider().setMax(lEffectiveSliderMax);
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

  private void setVariableValueFromTextField()
  {
    double lNewValue = getTextFieldValue();
    setVariableValue(lNewValue);
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

  public Label getLabel()
  {
    return mLabel;
  }

  public Slider getSlider()
  {
    return mSlider;
  }

  public TextField getTextField()
  {
    return mTextField;
  }

  public boolean isUpdateIfChanging()
  {
    return mUpdateIfChanging;
  }

  public void setUpdateIfChanging(boolean pUpdateIfChanging)
  {
    mUpdateIfChanging = pUpdateIfChanging;
  }

}
