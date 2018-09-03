package clearcontrol.core.string;

/**
 * Formats a memory size in bytes by automatically changing the scale from
 * bytes, to kilobytes, megabytes and gigatbytes.
 *
 * @author royer
 */
public class MemorySizeFormat
{

  /**
   * Returns a formatted string for a given size in bytes. if the short form is
   * chosen, 'Bytes' will be replaced by 'B', and so one for 'KiloBytes' to
   * 'KB'...
   * 
   * @param pSizeInBytes
   *          size in bytes
   * @param pShortForm
   *          short form flag
   * @return formatted string
   */
  public static String format(double pSizeInBytes, boolean pShortForm)
  {
    double lSizeScaled = pSizeInBytes;
    String lPostFix;

    if (pSizeInBytes < 1000)
    {
      lSizeScaled = pSizeInBytes;
      lPostFix = pShortForm ? "B" : "Bytes";
      return String.format("%d %s", (int) lSizeScaled, lPostFix);
    }
    else if (pSizeInBytes < 1000_000)
    {
      lSizeScaled = pSizeInBytes / 1000;
      lPostFix = pShortForm ? "KB" : "KiloBytes";
      return String.format("%.2f %s", lSizeScaled, lPostFix);
    }
    else if (pSizeInBytes < 1000_000_000)
    {
      lSizeScaled = pSizeInBytes / 1000_000;
      lPostFix = pShortForm ? "MB" : "MegaBytes";
      return String.format("%.2f %s", lSizeScaled, lPostFix);
    }
    else
    {
      lSizeScaled = pSizeInBytes / 1000_000_000;
      lPostFix = pShortForm ? "GB" : "GigaBytes";
      return String.format("%.2f %s", lSizeScaled, lPostFix);
    }

  }
}
