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

	int getMaxNumberOfTimePointsPerBuffer();
	
	int getNumberOfTimePoints();
	
	int getNumberOfStaves();

	String getName();







}
