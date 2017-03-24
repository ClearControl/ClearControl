package clearcontrol.gui.jfx.var.rangeslider;

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

import org.controlsfx.control.RangeSlider;

public class VariableRangeSlider<T extends Number> extends HBox
{

  private final Label mLabel;
  private final RangeSlider mRangeSlider;
  private final TextField mLowTextField, mHighTextField;

  private Variable<T> mLow, mHigh, mMin, mMax, mGranularity;
  private boolean mUpdateIfChanging = false;
  private T mTicks;

  public VariableRangeSlider(String pSliderName,
                             Variable<T> pLow,
                             Variable<T> pHigh,
                             T pMin,
                             T pMax,
                             T pGranularity,
                             T pTicks)
  {
    this(pSliderName,
         pLow,
         pHigh,
         new Variable<T>("min", pMin),
         new Variable<T>("max", pMax),
         new Variable<T>("granularity", pGranularity),
         pTicks);

  }

  public VariableRangeSlider(String pSliderName,
                             Variable<T> pLow,
                             Variable<T> pHigh,
                             Variable<T> pMin,
                             Variable<T> pMax,
                             T pGranularity,
                             T pTicks)
  {
    this(pSliderName,
         pLow,
         pHigh,
         pMin,
         pMax,
         new Variable<T>("granularity", pGranularity),
         pTicks);

  }

