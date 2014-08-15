package rtlib.kam.memory.ram;

import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;
import rtlib.kam.memory.PointerAccessible;
import rtlib.kam.memory.impl.direct.RAMDirect;

public interface RAM extends
										PointerAccessible,
										ReadAtAligned,
										WriteAtAligned,
										ReadAt,
										WriteAt,
										SizedInBytes,
										Freeable
{

	RAMDirect subRegion(long pOffset, long pLenghInBytes);

}
