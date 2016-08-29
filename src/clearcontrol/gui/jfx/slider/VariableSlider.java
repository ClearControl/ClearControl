package clearcontrol.gui.jfx.slider;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.signum;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.slider.customslider.Slider;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

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
	private T mTicks;

	public VariableSlider(String pSliderName,
												Variable<T> pVariable,
												T pMin,
												T pMax,
												T pGranularity,
												T pTicks)
	{
		this(	pSliderName,
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
		this(	pSliderName,
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
		mTicks = pTicks;

		setAlignment(Pos.CENTER);
		setPadding(new Insets(25, 25, 25, 25));

		mLabel = new Label(pSliderName);
		mLabel.setAlignment(Pos.CENTER);

		mSlider = new Slider();

		mTextField = new TextField();
		mTextField.setAlignment(Pos.CENTER);

		getTextField().setPrefWidth(7 * 15);
		getSlider().setPrefWidth(14 * 15);

		updateSliderMinMax(pMin, pMax, pTicks);

		getSlider().setMajorTickUnit(pTicks.doubleValue());
		getSlider().setShowTickMarks(true);
		getSlider().setShowTickLabels(true);
		if (pGranularity != null && pGranularity.get() != null)
			getSlider().setBlockIncrement(pGranularity.get().doubleValue());

		pMin.addSetListener((o, n) -> {
			if (!o.equals(n))
				Platform.runLater(() -> {
					updateSliderMinMax(pMin, pMax, pTicks);
				});
		});

		pMax.addSetListener((o, n) -> {
			if (!o.equals(n))
				Platform.runLater(() -> {
					updateSliderMinMax(pMin, pMax, pTicks);
				});
		});

		getTextField().textProperty().addListener((obs, o, n) -> {
			if (!o.equals(n))
				setUpdatedTextField();
		});

		getTextField().focusedProperty().addListener((obs, o, n) -> {
			if (!n)
				setSliderValueFromTextField();
		});

		getTextField().setOnKeyPressed((e) -> {
			if (e.getCode().equals(KeyCode.ENTER))
			{
				setVariableValue(Double.NaN, getSlider().getValue());
				setSliderValueFromTextField();
			}
			;
		});

		getChildren().add(getLabel());
		getChildren().add(getSlider());
		getChildren().add(getTextField());

		if (pMin.get() instanceof Double || pMin.get() instanceof Float)
		{
			setTextFieldDouble(mVariable.get());
		}
		if (pMin.get() instanceof Integer || pMin.get() instanceof Long)
		{
			setTextFieldLongValue(mVariable.get());
		}

		getSlider().setOnMouseDragged((e) -> {
			double lCorrectedSliderValue = correctValueDouble(getSlider().getValue());
			setTextFieldValue(lCorrectedSliderValue);

			if (!isUpdateIfChanging() && getSlider().isValueChanging())
				return;

			if (lCorrectedSliderValue != mVariable.get().doubleValue())
				setVariableValue(	mVariable.get().doubleValue(),
													lCorrectedSliderValue);
		});

		/*
		DoubleProperty lValueProperty = getSlider().valueProperty();

		lValueProperty.addListener((obs, o, n) -> {

			double lCorrectedOldValue = correctValueDouble(o.doubleValue());
			double lCorrectedNewValue = correctValueDouble(n.doubleValue());
			setTextFieldValue(lCorrectedNewValue);
			setSliderValue(lCorrectedNewValue);

			if (!isUpdateIfChanging() && getSlider().isValueChanging())
				return;

			if (lCorrectedOldValue != lCorrectedNewValue)
				setVariableValue(o, n);
		});/**/

		getSlider().valueChangingProperty().addListener((obs, o, n) -> {
			if (isUpdateIfChanging())
				return;
			if (o == true && n == false)
				setVariableValue(Double.NaN, getSlider().getValue());
		});

		mVariable.addSetListener((o, n) -> {
			if (!n.equals(o))
				Platform.runLater(() -> {
					if (n.equals(getSlider().getValue()) && n.equals(getTextFieldValue()))
					{
						//System.out.println("rejected");
						return;
					}

					if (pMin.get() instanceof Double || pMin.get() instanceof Float)
						setTextFieldDouble(n);
					else
						setTextFieldLongValue(n);

					setSliderValueFromTextField();

				});
		});

		setSliderValue(mVariable.get().doubleValue());

	}

	private void setTextFieldValue(Number n)
	{
		if (mMin.get() instanceof Double || mMin.get() instanceof Float)
			setTextFieldDouble(n);

		else if (mMin.get() instanceof Integer || mMin.get() instanceof Long)
			setTextFieldLongValue(n);
	}

	private void updateSliderMinMax(Variable<T> pMin,
																	Variable<T> pMax,
																	T pTicks)
	{
		double lTicksInterval = mTicks.doubleValue();
		if (lTicksInterval == 0)
		{
			double lRange = abs(pMax.get().doubleValue() - pMin.get()
																													.doubleValue());

			if (lRange > 1)
				lTicksInterval = max(1, ((int) lRange) / 100);
			else
				lTicksInterval = lRange / 100;
		}

		double lEffectiveSliderMin = lTicksInterval * Math.floor(pMin.get()
																																	.doubleValue() / lTicksInterval);
		double lEffectiveSliderMax = lTicksInterval * Math.ceil(pMax.get()
																																.doubleValue() / lTicksInterval);

		double lRelativeAbsoluteDistance = 2 * (abs(lEffectiveSliderMin) - abs(lEffectiveSliderMax))
																				/ (abs(lEffectiveSliderMin) + abs(lEffectiveSliderMax));

		if (lRelativeAbsoluteDistance < 0.1)
		{
			double lRadius = max(	abs(lEffectiveSliderMin),
														abs(lEffectiveSliderMax));
			lEffectiveSliderMin = signum(lEffectiveSliderMin) * lRadius;
			lEffectiveSliderMax = signum(lEffectiveSliderMax) * lRadius;
		}

		if (Double.isInfinite(mMin.get().doubleValue()) || Double.isNaN(mMin.get()
																																				.doubleValue()))
			getSlider().setMin(-10 * pTicks.doubleValue());
		else
			getSlider().setMin(lEffectiveSliderMin);

		if (Double.isInfinite(mMax.get().doubleValue()) || Double.isNaN(mMax.get()
																																				.doubleValue()))
			getSlider().setMax(10 * pTicks.doubleValue());
		else
			getSlider().setMax(lEffectiveSliderMax);
	}

	@SuppressWarnings("unchecked")
	private void setVariableValue(Number pOldValue, Number pNewValue)
	{
		if (!mVariable.get().equals(pNewValue))
		{
			double lCorrectedValueDouble = (double) correctValueDouble(pNewValue.doubleValue());
			long lCorrectedValueLong = (long) correctValueLong(pNewValue.longValue());
			if (mMin.get() instanceof Double)
				mVariable.set((T) new Double(lCorrectedValueDouble));
			else if (mMin.get() instanceof Float)
				mVariable.set((T) new Float(lCorrectedValueDouble));
			else if (mMin.get() instanceof Long)
				mVariable.set((T) new Long((long) lCorrectedValueLong));
			else if (mMin.get() instanceof Integer)
				mVariable.set((T) new Integer((int) lCorrectedValueLong));
			else if (mMin.get() instanceof Short)
				mVariable.set((T) new Short((short) lCorrectedValueLong));
			else if (mMin.get() instanceof Byte)
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

			double lCorrectedValue = lGranularity * Math.round(pValue / lGranularity);

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

			long lCorrectedValue = lGranularity * (long) Math.round(1.0 * pValue
																															/ lGranularity);
			return lCorrectedValue;
		}

		return pValue;
	}

	private void setTextFieldDouble(Number pDoubleValue)
	{
		double lCorrectedValue = (double) correctValueDouble(pDoubleValue.doubleValue());
		getTextField().setText(String.format("%.3f", lCorrectedValue));
		getTextField().setStyle("-fx-text-fill: black");
	}

	private void setTextFieldLongValue(Number n)
	{
		getTextField().setText(String.format("%d", (long) n.longValue()));
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
		setVariableValue(Double.NaN, lNewValue);
	}

	private double getTextFieldValue()
	{
		Double lDoubleValue = Double.parseDouble(mTextField.getText());

		double lCorrectedValue = (double) correctValueDouble(lDoubleValue);
		return lCorrectedValue;
	}

	private void setSliderValue(double pValue)
	{
		double lCorrectedValue = (double) correctValueDouble(pValue);
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
