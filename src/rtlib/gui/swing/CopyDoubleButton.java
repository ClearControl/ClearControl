package rtlib.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import rtlib.core.variable.types.objectv.ObjectInputVariableInterface;
import rtlib.core.variable.types.objectv.ObjectOutputVariableInterface;

public class CopyDoubleButton<O> extends JButton
{
	private static final long serialVersionUID = 1L;
	private final CopyDoubleButton<O> mThis;
	private String mLabel;
	private ObjectOutputVariableInterface<O> mSource;
	private ObjectInputVariableInterface<O> mDestination;

	public CopyDoubleButton(final String pLabel)
	{
		this(pLabel, null, null);
	}

	public CopyDoubleButton(final String pLabel,
													final ObjectOutputVariableInterface<O> pSource,
													final ObjectInputVariableInterface<O> pDestination)
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
				final O lValue = mSource.get();
				mDestination.set(lValue);
			}
		});

	}

	public void setSource(final ObjectOutputVariableInterface<O> pVariable)
	{
		mSource = pVariable;
	}

	public void setDestination(final ObjectInputVariableInterface<O> pVariable)
	{
		mDestination = pVariable;
	}

}
