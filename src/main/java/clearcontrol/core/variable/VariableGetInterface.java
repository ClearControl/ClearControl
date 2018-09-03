package clearcontrol.core.variable;

/**
 * Variable get interface.
 *
 * @param <O>
 *          reference type
 * @author royer
 */
public interface VariableGetInterface<O>
{
  /**
   * Returns the current variable value
   * 
   * @return current variable value
   */
  O get();
}
