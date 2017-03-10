package net.launcher.game.mods;

import net.launcher.utils.serial.SerializeMetadata;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author ci010
 */
public class ModParserChain implements ModParser
{
	private List<AbstractModParser> parsers;

	public ModParserChain(List<AbstractModParser> parsers)
	{
		this.parsers = parsers;
	}

	public ModContainer<?>[] parse(Path path) throws Exception
	{
		Objects.requireNonNull(path);
		if (Files.exists(path))
		{
			Map<Object, Object> context = new HashMap<>();
			SerializeMetadata.decorateWithFileInfo(context, path);
			for (AbstractModParser parser : parsers)
			{
				try
				{
					path = parser.validate(path);
					if (path != null) return parser.deserialize(path, context);
				}
				catch (Exception ignored) {}
			}
		}
		return null;
	}

}
