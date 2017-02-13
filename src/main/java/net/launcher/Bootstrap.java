package net.launcher;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ci010
 */
public class Bootstrap
{
	private static LaunchCore current;
	private static ReentrantLock lock = new ReentrantLock();

	public static void boost(Class<? extends LaunchCore> clz) throws Exception
	{
		lock.lock();
		if (current != null)
		{
			lock.unlock();
			throw new IllegalStateException();
		}
		LaunchCore launchCore = clz.newInstance();
		current = launchCore;
		launchCore.init();
		lock.unlock();
	}

	public static void destroy() throws Exception
	{
		lock.lock();
		if (current == null)
			throw new IllegalStateException();
		current.destroy();
		current = null;
		lock.unlock();
	}

	public static LaunchCore getCore()
	{
		Objects.requireNonNull(current);
		return current;
	}
}
