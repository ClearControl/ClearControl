package gui.swing;

import gui.swing.test.TestVideoCanvasFrameDisplay;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
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

public class CopyDoubleButton extends JButton
{
	private final CopyDoubleButton mThis;
	private String mLabel;
	private DoubleOutputVariableInterface mSource;
	private DoubleInputVariableInterface mDestination;

	public CopyDoubleButton(String pLabel)
	{
		this(pLabel, null, null);
	}

	public CopyDoubleButton(String pLabel,
													DoubleOutputVariableInterface pSource,
													DoubleInputVariableInterface pDestination)
	{
		mSource = pSource;
		mDestination = pDestination;
		mThis = this;
		setText(pLabel);

		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				final double lValue = mSource.getValue();
				mDestination.setValue(mSource, lValue);
			}
		});

	}

	public void setSource(DoubleOutputVariableInterface pDoubleVariable)
	{
		mSource = pDoubleVariable;
	}

	public void setDestination(DoubleInputVariableInterface pDoubleVariable)
	{
		mDestination = pDoubleVariable;
	}

}
