package net.launcher;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import net.launcher.auth.AuthenticationIndicator;
import net.launcher.setting.Option;
import net.launcher.utils.StatedProperty;
import net.launcher.utils.StrictProperty;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.LaunchOption;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;
import org.to2mbn.jmccc.util.Builder;

import java.util.Map;
import java.util.Optional;

/**
 * @author ci010
 */
public interface ILaunchProfile extends Builder<LaunchOption>, Observable
{
	Property<String> nameProperty();

	StatedProperty<WindowSize> resolutionProperty();

	StatedProperty<String> versionProperty();

	StatedProperty<Number> memoryProperty();

	StatedProperty<MinecraftDirectory> minecraftProperty();

	StatedProperty<JavaEnvironment> javaProperty();

	StrictProperty<String> accountProperty();

	StrictProperty<String> passwordProperty();

	ObservableValue<String> clientToken();

	ObservableValue<String> accessToken();

	<T> Property<T> createSettingIfAbsent(Option<T> option);

	<T> Optional<Property<T>> getSetting(Option<T> option);

	Map<Option<?>, Property<?>> getAllOption();

	Property<AuthenticationIndicator> authProperty();

	void onApply();

	void onDispose();
}
