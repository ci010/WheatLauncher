package net.launcher.setting;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author ci010
 */
public class GameSettingFactory
{
	private static Map<String, GameSetting> gameSettingMap = new HashMap<>();

	public static Map<String, GameSetting> getAllSetting() {return Collections.unmodifiableMap(gameSettingMap);}

	public static Optional<GameSetting> find(String id) {return Optional.ofNullable(gameSettingMap.get(id));}

	public static void register(Class<? extends GameSetting> settingClass)
	{
		GameSetting.ID annotation = settingClass.getAnnotation(GameSetting.ID.class);
		if (annotation == null)
			throw new IllegalArgumentException();
		if (gameSettingMap.containsKey(annotation.value()))
			throw new IllegalArgumentException();
		try
		{
			gameSettingMap.put(annotation.value(), settingClass.getDeclaredConstructor().newInstance());
		}
		catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
}
