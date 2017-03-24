package clearcontrol.core.variable.queue;

/**
 * Invalid queue exception.
 *
 * @author royer
 */
public class InvalidQueueException extends RuntimeException
{

  private static final long serialVersionUID = 1L;

  /**
   * Instanciates an invalid queue exception.
   * 
   * @param pMessage
   *          error message
   * @param pCause
   *          cause
   */
  public InvalidQueueException(String pMessage, Throwable pCause)
  {
    super(pMessage, pCause);
  }

  /**
   * Instanciates an invalid queue exception.
   * 
   * @param pMessage
   *          error message
   */
  public InvalidQueueException(String pMessage)
  {
    super(pMessage);
  }

}
