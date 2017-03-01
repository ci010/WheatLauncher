package net.launcher.setting;

import api.launcher.ARML;
import net.launcher.io.MappedStorageType;
import net.launcher.utils.NIOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class SettingMinecraft extends SettingType
{
	public static final SettingMinecraft INSTANCE;

	static
	{
		SettingManager.register(SettingMinecraft.class);
		INSTANCE = (SettingMinecraft) SettingManager.find("Minecraft").orElse(null);
	}

	public final OptionInt
			FOV = new OptionInt(this, "fov", 70, 30, 110),
			MAXFPS = new OptionInt(this, "maxFps", 120, 10, 260).setStep(10),
			RENDER_DISTANCE = new OptionInt(this, "renderDistance", 12, 2, 32),
			MIPMAP_LEVELS = new OptionInt(this, "mipmapLevels", 4, 0, 4),
			PARTICLES = new OptionInt(this, "particles", 0, 0, 2),
			AMBIENT_OCCLUSION = new OptionInt(this, "ao", 0, 0, 2);

	public final Option<Boolean>
			ANAGLYPH3D = new OptionBoolean(this, "anaglyph3d", false),
			ENABLE_VSYNC = new OptionBoolean(this, "enableVsync", false),
			FBO_ENABLE = new OptionBoolean(this, "fboEnable", true),
			RENDER_CLOUDS = new OptionBoolean(this, "renderClouds", true),
			USE_VBO = new OptionBoolean(this, "useVbo", true),
			GRAPHIC = new OptionBoolean(this, "fancyGraphics", true),
			ENTITY_SHADOWS = new OptionBoolean(this, "entityShadows", true),
			FORCE_UNICODE = new OptionBoolean(this, "forceUnicodeFont", false);

	public final Option<String> LANGUAGE = new OptionString(this, "lang", "en_US");
	public final Option<String[]> RESOURCE_PACE = new OptionJSONArray(this, "resourcePacks");

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
		all.add(FORCE_UNICODE);
	}

	@Override
	public String getID()
	{
		return "Minecraft";
	}

	@Override
	public List<Option<?>> getAllOption()
	{
		return all;
	}

	@Override
	public Setting load(Path minecraftFolder) throws IOException
	{
		Path path = minecraftFolder.resolve("options.txt");
		if (!Files.exists(path))
			return defaultInstance();

		Setting instance = defaultInstance();
		Map<String, ? extends Option<?>> optionMap = getAllOption().stream()
				.collect(Collectors.toMap(Option::getName, Function.identity()));

		String string = NIOUtils.readToString(path);
		String property = System.getProperty("line.separator");

		String[] lines = string.split(property);
		for (String line : lines)
		{
			String[] keyVPair = line.split(":");
			Option option = optionMap.get(keyVPair[0]);
			if (option == null)
			{
				ARML.logger().warning("unable to load " + keyVPair[0]);
				continue;
			}
			SettingProperty prop = instance.getOption(option);
			if (prop == null)
			{
				ARML.logger().warning("unable to load " + keyVPair[0]);
				continue;
			}
			prop.setValue(option.deserialize(keyVPair[1]));
		}
		return instance;
	}

	@Override
	public Setting defaultInstance()
	{
		return Setting.of(this);
	}

	@Override
	public void save(Path directory, Setting setting) throws IOException
	{
		List<Option<?>> allOption = getAllOption();
		Path path = directory.resolve("options.txt");
		if (Files.exists(path))
		{
			String s = NIOUtils.readToString(path);
			Map<String, String> deserialize = MappedStorageType.MC.deserialize(s);
			for (Option<?> option : allOption)
				deserialize.put(option.getName(), option.serialize(setting.getOption(option).getValue()));
			String serialize = MappedStorageType.MC.serialize(deserialize);
			NIOUtils.writeString(path, serialize);
		}
		else
		{
			Map<String, String> deserialize = new HashMap<>();
			for (Option<?> option : allOption)
				deserialize.put(option.getName(), option.serialize(setting.getOption(option).getValue()));
			String serialize = MappedStorageType.MC.serialize(deserialize);
			NIOUtils.writeString(path, serialize);
		}
	}
}
