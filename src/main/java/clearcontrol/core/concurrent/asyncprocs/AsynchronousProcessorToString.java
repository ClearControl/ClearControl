package clearcontrol.core.concurrent.asyncprocs;

/**
 * Asynchronous processor that receives an object and transforms into a string
 * by invoking the object's toString() method.
 *
 * @param <I>
 *          input type
 * @author royer
 */
public class AsynchronousProcessorToString<I> extends
                                          AsynchronousProcessorBase<I, String>
                                          implements
                                          AsynchronousProcessorInterface<I, String>
{

  /**
   * Instantiates an asynchronous toString() processor
   */
  public AsynchronousProcessorToString()
  {
    super(AsynchronousProcessorToString.class.getSimpleName(), 100);
  }

  @Override
  public String process(final I pInput)
  {
    final String lString = pInput.toString();
    return lString;
  }

}
