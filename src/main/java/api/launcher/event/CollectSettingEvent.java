package api.launcher.event;

import api.launcher.setting.SettingType;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.*;

/**
 * @author ci010
 */
public class CollectSettingEvent extends Event
{
	public static final EventType<CollectSettingEvent> TYPE = new EventType<>(EventType.ROOT, "COLLECT_SETTING");

	private List<SettingType> types = new ArrayList<>();
	private Map<String, SettingType> lookup = new TreeMap<>();

	public CollectSettingEvent()
	{
		super(TYPE);
	}

	public boolean register(SettingType type)
	{
		if (lookup.containsKey(type.getID()))
			return false;
		lookup.put(type.getID(), type);
		types.add(type);
		return true;
	}

	public List<SettingType> getTypes() {return Collections.unmodifiableList(types);}

	public Map<String, SettingType> getLookup() {return Collections.unmodifiableMap(lookup);}
}
