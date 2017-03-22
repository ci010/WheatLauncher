package api.launcher.event;

import api.launcher.auth.Authorize;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class RegisterAuthEvent extends Event
{
	public static final EventType<RegisterAuthEvent> TYPE = new EventType<>(EventType.ROOT, "REGISTER_AUTH");

	private Map<String, Authorize> registered = new TreeMap<>();

	public RegisterAuthEvent()
	{
		super(TYPE);
	}

	public Map<String, Authorize> getRegistered() {return registered;}

	public boolean register(Authorize authorize)
	{
		if (registered.containsKey(authorize.getId()))
			return false;
		registered.put(authorize.getId(), authorize);
		return true;
	}
}
