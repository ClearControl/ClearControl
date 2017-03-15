package clearcontrol.gui.swing;

import java.awt.EventQueue;

import javax.swing.JProgressBar;

import clearcontrol.core.variable.Variable;

public class JProgressBarDouble extends JProgressBar
{
  private final Variable<Double> mJProgressBarDoubleVariable;

  private final JProgressBarDouble mThis;

  private final double mMin, mMax;

  public JProgressBarDouble(final String pName,
                            final double pMin,
                            final double pMax,
                            final double pInicialValue)
  {
    super(0, 65535);
    mThis = this;
    mMin = pMin;
    mMax = pMax;

    mJProgressBarDoubleVariable = new Variable<Double>(pName,
                                                       pInicialValue)
    {
      @Override
      public Double setEventHook(final Double pOldValue,
                                 final Double pNewValue)
      {
        final int lProgressBarNewIntegerValue =
                                              toInt(65535,
                                                    mMin,
                                                    mMax,
                                                    pNewValue);

        if (mThis.getValue() != lProgressBarNewIntegerValue)
        {
          EventQueue.invokeLater(new Runnable()
          {
            @Override
            public void run()
            {
              mThis.setValue(lProgressBarNewIntegerValue);
            }
          });
        }

        return super.setEventHook(pOldValue, pNewValue);
      }
    };

  }

  public Variable<Double> getDoubleVariable()
  {
    return mJProgressBarDoubleVariable;
  }

  private static double toDouble(final int pResolution,
                                 final double pMin,
                                 final double pMax,
                                 final int pIntValue)
  {
    return pMin
           + (double) pIntValue / (pResolution - 1) * (pMax - pMin);
  }

  private static int toInt(final int pResolution,
                           final double pMin,
                           final double pMax,
                           final double pValue)
  {
    return (int) (Math.round((pResolution - 1)
                             * (clamp(pMin, pMax, pValue) - pMin))
                  / (pMax - pMin));
  }

  private static double clamp(final double pMin,
                              final double pMax,
                              final double pValue)
  {
    return Math.min(pMax, Math.max(pMin, pValue));
  }

}
