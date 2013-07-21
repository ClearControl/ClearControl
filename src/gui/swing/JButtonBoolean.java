package gui.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import variable.booleanv.BooleanVariable;

public class JButtonBoolean extends JButton
{
	private final JButtonBoolean mThis;
	private final BooleanVariable mBooleanVariable;
	private final String mOnLabel, mOffLabel;

	private boolean mButtonIsOnOffSwitch = true;

	public JButtonBoolean(final boolean pInitialState,
												final String pOnLabel,
												final String pOffLabel)
	{
		this(pInitialState, pOnLabel, pOffLabel, true);
	}

	public JButtonBoolean(final String pLabel)
	{
		this(false, pLabel, pLabel, false);
	}

	public JButtonBoolean(final String pRestLabel,
												final String pPressingLabel)
	{
		this(false, pPressingLabel, pRestLabel, false);
	}

	public JButtonBoolean(final boolean pInitialState,
												final String pOnLabel,
												final String pOffLabel,
												final boolean pButtonIsOnOffSwitch)
	{
		mThis = this;
		mBooleanVariable = new BooleanVariable(	pOnLabel + "/"
																								+ pOffLabel,
																						pInitialState)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
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
								setLabelFromState(lButtonState);
							}
							catch (final Throwable e)
							{
								e.printStackTrace();
							}
						}
					});
				}

				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mOnLabel = pOnLabel;
		mOffLabel = pOffLabel;
		mButtonIsOnOffSwitch = pButtonIsOnOffSwitch;
		setLabelFromState(mBooleanVariable.getBooleanValue());

		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent pE)
			{
				if (mButtonIsOnOffSwitch)
				{
					mBooleanVariable.toggle();
				}
				else
				{
					mBooleanVariable.setValue(true);
					mBooleanVariable.setValue(false);
				}

				final boolean lButtonState = mBooleanVariable.getBooleanValue();

				EventQueue.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							setLabelFromState(lButtonState);
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

	private void setLabelFromState(final boolean lButtonState)
	{
		setSelected(lButtonState);
		if (lButtonState)
		{
			setText(mOnLabel);
		}
		else
		{
			setText(mOffLabel);
		}
	}

}
