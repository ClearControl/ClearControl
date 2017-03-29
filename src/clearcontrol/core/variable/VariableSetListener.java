package clearcontrol.core.variable;

/**
 * Variable set listener
 *
 * @param <O>
 *          reference type
 * @author royer
 */
public interface VariableSetListener<O>
{
  /**
   * Called when a set event occurs.
   * 
   * @param pCurrentValue
   *          current variable value
   * @param pNewValue
   *          new value
   */
  void setEvent(O pCurrentValue, O pNewValue);
}
