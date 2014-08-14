package rtlib.symphony.interfaces;

import java.nio.ShortBuffer;

public interface StaveInterface
{

	boolean isCompatibleWith(StaveInterface pNewStave);

	int getStaveBufferLength();

	boolean isUpToDate();

	void requestUpdate();

	void setNumberOfTimePoints(final int pNumberOfTimePoints);

	int getNumberOfTimePoints();

	ShortBuffer getStaveBuffer();

	short[] getStaveArray();

	int getTimePointFromNormalized(double pNormalizedTimePoint);

	double getNormalizedTimePoint(int pIntegerTimePoint);

	int getMaximalSignalIntegerAmplitude();

}
