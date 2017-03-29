package clearcontrol.core.variable;

/**
 * Variable listener, consists of a set and get listener
 *
 * @param <O>
 *          reference type
 * @author royer
 */
public interface VariableListener<O> extends
                                 VariableSetListener<O>,
                                 VariableGetListener<O>
{
}
