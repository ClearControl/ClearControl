package rtlib.stack.processor.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.core.memory.SizeOf;
import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;
import rtlib.stack.processor.StackProcessorBase;
import rtlib.stack.processor.StackProcessorInterface;

public class StackProcessorTests
{

	@Test
	public void test()
	{
		StackProcessorInterface<Short, Short> lStackProcessor = new StackProcessorBase<Short, Short>("Test")
		{

			Recycler<Stack<Short>, Long> mRelayRecycler = new Recycler<Stack<Short>, Long>(Stack.class);

			@Override
			public Stack<Short> process(Stack<Short> pStack,
																	Recycler<Stack<Short>, Long> pStackRecycler)
			{
				long lWidth = pStack.getWidth();
				long lHeight = pStack.getHeight();
				long lBytesPerVoxel = pStack.getBytesPerVoxel();
				Stack<Short> lNewStack = mRelayRecycler.waitOrRequestRecyclableObject(1L,
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

		Recycler<Stack<Short>, Long> mStartRecycler = new Recycler<Stack<Short>, Long>(Stack.class);

		Stack<Short> lStack = mStartRecycler.failOrRequestRecyclableObject(	(long) SizeOf.sizeOf(short.class),
																																10L,
																																10L,
																																10L);
		assertTrue(lStack.getBytesPerVoxel() == 2);

		Stack<Short> lProcessedStack = lStackProcessor.process(	lStack,
																										mStartRecycler);

		assertFalse(lProcessedStack == lStack);

		assertTrue(lProcessedStack.getDepth() == 1);
		assertTrue(lProcessedStack.getBytesPerVoxel() == lStack.getBytesPerVoxel());

	}
}
