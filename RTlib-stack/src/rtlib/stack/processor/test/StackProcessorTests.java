package rtlib.stack.processor.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackProcessorBase;
import rtlib.stack.processor.StackProcessorInterface;

public class StackProcessorTests
{

	private static final int cMaximalNumberOfAvailableObjects = 10;

	@Test
	public void test()
	{
		final StackProcessorInterface<Short, Short> lStackProcessor = new StackProcessorBase<Short, Short>("Test")
		{

			Recycler<Stack<Short>, StackRequest<Short>> mRelayRecycler = new Recycler<Stack<Short>, StackRequest<Short>>(	Stack.class,
																																													10);

			@Override
			public Stack<Short> process(final Stack<Short> pStack,
																	final Recycler<Stack<Short>, StackRequest<Short>> pStackRecycler)
			{

				final StackRequest lStackRequest = StackRequest.build(pStack.getType(),
																															1,
																															pStack.getWidth(),
																															pStack.getHeight(),
																															1);

				final Stack<Short> lNewStack = mRelayRecycler.waitOrRequestRecyclableObject(1L,
																																										TimeUnit.MILLISECONDS,
																																										lStackRequest);
				assertTrue(lNewStack != null);
				lNewStack.copyMetaDataFrom(pStack);
				pStackRecycler.release(pStack);
				return lNewStack;
			}

		};

		final Recycler<Stack<Short>, StackRequest<Short>> mStartRecycler = new Recycler<Stack<Short>, StackRequest<Short>>(	Stack.class,
																																																												cMaximalNumberOfAvailableObjects);

		final Stack<Short> lStack = mStartRecycler.failOrRequestRecyclableObject(StackRequest.build(	short.class,
																																																															1L,
																																																															10L,
																																																															10L,
																																																															10L));
		assertTrue(lStack.getBytesPerVoxel() == 2);

		final Stack<Short> lProcessedStack = lStackProcessor.process(	lStack,
																																	mStartRecycler);

		assertFalse(lProcessedStack == lStack);

		assertTrue(lProcessedStack.getDepth() == 1);
		assertTrue(lProcessedStack.getBytesPerVoxel() == lStack.getBytesPerVoxel());

	}
}
