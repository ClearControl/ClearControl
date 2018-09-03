package clearcontrol.core.concurrent.asyncprocs;

/**
 * Asynchronous processor
 *
 * @param <I>
 *          input type
 * @param <O>
 *          output type
 * @author royer
 */
public class AsynchronousProcessor<I, O> extends
                                  AsynchronousProcessorBase<I, O>
{

  private ProcessorInterface<I, O> mProcessor;

  /**
   * Instantiates an asynchronous processor with a given name, max queue size,
   * and processor
   * 
   * @param pName
   *          name
   * @param pMaxQueueSize
   *          max queue size
   * @param pProcessor
   *          processor
   */
  @SuppressWarnings(
  { "unchecked", "rawtypes" })
  public AsynchronousProcessor(String pName,
                               int pMaxQueueSize,
                               final ProcessorInterface pProcessor)
  {
    super(pName, pMaxQueueSize);
    mProcessor = pProcessor;
  }

  @Override
  public O process(I pInput)
  {
    return mProcessor.process(pInput);
  }

}
