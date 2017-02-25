package api.launcher.event;

import api.launcher.LaunchProfile;
import javafx.event.Event;
import javafx.event.EventType;
import org.to2mbn.jmccc.option.LaunchOption;

/**
 * @author ci010
 */
public class LaunchEvent extends Event
{
	public static EventType<LaunchEvent> LAUNCH_EVENT = new EventType<>(EventType.ROOT, "LAUNCH_EVENT");

	private LaunchOption option;
	private LaunchProfile profile;

	public LaunchEvent(LaunchOption option, LaunchProfile profile)
	{
		super(LAUNCH_EVENT);
		this.option = option;
		this.profile = profile;
	}

	public LaunchOption getOption() {return option;}

	public LaunchProfile getProfile() {return profile;}
}
