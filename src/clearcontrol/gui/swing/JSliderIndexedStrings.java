package clearcontrol.gui.swing;

import static java.lang.Math.round;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import clearcontrol.core.variable.Variable;

public class JSliderIndexedStrings extends JPanel
{
  private static final long serialVersionUID = 1L;

  private final JLabel mNameLabel;
  private final JTextField mValueTextField;
  private final JSlider mSlider;
  private final JSliderIndexedStrings mThis;
  private final JButton mMinusStepButton;
  private final JButton mPlusStepButton;

  private boolean mWaitForMouseRelease = false;

  private final Variable<Double> mSliderVariable;

  private List<String> mItemsList;

  public JSliderIndexedStrings(final String pValueName,
                               List<String> pItemsList,
                               int pInitialIndex)
  {
    super();
    mItemsList = pItemsList;

    mSliderVariable = new Variable<Double>(pValueName, 0.0)
    {
      @Override
      public Double setEventHook(final Double pOldValue,
                                 final Double pNewValue)
      {

        final int lSliderIntegerValue = getInt(pNewValue);

        if (mSlider.getValue() != lSliderIntegerValue)
        {
          EventQueue.invokeLater(new Runnable()
          {
            @Override
            public void run()
            {
              mSlider.setValue(lSliderIntegerValue);
              writeValueIntoTextField(pNewValue);
              mValueTextField.setBackground(Color.white);
            }
          });
        }

        return super.setEventHook(pOldValue, pNewValue);
      }
    };
    setLayout(new MigLayout("",
                            "[41px,center][16.00%,grow,center][368px,grow,center][41px,center]",
                            "[25px:n:25px][27px]"));

    mNameLabel = new JLabel(pValueName);
    mNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(mNameLabel, "cell 1 0,growx,aligny center");

    mSlider = new JSlider(0, mItemsList.size() - 1, pInitialIndex);
    // mSlider.setOrientation(SwingConstants.VERTICAL);
    add(mSlider, "cell 0 1 4 1,growx,aligny top");

    mValueTextField = new JTextField(mItemsList.get(pInitialIndex));
    mValueTextField.setEditable(false);
    mValueTextField.setHorizontalAlignment(SwingConstants.CENTER);
    add(mValueTextField, "cell 2 0,grow");

    mThis = this;

    mSlider.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(final ChangeEvent pE)
      {

        final int lNewValue = getInt(mSlider.getValue());

        if (mSliderVariable.get() != lNewValue)
        {
          try
          {
            writeValueIntoTextField(lNewValue);
            mValueTextField.setBackground(Color.white);
          }
          catch (final Throwable e)
          {
            System.err.println(e.getLocalizedMessage());
          }

          if (isWaitForMouseRelease()
              && mSlider.getValueIsAdjusting())
          {
            return;
          }

          mSliderVariable.set((double) lNewValue);
        }
        // System.out.println("change received from slider:" +
        // lNewValue);
      }

    });

    mSlider.setMajorTickSpacing(1);
    mSlider.setMinorTickSpacing(1);
    mSlider.setPaintTicks(true);

    mMinusStepButton = new JButton("\u2013");
    add(mMinusStepButton, "cell 0 0,alignx left,growy");
    mMinusStepButton.addActionListener((e) -> {
      final double lStep = 1;
      final int lModifiers = e.getModifiers();
      final double lFactor =
                           ((lModifiers
                             & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) ? 100
                                                                                  : 10;
      int lNewValue = getInt(getDoubleVariable().get());
      if ((lModifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK)
        lNewValue += -lStep / lFactor;
      else if ((lModifiers
                & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
        lNewValue += -lStep * lFactor;
      else
        lNewValue += -lStep;
      lNewValue = getInt(lNewValue);
      getDoubleVariable().set((double) lNewValue);

    });

    mPlusStepButton = new JButton("+");
    add(mPlusStepButton, "cell 3 0,alignx left,growy");
    mPlusStepButton.addActionListener((e) -> {
      final double lStep = 1;
      final int lModifiers = e.getModifiers();
      final double lFactor =
                           ((lModifiers
                             & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) ? 100
                                                                                  : 10;
      double lNewValue = getDoubleVariable().get();
      if ((lModifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK)
        lNewValue += lStep / lFactor;
      else if ((lModifiers
                & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
        lNewValue += lStep * lFactor;
      else
        lNewValue += lStep;
      lNewValue = getInt(lNewValue);
      getDoubleVariable().set(lNewValue);
    });

    // Create the label table
    final Hashtable lLabelTable = new Hashtable();
    for (int i = 0; i < mItemsList.size(); i++)
    {
      lLabelTable.put(i, new JLabel(mItemsList.get(i)));
    }
    mSlider.setLabelTable(lLabelTable);

  }

  public Variable<Double> getDoubleVariable()
  {
    return mSliderVariable;
  }

  public double getValue()
  {
    return mSliderVariable.get();
  }

  private void writeValueIntoTextField(final double pNewValue)
  {
    final int lIntValue = getInt(pNewValue);
    final String lItemString = mItemsList.get(lIntValue);
    mValueTextField.setText(lItemString);
  }

  private int getInt(final double pNewValue)
  {
    final int lIntValue = (int) clamp(0,
                                      mItemsList.size() - 1,
                                      round(pNewValue));
    return lIntValue;
  }

  private static double toDouble(final int pResolution,
                                 final double pMin,
                                 final double pMax,
                                 final int pIntValue)
  {
    return pMin
           + (double) pIntValue / (pResolution - 1) * (pMax - pMin);
  }

  private static double clamp(final double pMin,
                              final double pMax,
                              final double pValue)
  {
    return Math.min(pMax, Math.max(pMin, pValue));
  }

  public void displayTickLabels(final boolean pDislayTickLabels)
  {
    mSlider.setPaintLabels(pDislayTickLabels);
  }

  public boolean isWaitForMouseRelease()
  {
    return mWaitForMouseRelease;
  }

  public void setWaitForMouseRelease(boolean pWaitForMouseRelease)
  {
    mWaitForMouseRelease = pWaitForMouseRelease;
  }

}
