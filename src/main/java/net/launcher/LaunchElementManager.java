package net.launcher;

import javafx.collections.ObservableList;
import net.launcher.profile.LaunchProfile;
import org.to2mbn.jmccc.option.LaunchOption;

/**
 * @author ci010
 */
public interface LaunchElementManager<T> extends LaunchHandler
{
	ObservableList<T> getAllElement();

	ObservableList<T> getIncludeElementContainer(LaunchProfile profile);

	void onLaunch(LaunchOption option, LaunchProfile profile);

	void onClose(LaunchOption option, LaunchProfile profile);
}
