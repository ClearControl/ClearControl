package rtlib.gui.swing;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import rtlib.core.variable.doublev.DoubleVariable;

public class JSliderDouble extends JPanel
{
	private static final int cMaxResolution = 1024 * 1024;
	private final JLabel mNameLabel;
	private final JTextField mValueTextField;
	private final JSlider mSlider;

	private final String mLabelsFormatString;
	private final int mResolution;
	private final double mMin, mMax;
	private double mStep;
	private double mQuanta = 0;
	private int mNumberOfLabels = 3;
	private boolean mWaitForMouseRelease = false;

	private final DoubleVariable mSliderDoubleVariable;

	private final JSliderDouble mThis;
	private JButton mMinusStepButton;
	private JButton mPlusStepButton;


	/**
	 * @wbp.parser.constructor
	 */
	public JSliderDouble(final String pValueName)
	{
		this(pValueName, 0, 1, 0.5);
	}

	public JSliderDouble(	final String pValueName,
												final double pMin,
												final double pMax,
												final double pValue)
	{
		this(pValueName, "%.1f", Integer.MAX_VALUE, pMin, pMax, pValue, 1);
	}

	public JSliderDouble(	final String pValueName,
												final double pMin,
												final double pMax,
												final double pStep,
												final double pValue)
	{
		this(	pValueName,
					"%.1f",
					Integer.MAX_VALUE,
					pMin,
					pMax,
					pValue,
					pStep);
	}

