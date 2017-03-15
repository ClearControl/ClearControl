package clearcontrol.gui.swing;

import java.awt.EventQueue;

import javax.swing.JLabel;

import clearcontrol.core.variable.Variable;

public class JLabelString extends JLabel
{
  private final Variable<String> mStringVariable;
  private JLabelString mThis;

  public JLabelString(final String pLabelName,
                      final String pInicialValue)
  {
    super(pInicialValue);
    mThis = this;

    mStringVariable = new Variable<String>(pLabelName, pInicialValue)
    {
      @Override
      public String setEventHook(final String pOldValue,
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

  public Variable<String> getStringVariable()
  {
    return mStringVariable;
  }

}
