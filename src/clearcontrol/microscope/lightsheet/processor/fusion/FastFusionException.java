package clearcontrol.microscope.lightsheet.processor.fusion;

/**
 * Fast fusion exception
 *
 * @author royer
 */
public class FastFusionException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  /**
   * Instanciates a Fast Fusion exception given a cause and message
   * 
   * @param pCause
   *          cause
   * @param format
   *          format string
   * @param args
   *          format string arguments
   */
  public FastFusionException(Throwable pCause,
                             String format,
                             Object... args)
  {
    super(String.format(format, args), pCause);
  }

  /**
   * Instanciates a Fast Fusion exception given a message
   * 
   * @param format
   *          format string
   * @param args
   *          format string arguments
   */
  public FastFusionException(String format, Object... args)
  {
    super(String.format(format, args));
  }

}
