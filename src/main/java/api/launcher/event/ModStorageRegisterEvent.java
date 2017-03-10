package api.launcher.event;

import javafx.event.Event;
import javafx.event.EventType;
import net.launcher.game.mods.AbstractModParser;
import net.launcher.game.mods.ModContainer;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.serial.BiSerializer;

import java.util.*;

/**
 * @author ci010
 */
public class ModStorageRegisterEvent extends Event
{
	public static EventType<ModStorageRegisterEvent> TYPE = new EventType<>(EventType.ROOT, "MOD_STORAGE");

	private Map<String, BiSerializer<ModContainer<?>, NBTCompound>> nbtSerial = new TreeMap<>();
	private List<AbstractModParser> parsers = new ArrayList<>();

	public ModStorageRegisterEvent() {super(TYPE);}

	public boolean register(String id, BiSerializer<ModContainer<?>, NBTCompound> serializer, AbstractModParser parser)
	{
		Objects.requireNonNull(id);
		Objects.requireNonNull(serializer);
		Objects.requireNonNull(parser);
		if (nbtSerial.containsKey(id) || parsers.contains(parser))
			return false;
		nbtSerial.put(id, serializer);
		parsers.add(parser);
		return true;
	}

	public Map<String, BiSerializer<ModContainer<?>, NBTCompound>> getNbtSerial()
	{
		return Collections.unmodifiableMap
				(nbtSerial);
	}

	public List<AbstractModParser> getParsers() {return Collections.unmodifiableList(parsers);}
}
