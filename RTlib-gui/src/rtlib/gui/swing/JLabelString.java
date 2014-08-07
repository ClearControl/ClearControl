package rtlib.gui.swing;

import java.awt.EventQueue;

import javax.swing.JLabel;

import rtlib.core.variable.objectv.ObjectVariable;

public class JLabelString extends JLabel
{
	private final ObjectVariable<String> mStringVariable;
	private JLabelString mThis;

	public JLabelString(final String pLabelName,
											final String pInicialValue)
	{
		super(pInicialValue);
		mThis = this;

		mStringVariable = new ObjectVariable<String>(	pLabelName,
																									pInicialValue)
		{
			@Override
			public String setEventHook(	final String pOldValue,
																	final String pNewValue)
			{
				if (!pNewValue.equals(mThis.getText()))
				{
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							mThis.setText(pNewValue);
						}
					});
				}
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

	}

	public ObjectVariable<String> getStringVariable()
	{
		return mStringVariable;
	}

}
