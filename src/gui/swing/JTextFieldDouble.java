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

public class JTextFieldDouble extends JPanel
{
	private JLabel mNameLabel;
	private JTextField mValueTextField;

	private final String mLabelsFormatString;
	private double mMin, mMax;

	private final JTextFieldDouble mThis;
	private DoubleVariable mDoubleVariable = new DoubleVariable(0);

	public JTextFieldDouble(String pValueName, double pValue)
	{
		this(pValueName, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, pValue);
	}

	public JTextFieldDouble(String pValueName,
													double pMin,
													double pMax,
													double pValue)
	{
		this(pValueName, 1024, pMin, pMax, pValue);
	}

	public JTextFieldDouble(String pValueName,
													int pResolution,
													double pMin,
													double pMax,
													double pValue)
	{
		this(pValueName, "%.1f", pResolution, pMin, pMax, pValue);
	}

	public JTextFieldDouble(String pValueName,
													String pLabelsFormatString,
													int pResolution,
													double pMin,
													double pMax,
													double pValue)
	{
		super();

		mDoubleVariable.setValue(null, pValue);

		setLayout(new BorderLayout(0, 0));

		mNameLabel = new JLabel(pValueName);
		mNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(mNameLabel, BorderLayout.NORTH);

		mValueTextField = new JTextField("" + pValue);
		mValueTextField.setHorizontalAlignment(SwingConstants.CENTER);
		add(mValueTextField, BorderLayout.SOUTH);

		mLabelsFormatString = pLabelsFormatString;
		mMin = pMin;
		mMax = pMax;
		mThis = this;

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
				if (pDoubleEventSource != mValueTextField)
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{

							mValueTextField.setText("" + pNewValue);
						}
					});

				}
			}
		});

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

	public void setColumns(int pNumberColumns)
	{
		mValueTextField.setColumns(pNumberColumns);
	}

	public void setValue(double pValue)
	{
		mValueTextField.setText(""+pValue);
	}

}
