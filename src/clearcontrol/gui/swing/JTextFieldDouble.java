package clearcontrol.gui.swing;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import clearcontrol.core.variable.Variable;

public class JTextFieldDouble extends JPanel
{
  private JLabel mNameLabel;
  private JTextField mValueTextField;

  private String mLabelsFormatString = "%g";

  // private final JTextFieldDouble mThis;
  private Variable<Double> mDoubleVariable;

  private double mMin;
  private double mMax;
  private volatile double mNewValue;

  public JTextFieldDouble()
  {
    this("default", true, 0);
  }

  public JTextFieldDouble(final String pValueName,
                          final boolean pNorthSouthLayout,
                          final double pValue)
  {

    this(pValueName,
         pNorthSouthLayout,
         "%.1f",
         Double.NEGATIVE_INFINITY,
         Double.POSITIVE_INFINITY,
         pValue);
  }

  public JTextFieldDouble(final String pValueName,
                          final boolean pNorthSouthLayout,
                          final String pLabelsFormatString,
                          final double pMin,
                          final double pMax,
                          final double pValue)
  {
    super();
    setMin(pMin);
    setMax(pMax);

    mDoubleVariable = new Variable<Double>(pValueName, pValue)
    {
      @Override
      public Double setEventHook(final Double pOldValue,
                                 final Double pNewValue)
      {

        if (pNewValue != mNewValue)
        {
          mNewValue = pNewValue;
          EventQueue.invokeLater(new Runnable()
          {
            @Override
            public void run()
            {
              // System.out.println("mValueTextField.setText('' + pNewValue);");
              final String lString =
                                   String.format(getLabelsFormatString(),
                                                 clamp(pNewValue));
              mValueTextField.setText(lString);
              mValueTextField.setBackground(Color.white);

            }
          });
        }

        return super.setEventHook(pOldValue, pNewValue);
      }
    };

    setLayout(new BorderLayout(0, 0));

    mNameLabel = new JLabel(pValueName);
    mValueTextField = new JTextField("" + pValue);

    add(mNameLabel,
        pNorthSouthLayout ? BorderLayout.NORTH : BorderLayout.WEST);
    add(mValueTextField,
        pNorthSouthLayout ? BorderLayout.SOUTH : BorderLayout.CENTER);

    if (pNorthSouthLayout)
    {
      mNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
      mValueTextField.setHorizontalAlignment(SwingConstants.CENTER);
    }
    else
    {
      mNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
      mValueTextField.setHorizontalAlignment(SwingConstants.LEFT);
    }

    setLabelsFormatString(pLabelsFormatString);

    mValueTextField.getDocument()
                   .addDocumentListener(new DocumentListener()
                   {

                     @Override
                     public void removeUpdate(final DocumentEvent pE)
                     {
                       mValueTextField.setBackground(Color.red);
                     }

                     @Override
                     public void insertUpdate(final DocumentEvent pE)
                     {
                       mValueTextField.setBackground(Color.red);
                     }

                     @Override
                     public void changedUpdate(final DocumentEvent pE)
                     {

                     }
                   });

    mValueTextField.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent pE)
      {

        final String lTextString = mValueTextField.getText().trim();
        if (lTextString.isEmpty())
        {
          return;
        }

        try
        {

          final double lNewValue =
                                 clamp(Double.parseDouble(lTextString));
          if (lNewValue != mNewValue)
          {
            mNewValue = lNewValue;
            mDoubleVariable.set(mNewValue);
          }

          mValueTextField.setBackground(Color.white);
        }
        catch (final NumberFormatException e)
        {
          mValueTextField.setBackground(Color.orange);
          return;
        }

      }

    });

  }

  public Variable<Double> getDoubleVariable()
  {
    return mDoubleVariable;
  }

  public void setColumns(final int pNumberColumns)
  {
    mValueTextField.setColumns(pNumberColumns);
  }

  public void setValue(final double pValue)
  {
    mValueTextField.setText("" + pValue);
  }

  private double clamp(double pValue)
  {
    return min(max(pValue, getMin()), getMax());
  }

  public double getMin()
  {
    return mMin;
  }

  public void setMin(double pMin)
  {
    mMin = pMin;
  }

  public double getMax()
  {
    return mMax;
  }

  public void setMax(double pMax)
  {
    mMax = pMax;
  }

  public String getLabelsFormatString()
  {
    return mLabelsFormatString;
  }

  public void setLabelsFormatString(String pLabelsFormatString)
  {
    mLabelsFormatString = pLabelsFormatString;
  }

}
