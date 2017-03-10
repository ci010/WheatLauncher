package net.launcher.game.mods.liteloader;

import net.launcher.game.mods.AbstractModParser;
import net.launcher.game.mods.ModContainer;
import net.launcher.utils.NIOUtils;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author ci010
 */
public class LiteModParser extends AbstractModParser
{
	@Override
	public Path validate(Path path) throws IOException
	{
		if (!path.endsWith(".litemod"))
			return null;
		FileSystem fileSystem = FileSystems.newFileSystem(path, this.getClass().getClassLoader());
		path = fileSystem.getPath("/");
		return path;
	}

	@Override
	public ModContainer<?>[] deserialize(Path path, Map<Object, Object> context) throws Exception
	{
		Path meta = path.resolve("litemod.json");
		LiteModMetaData metaData = LiteModMetaData.deserializer().deserialize(new JSONObject(NIOUtils.readToString(meta)));
		return new ModContainer[]{LiteModMetaData.createMod(metaData)};
	}
}
