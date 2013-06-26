package score.interfaces;

import java.nio.ShortBuffer;

public interface MovementInterface
{

	boolean isUpToDate();

	ShortBuffer getMovementBuffer();

	int computeMovementBufferLength();

	double getDeltaTimeInMicroseconds();

	void setDeltaTimeInMicroseconds(double pDeltaTimeInMicroseconds);

	double getDurationInMilliseconds();

	int getNumberOfTimePoints();

	int getNumberOfStaves();

	boolean isSync();

	boolean isSyncOnRisingEdge();

	int getSyncChannel();

	String getName();

}
