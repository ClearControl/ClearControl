package gui.swing;

import gui.swing.test.TestVideoCanvasFrameDisplay;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import variable.booleanv.BooleanVariable;
import variable.doublev.DoubleInputVariableInterface;
import variable.doublev.DoubleOutputVariableInterface;
import variable.doublev.DoubleVariable;

public class JCheckBoxBoolean extends JCheckBox
{

	private final JCheckBoxBoolean mThis;
	private BooleanVariable mBooleanVariable;

	public JCheckBoxBoolean(String pLabel)
	{
		this(pLabel,false);
	}

	public JCheckBoxBoolean(String pLabel, final boolean pInitialState)
	{
		super(pLabel);
		mThis = this;
		mBooleanVariable = new BooleanVariable(pInitialState);

		setCheckmarkFromState(mBooleanVariable.getBooleanValue());

		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				mBooleanVariable.toggle(mThis);

				final boolean lButtonState = mBooleanVariable.getBooleanValue();
				// System.out.println(lButtonState);

				EventQueue.invokeLater(new Runnable()
				{
					public void run()
					{
						try
						{
							setCheckmarkFromState(lButtonState);
						}
						catch (Throwable e)
						{
							e.printStackTrace();
						}
					}
				});
			}

		});

		mBooleanVariable.sendUpdatesTo(new DoubleInputVariableInterface()
		{

			@Override
			public void setValue(Object pDoubleEventSource, double pNewValue)
			{
				final boolean lButtonState = BooleanVariable.double2boolean(pNewValue);
				if (pDoubleEventSource != mThis)
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								setCheckmarkFromState(lButtonState);
							}
							catch (Throwable e)
							{
								e.printStackTrace();
							}
						}
					});
				}

			}
		});/**/

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
	public void setText(String pText)
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
	public void setLabel(String pLabel)
	{
		// TODO Auto-generated method stub
		super.setLabel(pLabel);
	}

}
