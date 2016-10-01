package net.wheatlauncher.internal;

import net.launcher.utils.resource.BackupRepository;
import net.wheatlauncher.Core;
import org.to2mbn.jmccc.version.Library;
import org.to2mbn.jmccc.version.Native;

/**
 * @author ci010
 */
public class LibraryControl
{
	private BackupRepository libRepo = new BackupRepository(Core.INSTANCE.getBackupRoot(), "libraries");
	private BackupRepository nativeRepo = new BackupRepository(Core.INSTANCE.getBackupRoot(), "natives");

	public boolean contains(Library library)
	{
		boolean result = libRepo.contains(library.getPath());
		if (library instanceof Native)
		{

		}
		return result;
	}
}
