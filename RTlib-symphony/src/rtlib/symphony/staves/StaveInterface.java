package rtlib.symphony.staves;

import java.nio.ShortBuffer;

public interface StaveInterface
{

	boolean isCompatibleWith(StaveInterface pNewStave);

	void setNumberOfTimePoints(final int pNumberOfTimePoints);

	int getNumberOfTimePoints();

	boolean isUpToDate();

	void requestUpdate();

	void updateStaveArray();

	short[] getStaveArray();

	ShortBuffer getStaveBuffer();

	int getStaveBufferLength();

	int getTimePointFromNormalized(double pNormalizedTimePoint);

	double getNormalizedTimePoint(int pIntegerTimePoint);

	int getMaximalSignalIntegerAmplitude();


}
