package net.launcher.setting;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class Setting
{
	private ObservableMap<String, SettingProperty<?>> optionObjectMap = FXCollections.observableMap(new TreeMap<>());
	private SettingType type;

	public static Setting of(SettingType type)
	{
		Objects.requireNonNull(type);
		return new Setting(type);
	}

	private Setting(SettingType type)
	{
		this.type = type;
		this.optionObjectMap = FXCollections.observableMap(type.getAllOption().stream().collect(
				Collectors.toMap(SettingType.Option::getName, option -> option.getDefaultValue(this))));
	}

	public void addOption(SettingType.Option<?>... options)
	{
		Objects.requireNonNull(options);
		for (SettingType.Option<?> option : options)
			optionObjectMap.put(option.getName(), option.getDefaultValue(this));
	}

	public SettingType getGameSettingType()
	{
		return type;
	}

	public <V> SettingProperty<V> getOption(SettingType.Option<V> s)
	{
		return (SettingProperty<V>) optionObjectMap.get(s.getName());
	}

	public <V> SettingProperty.List<V> getListProperty(SettingType.Option<ObservableList<V>> s)
	{
		return (SettingProperty.List<V>) optionObjectMap.get(s.getName());
	}
}
