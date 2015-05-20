package rtlib.symphony.staves;


public class EdgeStave extends StaveAbstract	implements
																								StaveInterface
{

	private volatile float mEdgePosition = 0.5f;
	private volatile float mValueBefore;
	private volatile float mValueAfter;

	public EdgeStave(	final String pName,
										float pEdgePosition,
										float mValueBefore,
										float mValueAfter)
	{
		super(pName);
		setEdgePosition(pEdgePosition);
		this.setValueBefore(mValueBefore);
		this.setValueAfter(mValueAfter);
	}

	@Override
	public float getValue(float pNormalizedTime)
	{
		if (pNormalizedTime > getEdgePosition())
			return getValueAfter();
		else
			return getValueBefore();
	}

	public float getEdgePosition()
	{
		return mEdgePosition;
	}

	public void setEdgePosition(float pEdgePosition)
	{
		mEdgePosition = pEdgePosition;
	}

	public float getValueBefore()
	{
		return mValueBefore;
	}

	public void setValueBefore(float pValueBefore)
	{
		mValueBefore = pValueBefore;
	}

	public float getValueAfter()
	{
		return mValueAfter;
	}

	public void setValueAfter(float pValueAfter)
	{
		mValueAfter = pValueAfter;
	}

	@Override
	public StaveInterface copy()
	{
		return new EdgeStave(	getName(),
													getEdgePosition(),
													getValueBefore(),
													getValueAfter());
	}

}
