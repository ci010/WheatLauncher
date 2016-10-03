package net.launcher.game;

import net.launcher.game.setting.*;
import net.launcher.io.MappedStorageType;
import net.launcher.io.SourceObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author ci010
 */
public class GameSettings
{
	private static SourceObject.Prototype
			MC_OPTION = new SourceObject.Prototype("option.txt", MappedStorageType.MC),
			FORGE_MODS = new SourceObject.Prototype("mods.json", MappedStorageType.JSON),
			OPTIFINE = new SourceObject.Prototype("optionsof.txt", MappedStorageType.MC),
			SHADER = new SourceObject.Prototype("optionsshaders.txt", MappedStorageType.PROPERTIES);

	static
	{
		registerNewSource(MC_OPTION);
		registerNewSource(FORGE_MODS);
		registerNewSource(OPTIFINE);
		registerNewSource(SHADER);
	}

	private static List<SourceObject.Prototype> others = new ArrayList<>();
	private static List<SourceObject.Prototype> view = Collections.unmodifiableList(others);

	public static Class<GameSettings> registerNewSource(SourceObject.Prototype prototype)
	{
		if (!others.contains(prototype))
			others.add(prototype);
		else throw new IllegalArgumentException();
		return GameSettings.class;
	}

	public static List<SourceObject.Prototype> getSourcesView()
	{
		return view;
	}

	private static List<Option<?>> ALL = new ArrayList<>();
	public static final Option<Integer>
			FOV = new IntOption(MC_OPTION, "fov", 70, 30, 110),
			MAXFPS = new IntOption(MC_OPTION, "maxFps", 120, 10, 260),
			RENDER_DISTANCE = new IntOption(MC_OPTION, "renderDistance", 12, 2, 16);

	public static final IntOption.Step
			MIPMAP_LEVELS = new IntOption.Step(MC_OPTION, "mipmapLevels", 4, 0, 4, 1).setBound(true),
			PARTICLES = new IntOption.Step(MC_OPTION, "particles", 0, 0, 2, 1).setBound(true),
			AMBIENT_OCCLUSION = new IntOption.Step(MC_OPTION, "ao", 0, 0, 2, 1).setBound(true);

	public static final Option<Boolean>
			ANAGLYPH3D = new BooleanOption(MC_OPTION, "anaglyph3d", false),
			ENABLE_VSYNC = new BooleanOption(MC_OPTION, "enableVsync", false),
			FBO_ENABLE = new BooleanOption(MC_OPTION, "fboEnable", true),
			RENDER_CLOUDS = new BooleanOption(MC_OPTION, "renderClouds", true),
			USE_VBO = new BooleanOption(MC_OPTION, "useVbo", true),
			GRAPHIC = new BooleanOption(MC_OPTION, "fancyGraphics", true),
			ENTITY_SHADOWS = new BooleanOption(MC_OPTION, "entityShadows", true);

	public static final Option<String> LANGUAGE = new StringOption(MC_OPTION, "lang", "en_US");
	public static final Option<String[]> RESOURCE_PACE = new StringArrayOption(MC_OPTION, "resourcePacks", new
			String[0]);

	public static void registerOption(Option<?> option)
	{
		if (!ALL.contains(option))
			ALL.add(option);
	}

	public static List<Option<?>> getOptionBySource(SourceObject sourceObject)
	{
		return ALL.stream().filter(option -> option.getSourceType().isTypeOf(sourceObject)).collect(Collectors.toList());
	}


	public static List<Option<?>> getAllOptions()
	{
		return null;
	}

	static
	{
		ALL.add(GRAPHIC);
		ALL.add(RENDER_DISTANCE);

		ALL.add(AMBIENT_OCCLUSION);
		ALL.add(MAXFPS);

		ALL.add(ANAGLYPH3D);//red blue 3d effect

		ALL.add(FOV);
		ALL.add(MIPMAP_LEVELS);
		ALL.add(PARTICLES);
		ALL.add(ENABLE_VSYNC);
		ALL.add(FBO_ENABLE);
		ALL.add(RENDER_CLOUDS);
		ALL.add(USE_VBO);
		ALL.add(ENTITY_SHADOWS);

		ALL.add(LANGUAGE);
		ALL.add(RESOURCE_PACE);
	}

	private GameSettings() {}
}