	public JSliderDouble(	final String pValueName,
												final String pLabelsFormatString,
												final int pResolution,
												final double pMin,
												final double pMax,
												final double pValue,
												final double pStep)
	{
		super();

		mResolution = min(cMaxResolution, pResolution);

		mSliderDoubleVariable = new DoubleVariable(pValueName, pValue)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{

				final int lSliderIntegerValue = toInt(mResolution,
																							mMin,
																							mMax,
																							constraintIfNescessary(pNewValue));

				if (mSlider.getValue() != lSliderIntegerValue)
				{
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							mSlider.setValue(lSliderIntegerValue);
							writeValueIntoTextField(pNewValue);
							mValueTextField.setBackground(Color.white);
						}
					});
				}

				return super.setEventHook(pOldValue, pNewValue);
			}
		};
		setLayout(new MigLayout("",
														"[center][18.63%,grow,center][368px,grow,center][center]",
														"[25px:n:25px,grow,center][27px]"));

		mNameLabel = new JLabel(pValueName);
		mNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(mNameLabel, "cell 1 0,growx,aligny center");


		mSlider = new JSlider(0, mResolution - 1, toInt(mResolution,
																										pMin,
																										pMax,
																										pValue));
		add(mSlider, "cell 0 1 4 1,growx,aligny top");

		mValueTextField = new JTextField("" + pValue);
		mValueTextField.setHorizontalAlignment(SwingConstants.CENTER);
		add(mValueTextField, "cell 2 0,grow");

		mLabelsFormatString = pLabelsFormatString;

		mMin = pMin;
		mMax = pMax;
		mStep = pStep;
		mThis = this;

		mSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent pE)
			{

				final double lNewValue = constraintIfNescessary(toDouble(	mResolution,
																																	mMin,
																																	mMax,
																																	mSlider.getValue()));

				if (mSliderDoubleVariable.getValue() != lNewValue)
				{
					try
					{
						if (Double.parseDouble(mValueTextField.getText().trim()) != lNewValue)
						{
							writeValueIntoTextField(lNewValue);
							mValueTextField.setBackground(Color.white);
						}
					}
					catch (final Throwable e)
					{
						System.err.println(e.getLocalizedMessage());
					}

					if (isWaitForMouseRelease() && mSlider.getValueIsAdjusting())
					{
						return;
					}

					mSliderDoubleVariable.setValue(lNewValue);
				}
				// System.out.println("change received from slider:" + lNewValue);
			}

		});

		mValueTextField.getDocument()
										.addDocumentListener(new DocumentListener()
										{

											@Override
											public void removeUpdate(final DocumentEvent pE)
											{
												mValueTextField.setBackground(Color.red);
											}

											@Override
											public void insertUpdate(final DocumentEvent pE)
											{
												mValueTextField.setBackground(Color.red);
											}

											@Override
											public void changedUpdate(final DocumentEvent pE)
											{
											}
										});

		mValueTextField.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(final ActionEvent pE)
			{

				final String lTextString = mValueTextField.getText().trim();
				if (lTextString.isEmpty())
				{
					return;
				}

				try
				{
					double lNewValue = Double.parseDouble(lTextString);
					final double lNewIntegerValue = constraintIfNescessary(lNewValue);

					if (lNewValue != lNewIntegerValue)
					{
						lNewValue = lNewIntegerValue;
						writeValueIntoTextField(lNewValue);
					}

					mSliderDoubleVariable.setValue(lNewValue);

					final int lSliderIntegerValue = toInt(mResolution,
																								mMin,
																								mMax,
																								lNewValue);

					try
					{
						if (mSlider.getValue() != lSliderIntegerValue)
						{
							mSlider.setValue(lSliderIntegerValue);
						}
					}
					catch (final Throwable e)
					{
						System.err.println(e.getLocalizedMessage());
					}

					mValueTextField.setBackground(Color.white);
					// System.out.println("change received from textfield:"
					// + lNewValue);
				}
				catch (final NumberFormatException e)
				{
					System.err.println(e.getLocalizedMessage());
					return;
				}

			}

		});

		mSlider.setMajorTickSpacing(mResolution / 10);
		mSlider.setMinorTickSpacing(mResolution / 100);
		mSlider.setPaintTicks(true);

		mMinusStepButton = new JButton("\u2013");
		mMinusStepButton.setHorizontalTextPosition(SwingConstants.CENTER);
		add(mMinusStepButton, "cell 0 0,alignx left,growy");
		mMinusStepButton.addActionListener((e) -> {
			final double lStep = max(mStep, mQuanta);
			final int lModifiers = e.getModifiers();
			final double lFactor = ((lModifiers & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) ? 100
																																												: 10;
			double lNewValue = getDoubleVariable().getValue();
			if ((lModifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK)
				lNewValue += -lStep / lFactor;
			else if ((lModifiers & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
				lNewValue += -lStep * lFactor;
			else
				lNewValue += -lStep;
			lNewValue = constraintIfNescessary(lNewValue);
			getDoubleVariable().setValue(lNewValue);

		});

		mPlusStepButton = new JButton("+");
		add(mPlusStepButton, "cell 3 0,alignx left,growy");
		mPlusStepButton.addActionListener((e) -> {
			final double lStep = max(mStep, mQuanta);
			final int lModifiers = e.getModifiers();
			final double lFactor = ((lModifiers & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) ? 100
																																												: 10;
			double lNewValue = getDoubleVariable().getValue();
			if ((lModifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK)
				lNewValue += lStep / lFactor;
			else if ((lModifiers & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
				lNewValue += lStep * lFactor;
			else
				lNewValue += lStep;
			lNewValue = constraintIfNescessary(lNewValue);
			getDoubleVariable().setValue(lNewValue);
		});


		// Create the label table
		final Hashtable lLabelTable = new Hashtable();
		for (int i = 0; i <= mNumberOfLabels; i++)
		{
			final int lInteger = i * (pResolution - 1) / mNumberOfLabels;
			final double lDouble = toDouble(mResolution,
																			mMin,
																			mMax,
																			lInteger);
			final String lDoubleString = String.format(	mLabelsFormatString,
																									lDouble);
			lLabelTable.put(lInteger, new JLabel(lDoubleString));
		}
		mSlider.setLabelTable(lLabelTable);/**/

	}

	public DoubleVariable getDoubleVariable()
	{
		return mSliderDoubleVariable;
	}

	public double getValue()
	{
		return mSliderDoubleVariable.getValue();
	}

	private void writeValueIntoTextField(final double lNewValue)
	{
		if (mQuanta == 1)
		{
			mValueTextField.setText(String.format(getFormat(),
																						(long) lNewValue));
		}
		else
		{
			mValueTextField.setText(String.format(getFormat(), lNewValue));
		}
	}

	private String getFormat()
	{
		if (mQuanta == 0)
		{
			return "%g";
		}
		else if (mQuanta == 1)
		{
			return "%d";
		}
		else
		{
			final int lQuantaLog10 = (int) Math.round(Math.log10(mQuanta));

			if (lQuantaLog10 < 0)
			{
				return "%." + -lQuantaLog10 + "f";
			}
			else
			{
				return "%g";
			}
		}
	}

	private static double toDouble(	final int pResolution,
																	final double pMin,
																	final double pMax,
																	final int pIntValue)
	{
		return pMin + (double) pIntValue
						/ (pResolution - 1)
						* (pMax - pMin);
	}

	private static int toInt(	final int pResolution,
														final double pMin,
														final double pMax,
														final double pValue)
	{
		return (int) Math.round((pResolution - 1) * (clamp(	pMin,
																												pMax,
																												pValue) - pMin)
														/ (pMax - pMin));
	}

	private static double clamp(final double pMin,
															final double pMax,
															final double pValue)
	{
		return Math.min(pMax, Math.max(pMin, pValue));
	}

	public int getNumberOfLabels()
	{
		return mNumberOfLabels;
	}

	public void setNumberOfLabels(final int numberOfLabels)
	{
		mNumberOfLabels = numberOfLabels;
	}

	public void displayTickLabels(final boolean pDislayTickLabels)
	{
		mSlider.setPaintLabels(pDislayTickLabels);
	}

	public void setConstraint(final double pQuanta)
	{
		mQuanta = pQuanta;
	}

	public void setIntegerConstraint()
	{
		mQuanta = 1;
	}

	public void removeConstraint()
	{
		mQuanta = 0;
	}

	private double constraintIfNescessary(final double pValue)
	{
		final double lMinMaxConstrained = clamp(mMin, mMax, pValue);
		if (mQuanta == 0)
		{
			return lMinMaxConstrained;
		}
		else if (mQuanta == 1)
		{
			return Math.round(lMinMaxConstrained);
		}
		else
		{
			return Math.round(lMinMaxConstrained / mQuanta) * mQuanta;
		}
	}

	public boolean isWaitForMouseRelease()
	{
		return mWaitForMouseRelease;
	}

	public void setWaitForMouseRelease(boolean pWaitForMouseRelease)
	{
		mWaitForMouseRelease = pWaitForMouseRelease;
	}

	public void setPlusMinusButtonsVisible(boolean pShow)
	{
		mPlusStepButton.setVisible(pShow);
		mMinusStepButton.setVisible(pShow);
	}

}
