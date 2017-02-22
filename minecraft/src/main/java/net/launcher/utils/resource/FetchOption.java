package net.launcher.utils.resource;

/**
 * @author ci010
 */
public enum FetchOption
{
	/**
	 * This will copy the resource file into the target directory.
	 */
	COPY,
	/**
	 * This will newService symbolic link to the target directory. It could be used in some temporary situation.
	 */
	SYMBOL_LINK,
	/**
	 * This will newService hard link to the target directory. Notice that this cannot be created cross hard disk.
	 */
	HARD_LINK
}
