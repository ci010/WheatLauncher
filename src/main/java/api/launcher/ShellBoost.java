package api.launcher;

import net.launcher.impl.ShellImpl;

/**
 * @author ci010
 */
class ShellBoost
{
	private static final Shell _ST = new ShellImpl();

	static Shell getInstance()
	{
		return _ST;
	}
}