  public VariableRangeSlider(String pSliderName,
                             Variable<T> pLow,
                             Variable<T> pHigh,
                             Variable<T> pMin,
                             Variable<T> pMax,
                             Variable<T> pGranularity,
                             T pTicks)
  {
    super();
    mLow = pLow;
    mHigh = pHigh;
    mMin = pMin;
    mMax = pMax;
    mGranularity = pGranularity;
    mTicks = pTicks;

    setAlignment(Pos.CENTER);
    setPadding(new Insets(25, 25, 25, 25));

    mLabel = new Label(pSliderName);
    mLabel.setAlignment(Pos.CENTER);

    mRangeSlider = new RangeSlider(mMin.get().doubleValue(),
                                   mMax.get().doubleValue(),
                                   mLow.get().doubleValue(),
                                   mHigh.get().doubleValue());

    mLowTextField = new TextField();
    mHighTextField = new TextField();

    getRangeSlider().setPrefWidth(14 * 15);

    getLowTextField().setAlignment(Pos.CENTER);
    getLowTextField().setPrefWidth(7 * 15);

    getHighTextField().setAlignment(Pos.CENTER);
    getHighTextField().setPrefWidth(7 * 15);

    updateSliderMinMax(pMin, pMax, pTicks);

    getRangeSlider().setMajorTickUnit(pTicks.doubleValue());
    getRangeSlider().setShowTickMarks(true);
    getRangeSlider().setShowTickLabels(true);
    if (pGranularity != null && pGranularity.get() != null)
      getRangeSlider().setBlockIncrement(pGranularity.get()
                                                     .doubleValue());

    pMin.addSetListener((o, n) -> {
      if (!pMin.get().equals(n) && n != null)
        Platform.runLater(() -> {
          updateSliderMinMax(pMin, pMax, pTicks);
        });
    });

    pMax.addSetListener((o, n) -> {
      if (!pMax.get().equals(n) && n != null)
        Platform.runLater(() -> {
          updateSliderMinMax(pMin, pMax, pTicks);
        });
    });

    Platform.runLater(() -> {
      updateSliderMinMax(pMin, pMax, pTicks);
    });

    getRangeSlider().setOnMouseDragged((e) -> {
      double lCorrectedSliderLowValue =
                                      correctLowValueDouble(getRangeSlider().getLowValue());
      double lCorrectedSliderHighValue =
                                       correctLowValueDouble(getRangeSlider().getHighValue());

      getRangeSlider().setLowValue(lCorrectedSliderLowValue);
      getRangeSlider().setHighValue(lCorrectedSliderHighValue);

      setLowTextField(lCorrectedSliderLowValue);
      setHighTextField(lCorrectedSliderHighValue);

      if (!(!isUpdateIfChanging()
            && getRangeSlider().isLowValueChanging()))
      {
        // if (lCorrectedSliderLowValue != mLow.get().doubleValue())
        setVariableValue(mLow, true, lCorrectedSliderLowValue);
      }

      if (!(!isUpdateIfChanging()
            && getRangeSlider().isHighValueChanging()))
      {
        // if (lCorrectedSliderHighValue != mHigh.get().doubleValue())
        setVariableValue(mHigh, false, lCorrectedSliderHighValue);
      }

    });

    getLowTextField().textProperty().addListener((obs, o, n) -> {
      if (!o.equals(n))
        setUpdatedLowTextField();
    });

    getHighTextField().textProperty().addListener((obs, o, n) -> {
      if (!o.equals(n))
        setUpdatedHighTextField();
    });

    getLowTextField().focusedProperty().addListener((obs, o, n) -> {
      if (!n)
      {
        setLowTextField(getLowTextFieldValue());
        setRangeSliderLowValueFromTextField();
        setVariableValue(mLow, true, getRangeSlider().getLowValue());
      }
    });

    getHighTextField().focusedProperty().addListener((obs, o, n) -> {
      if (!n)
      {
        setHighTextField(getHighTextFieldValue());
        setRangeSliderHighValueFromTextField();
        setVariableValue(mHigh,
                         false,
                         getRangeSlider().getHighValue());
      }
    });

    getLowTextField().setOnKeyPressed((e) -> {
      if (e.getCode().equals(KeyCode.ENTER))
      {
        setLowTextField(getLowTextFieldValue());
        setRangeSliderLowValueFromTextField();
        setVariableValue(mLow, true, getRangeSlider().getLowValue());
      }
      ;
    });

    getHighTextField().setOnKeyPressed((e) -> {
      if (e.getCode().equals(KeyCode.ENTER))
      {
        setHighTextField(getHighTextFieldValue());
        setRangeSliderHighValueFromTextField();
        setVariableValue(mHigh,
                         false,
                         getRangeSlider().getHighValue());
      }
      ;
    });

    getChildren().add(getLabel());
    getChildren().add(getLowTextField());
    getChildren().add(getRangeSlider());
    getChildren().add(getHighTextField());

    if (pMin.get() instanceof Double || pMin.get() instanceof Float)
    {
      setLowTextFieldDouble(mLow.get());
      setHighTextFieldDouble(mHigh.get());
    }
    if (pMin.get() instanceof Integer || pMin.get() instanceof Long)
    {
      setLowTextFieldLongValue(mLow.get());
      setHighTextFieldLongValue(mHigh.get());
    }

    getRangeSlider().lowValueChangingProperty()
                    .addListener((obs, o, n) -> {
                      if (isUpdateIfChanging())
                        return;
                      if (o == true && n == false)
                        setVariableValue(mLow,
                                         true,
                                         getRangeSlider().getLowValue());
                    });

    getRangeSlider().highValueChangingProperty()
                    .addListener((obs, o, n) -> {
                      if (isUpdateIfChanging())
                        return;
                      if (o == true && n == false)
                        setVariableValue(mHigh,
                                         false,
                                         getRangeSlider().getHighValue());
                    });

    mLow.addSetListener((o, n) -> {
      if (!n.equals(o) && n != null)
        Platform.runLater(() -> {
          if (n.equals(getRangeSlider().getLowValue())
              && n.equals(getLowTextFieldValue()))
            return;

          if (pMin.get() instanceof Double
              || pMin.get() instanceof Float)
            setLowTextFieldDouble(n);
          else
            setLowTextFieldLongValue(n);

        });
    });

    mHigh.addSetListener((o, n) -> {
      if (!n.equals(o) && n != null)
        Platform.runLater(() -> {
          if (n.equals(getRangeSlider().getHighValue())
              && n.equals(getHighTextFieldValue()))
            return;

          if (pMin.get() instanceof Double
              || pMin.get() instanceof Float)
            setHighTextFieldDouble(n);
          else
            setHighTextFieldLongValue(n);

        });
    });

    Platform.runLater(() -> {
      setLowTextFieldDouble(mLow.get().doubleValue());
      setHighTextFieldDouble(mHigh.get().doubleValue());

      setRangeSliderLowValue(mLow.get().doubleValue());
      setRangeSliderHighValue(mHigh.get().doubleValue());
    });

  }

