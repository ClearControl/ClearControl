package rtlib.gui.jfx.slider;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import rtlib.core.variable.Variable;
import rtlib.core.variable.bounded.BoundedVariable;

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

		// setAlignment(Pos.CENTER);
		// setPadding(new Insets(25, 25, 25, 25));

		mLabel = new Label(pSliderName);
		mSlider = new Slider();

		mTextField = new TextField();
		getTextField().setPrefWidth(5 * 15);

		if (Double.isInfinite(mMin.get().doubleValue()) || Double.isNaN(mMin.get()
																																				.doubleValue()))
			getSlider().setMin(-10*pTicks.doubleValue());
		else
			getSlider().setMin(pMin.get().doubleValue());

		if (Double.isInfinite(mMax.get().doubleValue()) || Double.isNaN(mMax.get()
																																				.doubleValue()))
			getSlider().setMax(10*pTicks.doubleValue());
		else
			getSlider().setMax(pMax.get().doubleValue());

		getSlider().setMajorTickUnit(pTicks.doubleValue());
		getSlider().setShowTickMarks(true);
		getSlider().setShowTickLabels(true);
		if (pGranularity != null && pGranularity.get() != null)
			getSlider().setBlockIncrement(pGranularity.get().doubleValue());

		pMin.addSetListener((o, n) -> {
			if (!o.equals(n))
				Platform.runLater(() -> {

					getSlider().setMin(pMin.get().doubleValue());
				});
		});

		pMax.addSetListener((o, n) -> {
			if (!o.equals(n))
				Platform.runLater(() -> {
					getSlider().setMax(pMax.get().doubleValue());
				});
		});

		if (pMin.get() instanceof Double || pMin.get() instanceof Float)
		{
			getSlider().valueProperty().addListener((obs, o, n) -> {
				if (!o.equals(n))
					setTextFieldDouble(n);
			});
		}
		if (pMin.get() instanceof Integer || pMin.get() instanceof Long)
		{
			getSlider().valueProperty().addListener((obs, o, n) -> {
				if (!o.equals(n))
					setTextFieldLongValue(n);
			});
		}

		getTextField().focusedProperty().addListener((obs, o, n) -> {
			if (!n)
				setSliderValueFromTextField();
		});
		getTextField().setOnKeyPressed((e) -> {
			if (e.getCode().equals(KeyCode.ENTER))
				setSliderValueFromTextField();
			;
		});

		getChildren().add(getLabel());
		getChildren().add(getSlider());
		getChildren().add(getTextField());

		if (pMin.get() instanceof Double || pMin.get() instanceof Float)
		{
			setTextFieldDouble(pVariable.get());
		}
		if (pMin.get() instanceof Integer || pMin.get() instanceof Long)
		{
			setTextFieldLongValue(pVariable.get());
		}

		DoubleProperty lValueProperty = getSlider().valueProperty();

		lValueProperty.addListener((obs, o, n) -> {
			if (!isUpdateIfChanging() && getSlider().isValueChanging())
				return;
			double lCorrectedOldValue = correctValueDouble(o.doubleValue());
			double lCorrectedNewValue = correctValueDouble(n.doubleValue());

			if (lCorrectedOldValue != lCorrectedNewValue)
				setVariableValue(o, n);
		});

		getSlider().valueChangingProperty().addListener((obs, o, n) -> {
			if (isUpdateIfChanging())
				return;
			if (o == true && n == false)
				setVariableValue(Double.NaN, getSlider().getValue());
		});

		mVariable.addSetListener((o, n) -> {
			if (!n.equals(o))
				Platform.runLater(() -> {
					if (n.equals(mSlider.getValue()))
						return;

					if (pMin.get() instanceof Double || pMin.get() instanceof Float)
						setTextFieldDouble(n);
					else
						setTextFieldLongValue(n);

				});
		});
		
		setSliderValue(mVariable.get().doubleValue());

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
			if (mMin.get() instanceof Float)
				mVariable.set((T) new Float(lCorrectedValueDouble));
			if (mMin.get() instanceof Long)
				mVariable.set((T) new Long((long) lCorrectedValueLong));
			if (mMin.get() instanceof Integer)
				mVariable.set((T) new Integer((int) lCorrectedValueLong));
			if (mMin.get() instanceof Short)
				mVariable.set((T) new Short((short) lCorrectedValueLong));
			if (mMin.get() instanceof Byte)
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
	}

	private void setTextFieldLongValue(Number n)
	{
		getTextField().setText(String.format("%d", (long) n.longValue()));
	}

	private void setSliderValueFromTextField()
	{
		try
		{
			Double lDoubleValue = Double.parseDouble(mTextField.getText());

			double lCorrectedValue = (double) correctValueDouble(lDoubleValue);
			mSlider.setValue(lCorrectedValue);
			getTextField().setStyle("-fx-text-fill: black");
		}
		catch (NumberFormatException e)
		{
			getTextField().setStyle("-fx-text-fill: red");
			// e.printStackTrace();
		}
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
