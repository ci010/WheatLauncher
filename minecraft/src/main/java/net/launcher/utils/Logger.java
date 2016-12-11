package net.launcher.utils;

/**
 * @author ci010
 */
public class Logger
{
	private static Manager manager = new Manager();

	private static class Manager extends SecurityManager
	{
		public Class getContext()
		{
			return getClassContext()[3];
		}

		@Override
		protected Class[] getClassContext()
		{
			return super.getClassContext();
		}
	}

	public static void traceLevel(String s, int level)
	{
		String v = "";
		Class[] classContext = manager.getClassContext();
		for (int i = 2; i <= level + 2; i++)
			v = v + "[" + classContext[i].getSimpleName() + "]";
		System.out.println(v + " " + s);
	}


	public static void trace()
	{
		System.out.println("[" + manager.getContext().getSimpleName() + "] ");
	}

	public static void trace(String log)
	{
		System.out.println("[" + manager.getContext().getSimpleName() + "] " + log);
	}

	public static void trace(Object o)
	{
		System.out.println("[" + manager.getContext().getSimpleName() + "] " + o.toString());
	}
}
