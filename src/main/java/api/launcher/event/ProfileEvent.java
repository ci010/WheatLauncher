package api.launcher.event;

import api.launcher.LaunchProfile;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author ci010
 */
public class ProfileEvent extends Event
{
	public static final EventType<ProfileEvent> CREATE = new EventType<>(EventType.ROOT, "PROFILE_CREATE");
	public static final EventType<ProfileEvent> DELETE = new EventType<>(EventType.ROOT, "PROFILE_DELETE");
	public static final EventType<ProfileEvent> VERSION_CHANGE = new EventType<>(EventType.ROOT, "VERSION_CHANGE");

	private LaunchProfile profile;

	public ProfileEvent(LaunchProfile profile, EventType<ProfileEvent> eventEventType)
	{
		super(null, null, eventEventType);
		this.profile = profile;
	}

	public LaunchProfile getProfile() {return profile;}
}
