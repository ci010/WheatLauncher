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
	private Map<Class<? extends SettingType>, SettingType> lookup = new HashMap<>();

	public CollectSettingEvent()
	{
		super(TYPE);
	}

	public <T extends SettingType, R extends T> boolean register(Class<T> clz, R type)
	{
		if (lookup.containsKey(clz))
			return false;
		lookup.put(clz, type);
		types.add(type);
		return true;
	}

	public List<SettingType> getTypes() {return Collections.unmodifiableList(types);}

	public Map<Class<? extends SettingType>, SettingType> getLookup() {return Collections.unmodifiableMap(lookup);}
}
