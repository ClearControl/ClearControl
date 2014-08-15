package rtlib.kam.memory.impl.direct.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.kam.memory.cursor.NDCursor;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;

public class NDArrayTypedDirectTests
{

	@Test
	public void testSlicing()
	{
		NDArrayTypedDirect<Short> lNDArrayTypedDirect = NDArrayTypedDirect.allocateTVND(short.class,
																																										1,
																																										10,
																																										7,
																																										3);
		NDCursor lDefaultCursor = lNDArrayTypedDirect.getDefaultCursor();

		lDefaultCursor.setCursorPosition(0, 0);
		lDefaultCursor.setCursorPosition(1, 2);
		lDefaultCursor.setCursorPosition(2, 2);
		lDefaultCursor.setCursorPosition(3, 2);

		lNDArrayTypedDirect.setShortAtCursor((short) 1234);

		NDArrayTypedDirect<Short> lSliceMajorAxis = lNDArrayTypedDirect.sliceMajorAxis(2);

		NDCursor lDefaultCursor2 = lSliceMajorAxis.getDefaultCursor();

		lDefaultCursor2.setCursorPosition(0, 0);
		lDefaultCursor2.setCursorPosition(1, 2);
		lDefaultCursor2.setCursorPosition(2, 2);

		short lShortAtCursor = lSliceMajorAxis.getShortAtCursor();
		System.out.println(lShortAtCursor);

		assertTrue(lShortAtCursor == 1234);
		

	}
}
