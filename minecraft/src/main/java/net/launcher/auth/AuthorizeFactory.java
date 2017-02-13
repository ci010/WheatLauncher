package net.launcher.auth;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class AuthorizeFactory
{
	private static Map<String, Authorize> authorizeList = new TreeMap<>();
	public static final Authorize ONLINE, OFFLINE;

	public static Optional<Authorize> find(String id)
	{
		return Optional.ofNullable(authorizeList.get(id));
	}

	public static Map<String, Authorize> getAuthorizeMap() {return Collections.unmodifiableMap(authorizeList);}

	public static void register(Class<? extends Authorize> authClass)
	{
		Authorize.ID annotation = authClass.getAnnotation(Authorize.ID.class);
		if (annotation == null)
			throw new IllegalArgumentException();
		if (authorizeList.containsKey(annotation.value()))
			throw new IllegalArgumentException();
		try
		{
			authorizeList.put(annotation.value(), authClass.getDeclaredConstructor().newInstance());
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	static
	{
		register(AuthOnline.class);
		register(AuthOffline.class);
		ONLINE = find("online").get();
		OFFLINE = find("offline").get();
	}

	private AuthorizeFactory() {}
}
