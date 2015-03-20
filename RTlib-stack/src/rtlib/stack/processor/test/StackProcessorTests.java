package rtlib.stack.processor.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;

import rtlib.stack.FragmentedOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackProcessorBase;
import rtlib.stack.processor.StackProcessorInterface;
import coremem.recycling.BasicRecycler;

public class StackProcessorTests
{

	private static final int cMaximalNumberOfAvailableObjects = 10;

	@Test
	public void test()
	{

		final FragmentedOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStackFactory = new FragmentedOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess>();

		final StackProcessorInterface<UnsignedShortType, ShortOffHeapAccess, UnsignedShortType, ShortOffHeapAccess> lStackProcessor = new StackProcessorBase<UnsignedShortType, ShortOffHeapAccess, UnsignedShortType, ShortOffHeapAccess>("Test")
		{

			BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> mRelayBasicRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(	lOffHeapPlanarStackFactory,
																																																																																																															10);

			@Override
			public StackInterface<UnsignedShortType, ShortOffHeapAccess> process(	final StackInterface<UnsignedShortType, ShortOffHeapAccess> pStack,
																																						final BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> pStackRecycler)
			{

				final StackRequest<UnsignedShortType> lStackRequest = StackRequest.build(	pStack.getType(),
																																									pStack.getWidth(),
																																									pStack.getHeight(),
																																									1);

				final StackInterface<UnsignedShortType, ShortOffHeapAccess> lNewStack = mRelayBasicRecycler.getOrWait(	1L,
																																																															TimeUnit.MILLISECONDS,
																																																															lStackRequest);
				assertTrue(lNewStack != null);
				lNewStack.copyMetaDataFrom(pStack);
				pStackRecycler.release(pStack);
				return lNewStack;
			}

		};

		final BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> mStartRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(	lOffHeapPlanarStackFactory,
																																																																																																																	cMaximalNumberOfAvailableObjects);

		final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStack = mStartRecycler.getOrFail(StackRequest.build(	new UnsignedShortType(),
																																																																					1L,
																																																																					10L,
																																																																					10L,
																																																																					10L));
		assertTrue(lStack.getBytesPerVoxel() == 2);

		final StackInterface<UnsignedShortType, ShortOffHeapAccess> lProcessedStack = lStackProcessor.process(lStack,
																																																					mStartRecycler);

		assertFalse(lProcessedStack == lStack);

		assertTrue(lProcessedStack.getDepth() == 1);
		assertTrue(lProcessedStack.getBytesPerVoxel() == lStack.getBytesPerVoxel());

	}
}
