package clearcontrol.hardware.cameras.devices.sim;

import static java.lang.Math.max;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.imglib2.exception.IncompatibleTypeException;
import clearcontrol.hardware.cameras.StackCameraDeviceBase;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;

public class StackGenerator
{
  private StackCameraDeviceBase lCameraDeviceBase;
  protected AtomicLong mCurrentStackIndex = new AtomicLong(0);

  public StackGenerator(StackCameraDeviceBase pCameraDeviceBase)
  {
    this.lCameraDeviceBase = pCameraDeviceBase;
  }

  protected StackInterface generateFractalStack() throws IncompatibleTypeException
  {
    final long lWidth = max(1,
                            lCameraDeviceBase.getStackWidthVariable()
                                             .get());
    final long lHeight = max(1,
                             lCameraDeviceBase.getStackHeightVariable()
                                              .get());
    final long lDepth = max(1,
                            lCameraDeviceBase.getStackDepthVariable()
                                             .get());
    final int lChannel = lCameraDeviceBase.getChannelVariable().get();

    System.out.println("lChannel=" + lChannel);

    final int lNumberOfImagesPerPlane =
                                      lCameraDeviceBase.getNumberOfImagesPerPlaneVariable()
                                                       .get()
                                                       .intValue();

    final StackRequest lStackRequest = StackRequest.build(lWidth,
                                                          lHeight,
                                                          lDepth);

    final StackInterface lStack = lCameraDeviceBase.getStackRecycler()
                                                   .getOrWait(1,
                                                              TimeUnit.SECONDS,
                                                              lStackRequest);

    if (lStack != null)
    {
      final byte time = (byte) mCurrentStackIndex.get();

      final ContiguousMemoryInterface lContiguousMemory =
                                                        lStack.getContiguousMemory();
      final ContiguousBuffer lContiguousBuffer =
                                               new ContiguousBuffer(lContiguousMemory);

      for (int z = 0; z < lDepth; z++)
        for (int y = 0; y < lHeight; y++)
          for (int x = 0; x < lWidth; x++)
          {
            short lValue =
                         (short) (((byte) (x + time)
                                   ^ (byte) (y + (lHeight * lChannel)
                                                 / 3)
                                   ^ (byte) z
                                   ^ (byte) (time)));/**/
            if (lValue < 32)
              lValue = 0;
            lContiguousBuffer.writeShort(lValue);
          }

    }

    return lStack;
  }

  /**
   * @return
   * @throws IncompatibleTypeException
   */
  protected StackInterface generateSinusStack() throws IncompatibleTypeException
  {
    final long lWidth = max(1,
                            lCameraDeviceBase.getStackWidthVariable()
                                             .get());
    final long lHeight = max(1,
                             lCameraDeviceBase.getStackHeightVariable()
                                              .get());
    final long lDepth = max(1,
                            lCameraDeviceBase.getStackDepthVariable()
                                             .get());
    final int lChannel = lCameraDeviceBase.getChannelVariable().get();

    final int lNumberOfImagesPerPlane =
                                      lCameraDeviceBase.getNumberOfImagesPerPlaneVariable()
                                                       .get()
                                                       .intValue();

    final StackRequest lStackRequest = StackRequest.build(lWidth,
                                                          lHeight,
                                                          lDepth);

    final StackInterface lStack = lCameraDeviceBase.getStackRecycler()
                                                   .getOrWait(1,
                                                              TimeUnit.SECONDS,
                                                              lStackRequest);

    if (lStack != null)
    {
      final byte time = (byte) mCurrentStackIndex.get();

      final ContiguousMemoryInterface lContiguousMemory =
                                                        lStack.getContiguousMemory();
      final ContiguousBuffer lContiguousBuffer =
                                               new ContiguousBuffer(lContiguousMemory);

      for (int z = 0; z < lDepth; z++)
        for (int y = 0; y < lHeight; y++)
          for (int x = 0; x < lWidth; x++)
          {
            short lValue =
                         (short) (128
                                  + 128
                                    * Math.sin(((x
                                                 + (lWidth * lChannel)
                                                   / 3)
                                                % lWidth)
                                               / 64.0));/**/
            lContiguousBuffer.writeShort(lValue);
          }

    }

    return lStack;
  }

  protected StackInterface generateOtherStack() throws IncompatibleTypeException
  {
    final long lWidth = max(1,
                            lCameraDeviceBase.getStackWidthVariable()
                                             .get());
    final long lHeight = max(1,
                             lCameraDeviceBase.getStackHeightVariable()
                                              .get());
    final long lDepth = max(1,
                            lCameraDeviceBase.getStackDepthVariable()
                                             .get());
    final int lChannel = lCameraDeviceBase.getChannelVariable().get();

    final int lNumberOfImagesPerPlane =
                                      lCameraDeviceBase.getNumberOfImagesPerPlaneVariable()
                                                       .get()
                                                       .intValue();

    final StackRequest lStackRequest = StackRequest.build(lWidth,
                                                          lHeight,
                                                          lDepth);

    final StackInterface lStack = lCameraDeviceBase.getStackRecycler()
                                                   .getOrWait(1,
                                                              TimeUnit.SECONDS,
                                                              lStackRequest);

    // mRecycler.printDebugInfo();
    // System.out.println(lStackRequest.toString());
    /*
    		if (lStack != null)
    		{
    			final byte time = (byte) mCurrentStackIndex;
    			if (mHint == null || mHint.type.startsWith("normal"))
    			{
    				final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();
    				final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);
    
    				for (int z = 0; z < lDepth; z++)
    					for (int y = 0; y < lHeight; y++)
    						for (int x = 0; x < lWidth; x++)
    						{
    							int lValueValue = (((byte) (x+time) ^ (byte) (y)
    																	^ (byte) z ^ (byte)(time)));
    							if (lValueValue < 32)
    								lValueValue = 0;
    							lContiguousBuffer.writeShort((short) lValueValue);
    						}
    			}
    			else if (mHint != null && mHint.type.startsWith("autofocus"))
    			{
    				final double lInFocusZ = mHint.focusz;
    
    				final ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();
    				final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);
    				for (int z = 0; z < lDepth; z++)
    				{
    					final double lNormalizedZ = (1.0 * z) / lDepth;
    					final double lFocalDistance = abs(lNormalizedZ - lInFocusZ);
    					final double lIntensity = 1 / (1 + 10 * lFocalDistance);
    					final double lFrequency = 0.1 * (1 - lFocalDistance);
    					for (int y = 0; y < lHeight; y++)
    						for (int x = 0; x < lWidth; x++)
    						{
    							final int lValueValue = (int) (128 * lIntensity * (1 + cos(x * lFrequency)));
    							// System.out.println(lValueValue);
    							lContiguousBuffer.writeShort((short) lValueValue);
    						}
    				}
    
    				/*final RandomAccessibleInterval<T> lImage = lStack.getImage();
    
    				for (int z = 0; z < lDepth; z++)
    				{
    					@SuppressWarnings("rawtypes")
    					final IntervalView lHyperSlice = Views.hyperSlice(lImage,
    																														2,
    																														z);
    
    					@SuppressWarnings(
    					{ "rawtypes", "unchecked" })
    					final RandomAccessible lInfiniteImg = Views.extendValue(lHyperSlice,
    																																	mType);
    
    					final double lNormalizedZ = 1.0 * z / lDepth;
    					final double lFocalDistance = abs(lNormalizedZ - lInFocusZ);
    					Gauss3.gauss(20 * lFocalDistance, lInfiniteImg, lHyperSlice);
    				}
    
    			}
    		}/**/

    return lStack;
  }
}
