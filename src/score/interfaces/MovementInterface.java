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

	int getMaxNumberOfTimePointsPerMovement();
	
	int getNumberOfTimePoints();
	
	int getNumberOfStaves();

	String getName();







}
