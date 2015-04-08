package rtlib.stack.processor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;

import rtlib.core.variable.VariableListener;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.AsynchronousPoolStackProcessorPipeline;
import rtlib.stack.processor.SameTypeStackProcessorInterface;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

public class AsynchronousPoolStackProcessorPipelineTests
{

	@Test
	public void test() throws InterruptedException
	{
		final AsynchronousPoolStackProcessorPipeline<UnsignedShortType, ShortOffHeapAccess> lAsynchronousPoolStackProcessorPipeline = new AsynchronousPoolStackProcessorPipeline<UnsignedShortType, ShortOffHeapAccess>("Test",
																																																																																																										10,
																																																																																																										4);

		final ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess>();

		final RecyclerInterface<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> lRecycler0 = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(	lOffHeapPlanarStackFactory,
																																																																																																																						10);

		final SameTypeStackProcessorInterface<UnsignedShortType, ShortOffHeapAccess> lStackProcessor1 = new SameTypeStackProcessorInterface<UnsignedShortType, ShortOffHeapAccess>()
		{

			@Override
			public void setActive(boolean pIsActive)
			{

			}

			@Override
			public boolean isActive()
			{
				return true;
			}

			@Override
			public StackInterface<UnsignedShortType, ShortOffHeapAccess> process(	StackInterface<UnsignedShortType, ShortOffHeapAccess> pStack,
																																						RecyclerInterface<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> pStackRecycler)
			{

				final StackInterface<UnsignedShortType, ShortOffHeapAccess> lNewStack = pStackRecycler.getOrWait(	1,
																																																					TimeUnit.SECONDS,
																																																					StackRequest.buildFrom(pStack));

				lNewStack.getContiguousMemory()
									.setByteAligned(0,
																	(byte) (pStack.getContiguousMemory()
																								.getByteAligned(0) + 1));
				pStack.release();
				return lNewStack;
			}
		};

		final SameTypeStackProcessorInterface<UnsignedShortType, ShortOffHeapAccess> lStackProcessor2 = new SameTypeStackProcessorInterface<UnsignedShortType, ShortOffHeapAccess>()
		{

			@Override
			public void setActive(boolean pIsActive)
			{

			}

			@Override
			public boolean isActive()
			{
				return true;
			}

			@Override
			public StackInterface<UnsignedShortType, ShortOffHeapAccess> process(	StackInterface<UnsignedShortType, ShortOffHeapAccess> pStack,
																																						RecyclerInterface<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> pStackRecycler)
			{
				final StackInterface<UnsignedShortType, ShortOffHeapAccess> lNewStack = pStackRecycler.getOrWait(	1,
																																																					TimeUnit.SECONDS,
																																																					StackRequest.buildFrom(pStack));

				lNewStack.getContiguousMemory()
									.setByteAligned(0,
																	(byte) (pStack.getContiguousMemory()
																								.getByteAligned(0) + 1));
				pStack.release();
				return lNewStack;
			}
		};

		lAsynchronousPoolStackProcessorPipeline.addStackProcessor(lStackProcessor1,
																															lOffHeapPlanarStackFactory,
																															10);

		lAsynchronousPoolStackProcessorPipeline.addStackProcessor(lStackProcessor2,
																															lOffHeapPlanarStackFactory,
																															10);

		assertTrue(lAsynchronousPoolStackProcessorPipeline.open());

		assertTrue(lAsynchronousPoolStackProcessorPipeline.start());

		lAsynchronousPoolStackProcessorPipeline.getOutputVariable()
																						.addListener(new VariableListener<StackInterface<UnsignedShortType, ShortOffHeapAccess>>()
																						{

																							@Override
																							public void setEvent(	StackInterface<UnsignedShortType, ShortOffHeapAccess> pCurrentValue,
																																		StackInterface<UnsignedShortType, ShortOffHeapAccess> pNewValue)
																							{
																								assertEquals(	2,
																															pNewValue.getContiguousMemory()
																																				.getByteAligned(0),
																															0);

																								pNewValue.release();
																							}

																							@Override
																							public void getEvent(StackInterface<UnsignedShortType, ShortOffHeapAccess> pCurrentValue)
																							{

																							}
																						});

		for (int i = 0; i < 1000; i++)
		{
			final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStack = lRecycler0.getOrWait(1,
																																																TimeUnit.SECONDS,
																																																StackRequest.build(	new UnsignedShortType(),
																																																										12,
																																																										13,
																																																										14));
			// System.out.println(lStack);
			lStack.getContiguousMemory().setByteAligned(0, (byte) 0);

			lAsynchronousPoolStackProcessorPipeline.getInputVariable()
																							.set(lStack);

			Thread.sleep(1);
		}

		assertTrue(lAsynchronousPoolStackProcessorPipeline.stop());

		assertTrue(lAsynchronousPoolStackProcessorPipeline.close());

	}
}
