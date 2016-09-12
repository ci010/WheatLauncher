package net.wheatlauncher.internal.repository;

import org.to2mbn.jmccc.version.Library;
import org.to2mbn.jmccc.version.Native;

/**
 * @author ci010
 */
public class LibraryControl
{
	private BackupRepository libRepo = new BackupRepository("libraries");
	private BackupRepository nativeRepo = new BackupRepository("natives");

	public boolean contains(Library library)
	{
		boolean result = libRepo.contains(library.getPath());
		if (library instanceof Native)
		{

		}
		return result;
	}
}
