package gui.swing;

import java.awt.EventQueue;

import javax.swing.JLabel;

import variable.doublev.DoubleVariable;

public class JLabelDouble extends JLabel
{
	private final DoubleVariable mDoubleVariable;
	private JLabelDouble mThis;
	private final String mFormatString;

	private boolean mIntegerConstraint = false;

	public JLabelDouble(final String pLabelName,
											final boolean pIntegerConstraint,
											final String pFormatString,
											final double pInicialValue)
	{
		super(getTextFromValue(	pIntegerConstraint,
														pFormatString,
														pInicialValue));
		mFormatString = pFormatString;
		mIntegerConstraint = mIntegerConstraint;
		mThis = this;

		mDoubleVariable = new DoubleVariable(pLabelName, pInicialValue)
		{
			@Override
			public double setEventHook(final double pNewValue)
			{
				if (pNewValue != mThis.getDoubleValue())
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							mThis.setText(getTextFromValue(	mIntegerConstraint,
																							pFormatString,
																							pNewValue));
						}

					});
				return super.setEventHook(pNewValue);
			}
		};

		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				setText(getTextFromValue(	mIntegerConstraint,
																	pFormatString,
																	pInicialValue));
			}
		});

	}

	protected double getDoubleValue()
	{
		return Double.parseDouble(getText());
	}

	public DoubleVariable getDoubleVariable()
	{
		return mDoubleVariable;
	}

	private static String getTextFromValue(	final boolean pIntegerConstraint,
																					final String pFormatString,
																					final double pNewValue)
	{
		if (pIntegerConstraint)
			return String.format(	pFormatString,
														(long) Math.round(pNewValue));
		else
			return String.format(pFormatString, pNewValue);
	}

	public boolean isIntegerConstraint()
	{
		return mIntegerConstraint;
	}

	public void setIntegerConstraint(final boolean pIsIntegerConstraint)
	{
		mIntegerConstraint = pIsIntegerConstraint;
	}

}