  private void setLowTextField(Number n)
  {
    if (mMin.get() instanceof Double || mMin.get() instanceof Float)
      setLowTextFieldDouble(n);
    else if (mMin.get() instanceof Integer
             || mMin.get() instanceof Long)
      setLowTextFieldLongValue(n);
  }

  private void setHighTextField(Number n)
  {
    if (mMin.get() instanceof Double || mMin.get() instanceof Float)
      setHighTextFieldDouble(n);
    else if (mMin.get() instanceof Integer
             || mMin.get() instanceof Long)
      setHighTextFieldLongValue(n);
  }

  private void updateSliderMinMax(Variable<T> pMin,
                                  Variable<T> pMax,
                                  T pTicks)
  {
    double lTicksInterval = mTicks.doubleValue();
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
      getRangeSlider().setMin(-10 * pTicks.doubleValue());
    else
      getRangeSlider().setMin(lEffectiveSliderMin);

    if (Double.isInfinite(mMax.get().doubleValue())
        || Double.isNaN(mMax.get().doubleValue()))
      getRangeSlider().setMax(10 * pTicks.doubleValue());
    else
      getRangeSlider().setMax(lEffectiveSliderMax);
  }

  @SuppressWarnings("unchecked")
  private void setVariableValue(Variable<T> pVariable,
                                boolean pIsLow,
                                Number pNewValue)
  {
    if (!pVariable.get().equals(pNewValue))
    {
      double lCorrectedValueDouble =
                                   pIsLow ? correctLowValueDouble(pNewValue.doubleValue())
                                          : correctHighValueDouble(pNewValue.doubleValue());
      long lCorrectedValueLong =
                               pIsLow ? correctLowValueLong(pNewValue.longValue())
                                      : correctHighValueLong(pNewValue.longValue());
      if (mMin.get() instanceof Double)
        pVariable.set((T) new Double(lCorrectedValueDouble));
      if (mMin.get() instanceof Float)
        pVariable.set((T) new Float(lCorrectedValueDouble));
      if (mMin.get() instanceof Long)
        pVariable.set((T) new Long(lCorrectedValueLong));
      if (mMin.get() instanceof Integer)
        pVariable.set((T) new Integer((int) lCorrectedValueLong));
      if (mMin.get() instanceof Short)
        pVariable.set((T) new Short((short) lCorrectedValueLong));
      if (mMin.get() instanceof Byte)
        pVariable.set((T) new Byte((byte) lCorrectedValueLong));
    }
  }

