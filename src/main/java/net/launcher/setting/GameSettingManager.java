package net.launcher.setting;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author ci010
 */
public class GameSettingManager
{
	private static Map<String, GameSettingType> gameSettingMap = new HashMap<>();

	public static Map<String, GameSettingType> getAllSetting() {return Collections.unmodifiableMap(gameSettingMap);}

	public static Optional<GameSettingType> find(String id) {return Optional.ofNullable(gameSettingMap.get(id));}

	public static void register(Class<? extends GameSettingType> settingClass)
	{
		try
		{
			GameSettingType gameSetting = settingClass.getDeclaredConstructor().newInstance();
			String id = gameSetting.getID();
			if (gameSettingMap.containsKey(id))
				throw new IllegalArgumentException();
			gameSettingMap.put(id, gameSetting);
		}
		catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
}
