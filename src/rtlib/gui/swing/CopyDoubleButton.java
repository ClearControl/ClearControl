package rtlib.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import rtlib.core.variable.types.doublev.DoubleInputVariableInterface;
import rtlib.core.variable.types.doublev.DoubleOutputVariableInterface;

public class CopyDoubleButton extends JButton
{
	private final CopyDoubleButton mThis;
	private String mLabel;
	private DoubleOutputVariableInterface mSource;
	private DoubleInputVariableInterface mDestination;

	public CopyDoubleButton(final String pLabel)
	{
		this(pLabel, null, null);
	}

	public CopyDoubleButton(final String pLabel,
							final DoubleOutputVariableInterface pSource,
							final DoubleInputVariableInterface pDestination)
	{
		mSource = pSource;
		mDestination = pDestination;
		mThis = this;
		setText(pLabel);

		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent pE)
			{
				final double lValue = mSource.getValue();
				mDestination.setValue(lValue);
			}
		});

	}

	public void setSource(final DoubleOutputVariableInterface pDoubleVariable)
	{
		mSource = pDoubleVariable;
	}

	public void setDestination(final DoubleInputVariableInterface pDoubleVariable)
	{
		mDestination = pDoubleVariable;
	}

}
