package api.launcher.setting;

import javafx.beans.binding.ListExpression;
import javafx.collections.ObservableList;

import java.util.Iterator;

/**
 * @author ci010
 */
public interface SettingProperty<T> extends javafx.beans.property.Property<T>
{
	@Override
	Setting getBean();

	SettingType.Option<T> getOption();

	@Override
	String getName();

	abstract class List<T> extends ListExpression<T> implements SettingProperty<ObservableList<T>> {}

	interface Limited<T> extends SettingProperty<T>, Iterator<T> {}
}
