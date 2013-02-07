package gui.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import variable.doublev.DoubleInputVariableInterface;
import variable.doublev.DoubleVariable;

public class JSliderDouble extends JPanel
{
	private JLabel mNameLabel;
	private JTextField mValueTextField;
	private JSlider mSlider;

	private final String mLabelsFormatString;
	private int mResolution;
	private double mMin, mMax;

	private final JSliderDouble mThis;
	private DoubleVariable mDoubleVariable = new DoubleVariable(0);
	private int mNumberOfLabels = 3;

	public JSliderDouble(String pValueName)
	{
		this(pValueName, 0, 1, 0.5);
	}

	public JSliderDouble(	String pValueName,
												double pMin,
												double pMax,
												double pValue)
	{
		this(pValueName, 1024, pMin, pMax, pValue);
	}

	public JSliderDouble(	String pValueName,
												int pResolution,
												double pMin,
												double pMax,
												double pValue)
	{
		this(pValueName, "%.1f", pResolution, pMin, pMax, pValue);
	}

	public JSliderDouble(	String pValueName,
												String pLabelsFormatString,
												int pResolution,
												double pMin,
												double pMax,
												double pValue)
	{
		super();
		
		mDoubleVariable.setValue(null, pValue);

		setLayout(new BorderLayout(0, 0));

		mSlider = new JSlider(0, pResolution - 1, toInt(pResolution,
																										pMin,
																										pMax,
																										pValue));
		add(mSlider);

		mNameLabel = new JLabel(pValueName);
		mNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(mNameLabel, BorderLayout.NORTH);

		mValueTextField = new JTextField("" + pValue);
		mValueTextField.setHorizontalAlignment(SwingConstants.CENTER);
		add(mValueTextField, BorderLayout.SOUTH);

		mLabelsFormatString = pLabelsFormatString;
		mResolution = pResolution;
		mMin = pMin;
		mMax = pMax;
		mThis = this;

		mSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent pE)
			{
				final double lNewValue = toDouble(mResolution,
																					mMin,
																					mMax,
																					mSlider.getValue());
				mDoubleVariable.setValue(mSlider, lNewValue);
				mValueTextField.setText("" + lNewValue);
				// System.out.println("change received from slider:" + lNewValue);
			}
		});

		mValueTextField.getDocument()
										.addDocumentListener(new DocumentListener()
										{
											public void changedUpdate(DocumentEvent e)
											{
												parseDoubleAndNotify();
											}

											public void removeUpdate(DocumentEvent e)
											{
												parseDoubleAndNotify();
											}

											public void insertUpdate(DocumentEvent e)
											{
												parseDoubleAndNotify();
											}

											public void parseDoubleAndNotify()
											{
												final String lTextString = mValueTextField.getText()
																																	.trim();
												if (lTextString.isEmpty())
													return;

												try
												{
													final double lNewValue = Double.parseDouble(lTextString);
													mDoubleVariable.setValue(	mValueTextField,
																										lNewValue);
													try
													{
														mSlider.setValue(toInt(	mResolution,
																										mMin,
																										mMax,
																										lNewValue));
													}
													catch (Throwable e)
													{
													}
													// System.out.println("change received from textfield:"
													// + lNewValue);
												}
												catch (NumberFormatException e)
												{
													JOptionPane.showMessageDialog(null,
																												"Error: Please enter number bigger than 0",
																												"Error Message",
																												JOptionPane.ERROR_MESSAGE);
													return;
												}
											}
										});

		mDoubleVariable.sendUpdatesTo(new DoubleInputVariableInterface()
		{
			@Override
			public void setValue(	Object pDoubleEventSource,
														final double pNewValue)
			{
				if (pDoubleEventSource != mSlider && pDoubleEventSource != mValueTextField)
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							mSlider.setValue(toInt(	mResolution,
																			mMin,
																			mMax,
																			pNewValue));
							mValueTextField.setText("" + pNewValue);
						}
					});

				}
			}
		});

		mSlider.setMajorTickSpacing(pResolution / 10);
		mSlider.setMinorTickSpacing(pResolution / 100);
		mSlider.setPaintTicks(true);

		// Create the label table
		Hashtable lLabelTable = new Hashtable();
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
		return mDoubleVariable;
	}

	private static double toDouble(	int pResolution,
																	double pMin,
																	double pMax,
																	int pIntValue)
	{
		return pMin + (((double) pIntValue) / (pResolution - 1))
						* (pMax - pMin);
	}

	private static int toInt(	int pResolution,
														double pMin,
														double pMax,
														double pValue)
	{
		return (int) Math.round((pResolution - 1) * (clamp(	pMin,
																												pMax,
																												pValue) - pMin)
														/ (pMax - pMin));
	}

	private static double clamp(double pMin, double pMax, double pValue)
	{
		return Math.min(pMax, Math.max(pMin, pValue));
	}

	public int getNumberOfLabels()
	{
		return mNumberOfLabels;
	}

	public void setNumberOfLabels(int numberOfLabels)
	{
		mNumberOfLabels = numberOfLabels;
	}

}