  @SuppressWarnings("unchecked")
  private double correctLowValueDouble(double pValue)
  {
    if (pValue < mMin.get().doubleValue())
      return mMin.get().doubleValue();
    if (pValue > getRangeSlider().getHighValue())
      return getRangeSlider().getHighValue();

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
  private double correctHighValueDouble(double pValue)
  {
    if (pValue < getRangeSlider().getLowValue())
      return getRangeSlider().getLowValue();
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
  private long correctLowValueLong(long pValue)
  {
    if (pValue < mMin.get().longValue())
      return mMin.get().longValue();
    if (pValue > getRangeSlider().getHighValue())
      return (long) getRangeSlider().getHighValue();

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

  @SuppressWarnings("unchecked")
  private long correctHighValueLong(long pValue)
  {
    if (pValue < getRangeSlider().getLowValue())
      return (long) getRangeSlider().getLowValue();
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

  private void setLowTextFieldDouble(Number pDoubleValue)
  {
    double lCorrectedValue =
                           correctLowValueDouble(pDoubleValue.doubleValue());
    getLowTextField().setText(String.format("%.3f", lCorrectedValue));
    getLowTextField().setStyle("-fx-text-fill: black");
  }

  private void setHighTextFieldDouble(Number pDoubleValue)
  {
    double lCorrectedValue =
                           correctHighValueDouble(pDoubleValue.doubleValue());
    getHighTextField().setText(String.format("%.3f",
                                             lCorrectedValue));
    getHighTextField().setStyle("-fx-text-fill: black");
  }

  private void setLowTextFieldLongValue(Number n)
  {
    getLowTextField().setText(String.format("%d", n.longValue()));
    getLowTextField().setStyle("-fx-text-fill: black");
  }

  private void setHighTextFieldLongValue(Number n)
  {
    getHighTextField().setText(String.format("%d", n.longValue()));
    getHighTextField().setStyle("-fx-text-fill: black");
  }

  private void setRangeSliderLowValueFromTextField()
  {
    try
    {
      double lCorrectedValue = getLowTextFieldValue();
      getRangeSlider().setLowValue(lCorrectedValue);
      getLowTextField().setStyle("-fx-text-fill: black");
    }
    catch (NumberFormatException e)
    {
      getLowTextField().setStyle("-fx-text-fill: red");
    }
  }

  private void setRangeSliderHighValueFromTextField()
  {
    try
    {
      double lCorrectedValue = getHighTextFieldValue();
      getRangeSlider().setHighValue(lCorrectedValue);
      getHighTextField().setStyle("-fx-text-fill: black");
    }
    catch (NumberFormatException e)
    {
      getHighTextField().setStyle("-fx-text-fill: red");
    }
  }

  private void setUpdatedLowTextField()
  {
    getLowTextField().setStyle("-fx-text-fill: orange");
  }

  private void setUpdatedHighTextField()
  {
    getHighTextField().setStyle("-fx-text-fill: orange");
  }

  private void setVariableLowValueFromTextField()
  {
    double lNewValue = getLowTextFieldValue();
    setVariableValue(mLow, true, lNewValue);
  }

  private void setVariableHighValueFromTextField()
  {
    double lNewValue = getHighTextFieldValue();
    setVariableValue(mHigh, false, lNewValue);
  }

  private double getLowTextFieldValue()
  {
    Double lDoubleValue = Double.parseDouble(mLowTextField.getText());

    double lCorrectedValue = correctLowValueDouble(lDoubleValue);
    return lCorrectedValue;
  }

  private double getHighTextFieldValue()
  {
    Double lDoubleValue =
                        Double.parseDouble(mHighTextField.getText());

    double lCorrectedValue = correctHighValueDouble(lDoubleValue);
    return lCorrectedValue;
  }

  private void setRangeSliderLowValue(double pValue)
  {
    double lCorrectedValue = correctLowValueDouble(pValue);
    mRangeSlider.setLowValue(lCorrectedValue);
    getLowTextField().setStyle("-fx-text-fill: black");
  }

  private void setRangeSliderHighValue(double pValue)
  {
    double lCorrectedValue = correctHighValueDouble(pValue);
    mRangeSlider.setHighValue(lCorrectedValue);
    getHighTextField().setStyle("-fx-text-fill: black");
  }

  public Label getLabel()
  {
    return mLabel;
  }

  public RangeSlider getRangeSlider()
  {
    return mRangeSlider;
  }

  public TextField getLowTextField()
  {
    return mLowTextField;
  }

  public TextField getHighTextField()
  {
    return mHighTextField;
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
