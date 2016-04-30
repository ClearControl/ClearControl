package clearcontrol.core.file;

import java.io.File;

import clearcontrol.core.file.FileEventNotifier.FileEventKind;

public interface FileEventNotifierListener
{

	void fileEvent(	FileEventNotifier pThis,
									File pFile,
									FileEventKind pEventKind);

}
