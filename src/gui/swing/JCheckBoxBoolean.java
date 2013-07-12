package gui.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import variable.booleanv.BooleanVariable;

public class JCheckBoxBoolean extends JCheckBox
{

	private final JCheckBoxBoolean mThis;
	private final BooleanVariable mBooleanVariable;

	public JCheckBoxBoolean(final String pLabel)
	{
		this(pLabel, false);
	}

	public JCheckBoxBoolean(final String pLabel,
													final boolean pInitialState)
	{
		super(pLabel);
		mThis = this;
		mBooleanVariable = new BooleanVariable(pLabel, pInitialState)
		{

			@Override
			public double setEventHook(final double pNewValue)
			{
				final boolean lButtonState = BooleanVariable.double2boolean(pNewValue);
				// if (pDoubleEventSource != mThis)
				{
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								setCheckmarkFromState(lButtonState);
							}
							catch (final Throwable e)
							{
								e.printStackTrace();
							}
						}
					});
				}

				return pNewValue;
			}
		};

		setCheckmarkFromState(mBooleanVariable.getBooleanValue());

		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent pE)
			{
				mBooleanVariable.toggle();

				final boolean lButtonState = mBooleanVariable.getBooleanValue();
				// System.out.println(lButtonState);

				EventQueue.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							setCheckmarkFromState(lButtonState);
						}
						catch (final Throwable e)
						{
							e.printStackTrace();
						}
					}
				});
			}

		});

	}

	public BooleanVariable getBooleanVariable()
	{
		return mBooleanVariable;
	}

	private void setCheckmarkFromState(final boolean lButtonState)
	{
		setSelected(lButtonState);
	}

	@Override
	public String getText()
	{
		// TODO Auto-generated method stub
		return super.getText();
	}

	@Override
	public void setText(final String pText)
	{
		// TODO Auto-generated method stub
		super.setText(pText);
	}

	@Override
	@Deprecated
	public String getLabel()
	{
		// TODO Auto-generated method stub
		return super.getLabel();
	}

	@Override
	@Deprecated
	public void setLabel(final String pLabel)
	{
		// TODO Auto-generated method stub
		super.setLabel(pLabel);
	}

}
