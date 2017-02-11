package net.launcher.setting;

import javafx.beans.binding.ListExpression;
import javafx.collections.ObservableList;

/**
 * @author ci010
 */
public interface GameSettingProperty<T> extends javafx.beans.property.Property<T>
{
	@Override
	GameSetting getBean();

	GameSettingType.Option<T> getOption();

	@Override
	String getName();

	abstract class List<T> extends ListExpression<T> implements GameSettingProperty<ObservableList<T>> {}
}
