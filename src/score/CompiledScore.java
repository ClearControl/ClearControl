package score;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import score.interfaces.MovementInterface;
import score.interfaces.ScoreInterface;

public class CompiledScore 
{
	private ArrayList<CompiledMovement> mCompiledMovementList = new ArrayList<CompiledMovement>();
	private String mName;

	public CompiledScore(final String pName)
	{
		mName = pName;
	}

	public void addMovement(MovementInterface pMovement)
	{
		CompiledMovement lCompiledMovement = new CompiledMovement(pMovement);
		mCompiledMovementList.add(lCompiledMovement);
	}
	
	public void clear()
	{
		mCompiledMovementList.clear();
	}

	public ArrayList<CompiledMovement> getMovements()
	{
		return mCompiledMovementList;
	}

	public int getNumberOfMovements()
	{
		return mCompiledMovementList.size();
	}

}
