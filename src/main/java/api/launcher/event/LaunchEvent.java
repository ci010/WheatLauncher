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
	public static final EventType<LaunchEvent> LAUNCH_EVENT = new EventType<>(EventType.ROOT, "LAUNCH_EVENT");
	public static final EventType<LaunchEvent.Exit> GAME_EXIT = new EventType<>(EventType.ROOT, "GAME_EXIT");

	private LaunchOption option;
	private LaunchProfile profile;

	public static LaunchEvent launch(LaunchOption option, LaunchProfile profile)
	{return new LaunchEvent(LAUNCH_EVENT, option, profile);}

	public static LaunchEvent exit(LaunchOption option, LaunchProfile profile, int exit)
	{return new LaunchEvent.Exit(option, profile, exit);}

	private LaunchEvent(EventType<?> launchEvent, LaunchOption option, LaunchProfile profile)
	{
		super(launchEvent);
		this.option = option;
		this.profile = profile;
	}

	public LaunchOption getOption() {return option;}

	public LaunchProfile getProfile() {return profile;}

	public static class Exit extends LaunchEvent
	{
		private int exitCode;

		private Exit(LaunchOption option, LaunchProfile profile, int exitCode)
		{
			super(GAME_EXIT, option, profile);
			this.exitCode = exitCode;
		}

		public int getExitCode() {return exitCode;}
	}
}
