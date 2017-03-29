package clearcontrol.core.variable;

/**
 * Variable set interface
 *
 * @param <O>
 *          reference type
 * @author royer
 */
public interface VariableSetInterface<O>
{
  /**
   * Sets the current variable value to a new value
   * 
   * @param pNewReference
   *          new value
   */
  void set(O pNewReference);
}
