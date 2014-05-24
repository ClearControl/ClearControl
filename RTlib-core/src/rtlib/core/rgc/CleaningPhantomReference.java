package rtlib.core.rgc;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

class CleaningPhantomReference extends PhantomReference<Cleanable>
{

	private final Cleaner mCleaner;

	public CleaningPhantomReference(Cleanable pReferent,
																	Cleaner pCleaner,
																	ReferenceQueue<Cleanable> pReferencenQueue)
	{
		super(pReferent, pReferencenQueue);
		mCleaner = pCleaner;
	}

	public Cleaner getCleaner()
	{
		return mCleaner;
	}

}
