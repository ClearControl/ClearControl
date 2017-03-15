package clearcontrol.gui.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import clearcontrol.core.variable.Variable;

public class JButtonBoolean extends JButton
{
  private final JButtonBoolean mThis;
  private final Variable<Boolean> mBooleanVariable;
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
    mBooleanVariable =
                     new Variable<Boolean>(pOnLabel + "/"
                                           + pOffLabel,
                                           pInitialState)
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
                                 setLabelFromState(lButtonState);
                               }
                               catch (final Throwable e)
                               {
                                 e.printStackTrace();
                               }
                             }
                           });
                         }

                         return super.setEventHook(pOldValue,
                                                   pNewValue);
                       }
                     };

    mOnLabel = pOnLabel;
    mOffLabel = pOffLabel;
    mButtonIsOnOffSwitch = pButtonIsOnOffSwitch;
    setLabelFromState(mBooleanVariable.get());

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
          mBooleanVariable.setEdge(false, true);
        }

        final boolean lButtonState = mBooleanVariable.get();

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

  public Variable<Boolean> getBooleanVariable()
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
