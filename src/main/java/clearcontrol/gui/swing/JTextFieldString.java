package clearcontrol.gui.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import clearcontrol.core.variable.Variable;

public class JTextFieldString extends JPanel
{
  private JLabel mNameLabel;
  private JTextField mValueTextField;

  private final JTextFieldString mThis;
  private final Variable<String> mObjectVariable;

  public JTextFieldString(final String pValueName,
                          final String pInicialValue)
  {
    super();

    mObjectVariable = new Variable<String>(pValueName, pInicialValue)
    {
      @Override
      public String setEventHook(final String pOldValue,
                                 final String pNewValue)
      {

        if (!pNewValue.equals(mValueTextField.getText()))
        {
          EventQueue.invokeLater(new Runnable()
          {
            @Override
            public void run()
            {
              mValueTextField.setText("" + pNewValue);
            }
          });
        }

        return pNewValue;
      }
    };

    setLayout(new BorderLayout(0, 0));

    mNameLabel = new JLabel(pValueName);
    mNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(mNameLabel, BorderLayout.NORTH);

    mValueTextField = new JTextField(pInicialValue);
    mValueTextField.setHorizontalAlignment(SwingConstants.CENTER);
    add(mValueTextField, BorderLayout.SOUTH);

    mThis = this;

    mValueTextField.getDocument()
                   .addDocumentListener(new DocumentListener()
                   {
                     @Override
                     public void changedUpdate(final DocumentEvent e)
                     {
                       parseDoubleAndNotify();
                     }

                     @Override
                     public void removeUpdate(final DocumentEvent e)
                     {
                       parseDoubleAndNotify();
                     }

                     @Override
                     public void insertUpdate(final DocumentEvent e)
                     {
                       parseDoubleAndNotify();
                     }

                     public void parseDoubleAndNotify()
                     {
                       final String lTextString =
                                                mValueTextField.getText()
                                                               .trim();

                       try
                       {
                         mObjectVariable.set(lTextString);

                       }
                       catch (final NumberFormatException e)
                       {
                         JOptionPane.showMessageDialog(null,
                                                       "String cannot be parsed as double!",
                                                       "Error Message",
                                                       JOptionPane.ERROR_MESSAGE);
                         return;
                       }
                     }
                   });

  }

  public Variable<String> getStringVariable()
  {
    return mObjectVariable;
  }

  public void setColumns(final int pNumberColumns)
  {
    mValueTextField.setColumns(pNumberColumns);
  }

  public void setValue(final double pValue)
  {
    mValueTextField.setText("" + pValue);
  }

}
