package clearcontrol.gui.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import clearcontrol.core.variable.Variable;

public class JCheckBoxBoolean extends JCheckBox
{

  private final JCheckBoxBoolean mThis;
  private final Variable<Boolean> mBooleanVariable;

  public JCheckBoxBoolean(final String pLabel)
  {
    this(pLabel, false);
  }

  public JCheckBoxBoolean(final String pLabel,
                          final boolean pInitialState)
  {
    super(pLabel);
    mThis = this;
    mBooleanVariable = new Variable<Boolean>(pLabel, pInitialState)
    {

      @Override
      public Boolean setEventHook(final Boolean pOldValue,
                                  final Boolean pNewValue)
      {
        final boolean lButtonState = pNewValue;
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

        return super.setEventHook(pOldValue, pNewValue);
      }
    };

    setCheckmarkFromState(mBooleanVariable.get());

    addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent pE)
      {
        mBooleanVariable.toggle();

        final boolean lButtonState = mBooleanVariable.get();
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

  public Variable<Boolean> getBooleanVariable()
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
    return super.getText();
  }

  @Override
  public void setText(final String pText)
  {
    super.setText(pText);
  }

  @Override
  @Deprecated
  public String getLabel()
  {
    return super.getLabel();
  }

  @Override
  @Deprecated
  public void setLabel(final String pLabel)
  {
    super.setLabel(pLabel);
  }

}
