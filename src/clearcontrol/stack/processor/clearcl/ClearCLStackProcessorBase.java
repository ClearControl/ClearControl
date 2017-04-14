package clearcontrol.stack.processor.clearcl;

import clearcl.ClearCLContext;
import clearcontrol.stack.processor.StackProcessorBase;
import clearcontrol.stack.processor.StackProcessorInterface;

/**
 * Base class for stack processors that use ClearCL
 *
 * @author royer
 */
public abstract class ClearCLStackProcessorBase extends
                                                StackProcessorBase
                                                implements
                                                StackProcessorInterface
{

  private final ClearCLContext mContext;

  /**
   * Instanciates a ClearCL powered stack processor
   * 
   * @param pContext
   *          ClearCL context
   * @param pProcessorName
   *          processor name
   */
  public ClearCLStackProcessorBase(String pProcessorName,
                                   ClearCLContext pContext)
  {
    super(pProcessorName);
    mContext = pContext;
  }

  /**
   * Returns underlying ClearCL context
   * 
   * @return context
   */
  public ClearCLContext getContext()
  {
    return mContext;
  }

}
