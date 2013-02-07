package score.interfaces;

import java.nio.ShortBuffer;

public interface StaveInterface
{

	boolean isCompatibleWith(StaveInterface pNewStave);

	int getStaveBufferLength();

	boolean isUpToDate();
	
	void requestUpdate();

	int getNumberOfTimePoints();

	ShortBuffer getStaveBuffer();

	short[] getStaveArray();

	int getTimePointFromNormalized(double pNormalizedTimePoint);

	double getNormalizedTimePoint(int pIntegerTimePoint);

	int getMaximalSignalIntegerAmplitude();


}
