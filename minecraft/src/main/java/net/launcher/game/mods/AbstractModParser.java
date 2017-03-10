package net.launcher.game.mods;

import net.launcher.utils.Patterns;
import net.launcher.utils.serial.Deserializer;
import net.launcher.utils.serial.SerializeMetadata;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ci010
 */
public abstract class AbstractModParser implements Deserializer<ModContainer<?>[], Path>, ModParser
{
	public ModContainer<?>[] parse(Path path) throws Exception
	{
		Objects.requireNonNull(path);
		if (Files.exists(path))
		{
			Map<Object, Object> context = new HashMap<>();
			SerializeMetadata.decorateWithFileInfo(context, path);
			path = validate(path);
			if (path != null) return deserialize(path, context);
		}
		return null;
	}

	public Path validate(Path path) throws IOException
	{
		if (Patterns.ZIP_JAR.matcher(path.getFileName().toString()).matches())
		{
			FileSystem fileSystem = FileSystems.newFileSystem(path, this.getClass().getClassLoader());
			path = fileSystem.getPath("/");
		}
		return path;
	}
}
