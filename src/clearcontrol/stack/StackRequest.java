package clearcontrol.stack;

import coremem.recycling.RecyclerRequestInterface;

public class StackRequest implements RecyclerRequestInterface
{

  private final long mWidth, mHeight, mDepth, mMetaDataSizeInBytes,
      mAlignment;

  public StackRequest(final long pWidth,
                      final long pHeight,
                      final long pDepth,
                      long pMetaDataSizeInBytes,
                      long pAlignment)
  {
    mWidth = pWidth;
    mHeight = pHeight;
    mDepth = pDepth;
    mMetaDataSizeInBytes = pMetaDataSizeInBytes;
    mAlignment = pAlignment;
  }

  public StackRequest(long pWidth, long pHeight, long pDepth)
  {
    this(pWidth, pHeight, pDepth, 0, 0);
  }

  public static StackRequest build(final long pWidth,
                                   final long pHeight,
                                   final long pDepth)
  {
    return new StackRequest(pWidth, pHeight, pDepth);
  }

  public static StackRequest build(final long pWidth,
                                   final long pHeight,
                                   final long pDepth,
                                   final long pMetaDataLength)
  {
    return new StackRequest(pWidth,
                            pHeight,
                            pDepth,
                            pMetaDataLength,
                            0);
  }

  public static StackRequest build(final long pWidth,
                                   final long pHeight,
                                   final long pDepth,
                                   final long pMetaDataLength,
                                   final long pAlignment)
  {
    return new StackRequest(pWidth,
                            pHeight,
                            pDepth,
                            pMetaDataLength,
                            pAlignment);
  }

  public static StackRequest buildFrom(final StackInterface pStack)
  {
    return new StackRequest(pStack.getWidth(),
                            pStack.getHeight(),
                            pStack.getDepth());
  }

  public long getWidth()
  {
    return mWidth;
  }

  public long getHeight()
  {
    return mHeight;
  }

  public long getDepth()
  {
    return mDepth;
  }

  public long[] getDimensions()
  {
    return new long[]
    { mWidth, mHeight, mDepth };
  }

  public long getAlignment()
  {
    return mAlignment;
  }

  public long getMetadataSizeInBytes()
  {
    return mMetaDataSizeInBytes;
  }

  @Override
  public String toString()
  {
    return String.format("StackRequest [mWidth=%s, mHeight=%s, mDepth=%s]",
                         mWidth,
                         mHeight,
                         mDepth);
  }

}
