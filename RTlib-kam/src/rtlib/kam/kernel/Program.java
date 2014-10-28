package rtlib.kam.kernel;

import java.io.IOException;
import java.net.URL;

import rtlib.core.rgc.Freeable;

public interface Program<T> extends Freeable
{

	void setProgramString(String pProgramString);

	void setProgramStringFromURL(URL pProgramURL) throws IOException;

	void setProgramStringFromRessource(	Class<?> pRootClass,
																			String pRessourceName) throws IOException;

	boolean isUpToDate();

	void execute(	String pFunctionName,
								int[] pRange,
								int[] pLocalRange,
								Object... args);

	void execute(String pFunctionName, int[] pRange, Object... pArgs);

}
