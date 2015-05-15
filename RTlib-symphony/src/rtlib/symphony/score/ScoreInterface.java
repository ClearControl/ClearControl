package rtlib.symphony.score;

import java.util.ArrayList;

import rtlib.symphony.movement.MovementInterface;

public interface ScoreInterface
{


	public abstract int getNumberOfMovements();

	public abstract ArrayList<MovementInterface> getMovements();

	public abstract boolean isUpToDate();

	public abstract void clear();

	public abstract void removeMovementAt(final int pIndex);

	public abstract void insertMovementAt(final int pIndex, final MovementInterface pMovement);

	public abstract void addMovementMultipleTimes(final MovementInterface pMovement,
																										final int pNumberOfTimes);

	public abstract void addMovement(final MovementInterface pMovement);

	public abstract MovementInterface getMovement(int pMovementIndex);

	public abstract long getTotalNumberOfTimePoints();

	public abstract int getMaxNumberOfStaves();

}
