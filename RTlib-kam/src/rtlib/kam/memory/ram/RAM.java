package rtlib.kam.memory.ram;

import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;
import rtlib.kam.memory.PointerAccessible;


public interface RAM extends
										PointerAccessible,
										ReadAtAligned,
										WriteAtAligned,
										ReadAt,
										WriteAt,
										SizedInBytes,
										Freeable
{

}
