package net.launcher.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ci010
 */
public class AuthenticationIndicatorFactory
{
	public static final AuthenticationIndicator
			ONLINE = new OnlineAuth(),
			OFFLINE = new OfflineAuth();

	public static Optional<AuthenticationIndicator> get(String name)
	{
		for (AuthenticationIndicator indicator : list)
			if (indicator.id().equals(name))
				return Optional.of(indicator);
		return Optional.empty();
	}

	public static void register(Class<? extends AuthenticationIndicator> indicator)
	{
		try
		{
			AuthenticationIndicator i = indicator.newInstance();
			for (AuthenticationIndicator au : list)
				if (au == i || au.id().equals(i.id()))
					return;
			list.add(i);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private AuthenticationIndicatorFactory() {}

	private static List<AuthenticationIndicator> list = new ArrayList<>();

	static
	{
		list.add(ONLINE);
		list.add(OFFLINE);
	}
}
