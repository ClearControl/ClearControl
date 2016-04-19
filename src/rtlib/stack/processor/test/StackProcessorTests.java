package rtlib.stack.processor.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackProcessorBase;
import rtlib.stack.processor.StackProcessorInterface;

public class StackProcessorTests
{

	private static final int cMaximalNumberOfAvailableObjects = 10;

	@Test
	public void test()
	{

		final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

		final StackProcessorInterface lStackProcessor = new StackProcessorBase("Test")
		{

			BasicRecycler<StackInterface, StackRequest> mRelayBasicRecycler = new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
																																																												10);

			@Override
			public StackInterface process(final StackInterface pStack,
																		final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
			{

				final StackRequest lStackRequest = StackRequest.build(pStack.getWidth(),
																															pStack.getHeight(),
																															1);

				final StackInterface lNewStack = mRelayBasicRecycler.getOrWait(	1L,
																																				TimeUnit.MILLISECONDS,
																																				lStackRequest);
				assertTrue(lNewStack != null);
				lNewStack.copyMetaDataFrom(pStack);
				pStackRecycler.release(pStack);
				return lNewStack;
			}

		};

		final BasicRecycler<StackInterface, StackRequest> mStartRecycler = new BasicRecycler<StackInterface, StackRequest>(	lOffHeapPlanarStackFactory,
																																																												cMaximalNumberOfAvailableObjects);

		final StackInterface lStack = mStartRecycler.getOrFail(StackRequest.build(1L,
																																							10L,
																																							10L,
																																							10L));
		assertTrue(lStack.getBytesPerVoxel() == 2);

		final StackInterface lProcessedStack = lStackProcessor.process(	lStack,
																																		mStartRecycler);

		assertFalse(lProcessedStack == lStack);

		assertTrue(lProcessedStack.getDepth() == 1);
		assertTrue(lProcessedStack.getBytesPerVoxel() == lStack.getBytesPerVoxel());

	}
}
