package rtlib.symphony.movement;

import java.nio.ShortBuffer;

import rtlib.symphony.staves.StaveInterface;

public interface MovementInterface
{

	String getName();

	boolean setStave(int pStaveIndex, StaveInterface pNewStave);

	StaveInterface getStave(int pStaveIndex);

	int getNumberOfStaves();

	boolean isUpToDate();

	ShortBuffer getMovementBuffer();

	int computeMovementBufferLength();

	void setDeltaTimeInMicroseconds(double pDeltaTimeInMicroseconds);

	double getDeltaTimeInMicroseconds();

	double getDurationInMilliseconds();

	int getNumberOfTimePoints();

	boolean isSync();

	boolean isSyncOnRisingEdge();

	int getSyncChannel();

}
