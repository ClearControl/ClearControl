package clearcontrol.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import clearcontrol.core.variable.VariableGetInterface;
import clearcontrol.core.variable.VariableSetInterface;

public class CopyDoubleButton<O> extends JButton
{
  private static final long serialVersionUID = 1L;
  private final CopyDoubleButton<O> mThis;
  private String mLabel;
  private VariableGetInterface<O> mSource;
  private VariableSetInterface<O> mDestination;

  public CopyDoubleButton(final String pLabel)
  {
    this(pLabel, null, null);
  }

  public CopyDoubleButton(final String pLabel,
                          final VariableGetInterface<O> pSource,
                          final VariableSetInterface<O> pDestination)
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

  public void setSource(final VariableGetInterface<O> pVariable)
  {
    mSource = pVariable;
  }

  public void setDestination(final VariableSetInterface<O> pVariable)
  {
    mDestination = pVariable;
  }

}
