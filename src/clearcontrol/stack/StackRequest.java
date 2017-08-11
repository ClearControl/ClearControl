package clearcontrol.stack;

import coremem.recycling.RecyclerRequestInterface;

/**
 * Stack request
 *
 * @author royer
 */
public class StackRequest implements RecyclerRequestInterface
{

  private final long mWidth, mHeight, mDepth;
  private final long mMetaDataSizeInBytes, mAlignment;

  /**
   * Instanciates a stack request given: width, height, depth, metadata size in
   * bytes, and alignment
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @param pMetaDataSizeInBytes
   *          metadata size in bytes
   * @param pAlignment
   *          alignment
   */
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

  /**
   * Instanciates a stack request of given width, height and depth
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   */
  public StackRequest(long pWidth, long pHeight, long pDepth)
  {
    this(pWidth, pHeight, pDepth, 0, 0);
  }

  /**
   * Instanciates a stack request of given width, height and depth
   * 
   * @param pDimensions
   *          (width,height,depth)
   * @return stack request
   */
  public static StackRequest build(final long... pDimensions)
  {
    return new StackRequest(pDimensions[0],
                            pDimensions[1],
                            pDimensions[2]);
  }

  /**
   * Instanciates a stack request of given width, height and depth
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @param pMetaDataLength
   *          metadata length
   * @return stack request
   */
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

  /**
   * Instanciates a stack request of given width, height, depth, metadtat length
   * and alignment
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @param pMetaDataLength
   *          meta data length
   * @param pAlignment
   *          alignment
   * @return stack request
   */
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

  /**
   * Instanciates a stack request using the details of a given stack
   * 
   * @param pStack
   *          stack to use as template
   * @return stack request
   */
  public static StackRequest buildFrom(final StackInterface pStack)
  {
    return new StackRequest(pStack.getWidth(),
                            pStack.getHeight(),
                            pStack.getDepth());
  }

  /**
   * Returns width
   * 
   * @return width
   */
  public long getWidth()
  {
    return mWidth;
  }

  /**
   * Returns height
   * 
   * @return height
   */
  public long getHeight()
  {
    return mHeight;
  }

  /**
   * Returns depth
   * 
   * @return depth
   */
  public long getDepth()
  {
    return mDepth;
  }

  /**
   * Returns dimensions
   * 
   * @return dimensions
   */
  public long[] getDimensions()
  {
    return new long[]
    { mWidth, mHeight, mDepth };
  }

  /**
   * Returns alignment
   * 
   * @return alignement
   */
  public long getAlignment()
  {
    return mAlignment;
  }

  /**
   * Returns the metadata size in bytes
   * 
   * @return metadata size in bytes
   */
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
