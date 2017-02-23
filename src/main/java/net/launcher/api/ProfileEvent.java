package net.launcher.api;

import javafx.event.Event;
import javafx.event.EventType;
import net.launcher.profile.LaunchProfile;

/**
 * @author ci010
 */
public class ProfileEvent extends Event
{
	public static final EventType<ProfileEvent> CREATE = new EventType<>(EventType.ROOT, "PROFILE_CREATE");
	public static final EventType<ProfileEvent> DELETE = new EventType<>(EventType.ROOT, "PROFILE_DELETE");

	private LaunchProfile profile;

	public ProfileEvent(LaunchProfile profile, EventType<ProfileEvent> eventEventType)
	{
		super(null, null, eventEventType);
		this.profile = profile;
	}

	public LaunchProfile getProfile() {return profile;}
}
