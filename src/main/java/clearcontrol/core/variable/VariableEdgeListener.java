package clearcontrol.core.variable;

/**
 * Variable edge event listener
 *
 * @param <O>
 *          reference type
 * @author royer
 */
public interface VariableEdgeListener<O>
{
  /**
   * Called when an edge event occurs
   * 
   * @param pNewValue
   *          new value (after the edge)
   */
  void fire(O pNewValue);
}
