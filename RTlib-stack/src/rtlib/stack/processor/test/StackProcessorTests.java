package rtlib.stack.processor.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;
import rtlib.stack.processor.StackProcessorBase;
import rtlib.stack.processor.StackProcessorInterface;

public class StackProcessorTests
{

	@Test
	public void test()
	{
		StackProcessorInterface lStackProcessor = new StackProcessorBase("Test")
		{
			Recycler<Stack, Long> mRelayRecycler = new Recycler<Stack, Long>(Stack.class);

			@Override
			public Stack process(	Stack pStack,
														Recycler<Stack, Long> pStackRecycler)
			{
				long lWidth = pStack.getWidth();
				long lHeight = pStack.getHeight();
				long lBytesPerVoxel = pStack.getBytesPerVoxel();
				Stack lNewStack = mRelayRecycler.waitOrRequestRecyclableObject(	1L,
																																				TimeUnit.MILLISECONDS,
																																				lBytesPerVoxel,
																																				lWidth,
																																				lHeight,
																																				1L);
				assertTrue(lNewStack != null);
				lNewStack.copyMetaDataFrom(pStack);
				pStackRecycler.release(pStack);
				return lNewStack;
			}
		};

		Recycler<Stack, Long> mStartRecycler = new Recycler<Stack, Long>(Stack.class);

		Stack lStack = mStartRecycler.failOrRequestRecyclableObject(2L,
																																10L,
																																10L,
																																10L);
		assertTrue(lStack.getBytesPerVoxel() == 2);

		Stack lProcessedStack = lStackProcessor.process(lStack,
																										mStartRecycler);

		assertFalse(lProcessedStack == lStack);

		assertTrue(lProcessedStack.getDepth() == 1);
		assertTrue(lProcessedStack.getBytesPerVoxel() == lStack.getBytesPerVoxel());

	}
}
