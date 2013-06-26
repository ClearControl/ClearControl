package file;

import java.io.File;

import file.FileEventNotifier.FileEventKind;

public interface FileEventNotifierListener
{

	void fileEvent(	FileEventNotifier pThis,
									File pFile,
									FileEventKind pEventKind);

}
