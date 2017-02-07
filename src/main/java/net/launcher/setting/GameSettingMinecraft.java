package net.launcher.setting;

import net.launcher.io.MappedStorageType;
import net.launcher.utils.NIOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
@GameSetting.ID("Minecraft")
public class GameSettingMinecraft extends GameSetting
{
	public static final GameSettingMinecraft INSTANCE;

	static
	{
		GameSettingFactory.register(GameSettingMinecraft.class);
		INSTANCE = (GameSettingMinecraft) GameSettingFactory.find("Minecraft").orElse(null);
	}

	public final IntOption
			FOV = new IntOption(this, "fov", 70, 30, 110),
			MAXFPS = new IntOption(this, "maxFps", 120, 10, 260).setStep(10),
			RENDER_DISTANCE = new IntOption(this, "renderDistance", 12, 2, 32),
			MIPMAP_LEVELS = new IntOption(this, "mipmapLevels", 4, 0, 4),
			PARTICLES = new IntOption(this, "particles", 0, 0, 2),
			AMBIENT_OCCLUSION = new IntOption(this, "ao", 0, 0, 2);

	public final Option<Boolean>
			ANAGLYPH3D = new BooleanOption(this, "anaglyph3d", false),
			ENABLE_VSYNC = new BooleanOption(this, "enableVsync", false),
			FBO_ENABLE = new BooleanOption(this, "fboEnable", true),
			RENDER_CLOUDS = new BooleanOption(this, "renderClouds", true),
			USE_VBO = new BooleanOption(this, "useVbo", true),
			GRAPHIC = new BooleanOption(this, "fancyGraphics", true),
			ENTITY_SHADOWS = new BooleanOption(this, "entityShadows", true);

	public final Option<String> LANGUAGE = new StringOption(this, "lang", "en_US");
	public final Option<String[]> RESOURCE_PACE = new StringArrayOption(this, "resourcePacks");

	private final List<Option<?>> all = new ArrayList<>();

	{
		all.add(FOV);
		all.add(MAXFPS);
		all.add(RENDER_CLOUDS);
		all.add(MIPMAP_LEVELS);
		all.add(PARTICLES);
		all.add(AMBIENT_OCCLUSION);
		all.add(ANAGLYPH3D);
		all.add(ENABLE_VSYNC);
		all.add(FBO_ENABLE);
		all.add(RENDER_DISTANCE);
		all.add(USE_VBO);
		all.add(GRAPHIC);
		all.add(ENTITY_SHADOWS);
		all.add(LANGUAGE);
		all.add(RESOURCE_PACE);
	}

	@Override
	public List<Option<?>> getAllOption()
	{
		return all;
	}

	@Override
	public GameSettingInstance load(Path directory) throws IOException
	{
		Path path = directory.resolve("options.txt");
		if (!Files.exists(path))
			throw new FileNotFoundException();
		String string = NIOUtils.readToString(path);
		GameSettingInstance instance = new GameSettingInstance(this);
		Map<String, ? extends Option<?>> collect = getAllOption().stream().collect(Collectors.toMap(Option::getName, v -> v));
		Map<String, String> deserialize = MappedStorageType.MC.deserialize(string);
		for (Map.Entry<String, String> entry : deserialize.entrySet())
		{
			Option option = collect.get(entry.getKey());
			if (option != null)
				instance.setOption(option, option.deserialize(entry.getValue()));
		}
		return instance;
	}

	@Override
	public void save(Path directory, GameSettingInstance setting) throws IOException
	{
		List<Option<?>> allOption = getAllOption();
		Path path = directory.resolve("options.txt");
		if (!Files.exists(path))
		{
			Map<String, String> deserialize = MappedStorageType.MC.deserialize(NIOUtils.readToString(path));
			for (Option<?> option : allOption)
				deserialize.put(option.getName(), setting.getOption(option).toString());
			MappedStorageType.MC.serialize(deserialize);
		}
	}

	@Override
	public void saveTemplate(Path templateRoot, GameSettingInstance.Template instance) throws IOException
	{

	}

	@Override
	public GameSettingInstance.Template loadTemplate(Path templateRoot, String template) throws IOException
	{
		return null;
	}


}
