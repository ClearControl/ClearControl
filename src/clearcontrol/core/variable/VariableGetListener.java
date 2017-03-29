package clearcontrol.core.variable;

/**
 * Variable get event listener
 *
 * @param <O>
 *          reference type
 * @author royer
 */
public interface VariableGetListener<O>
{
  /**
   * Called when a get event occurs
   * 
   * @param pCurrentValue
   *          current value
   */
  void getEvent(O pCurrentValue);
}
