package clearcontrol.core.concurrent.asyncprocs;

/**
 * Processor interface
 * 
 * A processor takes an object of type <I> as input and returns an object of
 * type <O> as output.
 *
 * @param <I>
 *          input type
 * @param <O>
 *          output type
 * @author royer
 */
public interface ProcessorInterface<I, O>
{

  /**
   * Processes the input and returns the corresponding output.
   * 
   * @param pInput
   *          input object
   * @return output object
   */
  public O process(I pInput);

}
