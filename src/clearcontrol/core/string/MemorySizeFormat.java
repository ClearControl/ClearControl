package clearcontrol.core.string;

public class MemorySizeFormat
{

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
