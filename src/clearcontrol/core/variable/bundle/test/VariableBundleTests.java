package clearcontrol.core.variable.bundle.test;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableListener;
import clearcontrol.core.variable.bundle.VariableBundle;

import org.junit.Test;

/**
 * Variable bunle tests
 *
 * @author royer
 */
public class VariableBundleTests
{

  /**
   * Basic tests
   */
  @Test
  public void test()
  {
    final VariableBundle lVariableBundle = new VariableBundle("Name");
    lVariableBundle.addListener(new VariableListener<VariableBundle>()
    {

      @Override
      public void setEvent(VariableBundle pCurrentValue,
                           VariableBundle pNewValue)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void getEvent(VariableBundle pCurrentValue)
      {
        // TODO Auto-generated method stub

      }
    });

    final Variable<Double> lTestVariable =
                                         new Variable<Double>("var1");
    lVariableBundle.addVariable(lTestVariable);

  }

}
