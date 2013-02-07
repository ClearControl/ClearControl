package score.interfaces;

import java.nio.ShortBuffer;

public interface MovementInterface
{

	boolean isUpToDate();

	ShortBuffer getMovementBuffer();

	int computeMovementBufferLength();

}
