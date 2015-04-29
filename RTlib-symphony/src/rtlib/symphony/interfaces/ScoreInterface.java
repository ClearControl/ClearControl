package rtlib.symphony.interfaces;

import java.util.ArrayList;

import rtlib.symphony.movement.Movement;

public interface ScoreInterface
{

	public abstract int getNumberOfMovements();

	public abstract ArrayList<MovementInterface> getMovements();

	public abstract boolean isUpToDate();

	public abstract void clear();

	public abstract void removeMovementAt(final int pIndex);

	public abstract void addMovementAt(final int pIndex, final MovementInterface pMovement);

	public abstract void addMovementMultipleTimes(	final Movement pMovement,
																										final int pNumberOfTimes);

	public abstract boolean addMovement(final MovementInterface pMovement);

}
