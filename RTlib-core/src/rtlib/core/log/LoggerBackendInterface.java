package rtlib.core.log;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface LoggerBackendInterface extends Closeable
{

	void setLogFile(File lLogFile) throws IOException;
	
	File getLogFile();
	File getLogDataFile();

	void logMessage(String pType, Class<?> pClass, String pMessage);

	void flush();


	
}
