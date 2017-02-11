package net.launcher.setting;

import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class GameSetting
{
	private Map<String, GameSettingProperty<?>> optionObjectMap = new TreeMap<>();
	private GameSettingType type;

	public static GameSetting of(GameSettingType type, List<GameSettingProperty<?>> properties)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(properties);
		return new GameSetting(type, properties);
	}

	private GameSetting(GameSettingType type, List<GameSettingProperty<?>> properties)
	{
		this.type = type;
		for (GameSettingProperty<?> property : properties) optionObjectMap.put(property.getName(), property);
	}

	public GameSettingType getGameSettingType()
	{
		return type;
	}

	public <V> GameSettingProperty<V> getOption(GameSettingType.Option<V> s)
	{
		return (GameSettingProperty<V>) optionObjectMap.get(s.getName());
	}

	public <V> GameSettingProperty.List<V> getListProperty(GameSettingType.Option<ObservableList<V>> s)
	{
		return (GameSettingProperty.List<V>) optionObjectMap.get(s.getName());
	}

	public void loadFrom(GameSetting settingInstance)
	{
		Objects.requireNonNull(settingInstance);
		if (settingInstance.getGameSettingType() != type) throw new IllegalArgumentException("wrong type!");
		this.optionObjectMap.clear();
		this.optionObjectMap.putAll(settingInstance.optionObjectMap);
	}
}
