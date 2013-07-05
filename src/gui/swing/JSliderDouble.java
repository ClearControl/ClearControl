package gui.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import variable.doublev.DoubleVariable;

public class JSliderDouble extends JPanel
{
	private final JLabel mNameLabel;
	private final JTextField mValueTextField;
	private final JSlider mSlider;

	private final String mLabelsFormatString;
	private final int mResolution;
	private final double mMin, mMax;
	private int mNumberOfLabels = 3;

	private final DoubleVariable mSliderDoubleVariable;

	private final JSliderDouble mThis;

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
		this(pValueName, 1024, pMin, pMax, pValue);
	}

	public JSliderDouble(	final String pValueName,
												final int pResolution,
												final double pMin,
												final double pMax,
												final double pValue)
	{
		this(pValueName, "%.1f", pResolution, pMin, pMax, pValue);
	}

	public JSliderDouble(	final String pValueName,
												final String pLabelsFormatString,
												final int pResolution,
												final double pMin,
												final double pMax,
												final double pValue)
	{
		super();

		mSliderDoubleVariable = new DoubleVariable(pValueName, pValue)
		{
			@Override
			public double setEventHook(final double pNewValue)
			{

				final int lSliderIntegerValue = toInt(mResolution,
																							mMin,
																							mMax,
																							pNewValue);

				if (mSlider.getValue() != lSliderIntegerValue)
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							mSlider.setValue(lSliderIntegerValue);
							mValueTextField.setText("" + pNewValue);
						}
					});

				return pNewValue;
			}
		};

		setLayout(new BorderLayout(0, 0));

		mSlider = new JSlider(0, pResolution - 1, toInt(pResolution,
																										pMin,
																										pMax,
																										pValue));
		add(mSlider, BorderLayout.SOUTH);

		mNameLabel = new JLabel(pValueName);
		mNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(mNameLabel, BorderLayout.NORTH);

		mValueTextField = new JTextField("" + pValue);
		mValueTextField.setHorizontalAlignment(SwingConstants.CENTER);
		add(mValueTextField, BorderLayout.CENTER);

		mLabelsFormatString = pLabelsFormatString;
		mResolution = pResolution;
		mMin = pMin;
		mMax = pMax;
		mThis = this;

		mSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent pE)
			{
				final double lNewValue = toDouble(mResolution,
																					mMin,
																					mMax,
																					mSlider.getValue());
				mSliderDoubleVariable.setValue(lNewValue);
				try
				{
					if (Double.parseDouble(mValueTextField.getText().trim()) != lNewValue)
						mValueTextField.setText("" + lNewValue);
				}
				catch (Throwable e)
				{
					System.err.println(e.getLocalizedMessage());
				}
				// System.out.println("change received from slider:" + lNewValue);
			}
		});

		mValueTextField.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent pE)
			{

				final String lTextString = mValueTextField.getText().trim();
				if (lTextString.isEmpty())
					return;

				try
				{
					final double lNewValue = Double.parseDouble(lTextString);
					mSliderDoubleVariable.setValue(lNewValue);

					final int lSliderIntegerValue = toInt(mResolution,
																								mMin,
																								mMax,
																								lNewValue);

					try
					{
						if (mSlider.getValue() != lSliderIntegerValue)
							mSlider.setValue(lSliderIntegerValue);
					}
					catch (Throwable e)
					{
						System.err.println(e.getLocalizedMessage());
					}

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

		mSlider.setMajorTickSpacing(pResolution / 10);
		mSlider.setMinorTickSpacing(pResolution / 100);
		mSlider.setPaintTicks(true);

		// Create the label table
		final Hashtable lLabelTable = new Hashtable();
		for (int i = 0; i <= mNumberOfLabels; i++)
		{
			final int lInteger = (i * (pResolution - 1)) / mNumberOfLabels;
			final double lDouble = toDouble(mResolution,
																			mMin,
																			mMax,
																			lInteger);
			final String lDoubleString = String.format(	mLabelsFormatString,
																									lDouble);
			lLabelTable.put(lInteger, new JLabel(lDoubleString));
		}
		mSlider.setLabelTable(lLabelTable);/**/
		mSlider.setPaintLabels(true);

	}

	public DoubleVariable getDoubleVariable()
	{
		return mSliderDoubleVariable;
	}

	private static double toDouble(	final int pResolution,
																	final double pMin,
																	final double pMax,
																	final int pIntValue)
	{
		return pMin + (((double) pIntValue) / (pResolution - 1))
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

}
