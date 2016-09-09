package net.wheatlauncher.launch;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import net.wheatlauncher.utils.PrimitiveType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

/**
 * @author ci010
 */
public class GameSettings
{
	private File file;
	private EnumMap<Option, Property<?>> map = new EnumMap<>(Option.class);

	public GameSettings(File file)
	{
		this.file = file;
		this.load();
	}

	public Property<?> getOption(Option option)
	{
		Property<?> property = map.get(option);
		if (property == null)
			map.put(option, property = createProperty(option, null));
		return property;
	}

	public void load()
	{
		try (FileInputStream in = new FileInputStream(file))
		{
			Properties properties = new Properties();
			properties.load(in);
			properties.forEach((k, v) -> {
				Option option = null;
				try
				{
					option = Option.valueOf(k.toString());
				}
				catch (IllegalArgumentException e) {}
				if (option != null)
				{
					if (v instanceof String)
						v = option.type.parse(v.toString());
					else if (!option.type.isType(v)) throw new RuntimeException();
					map.put(option, createProperty(option, v));
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void save()
	{
		Properties properties = new Properties();
		try (FileInputStream in = new FileInputStream(file))
		{
			properties.load(in);
			for (Map.Entry<Object, Object> entry : properties.entrySet())
			{
				Option option = null;
				try
				{
					option = Option.valueOf(entry.getKey().toString());
				}
				catch (IllegalArgumentException e) {}
				if (option != null)
				{
					Object value = map.get(option).getValue();
					entry.setValue(value);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try (FileOutputStream out = new FileOutputStream(file))
		{
			properties.store(out, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private Property<?> createProperty(Option option, Object v)
	{
		if (v == null) v = option.defaultValue;
		if (!option.type.isType(v))
			throw new IllegalArgumentException("Type miss match! " + v + " with " + option.type);
		if (option.type == null)
		{
		}
		else if (option.type == PrimitiveType.FLOAT)
			return new SimpleFloatProperty((Float) v);
		else if (option.type == PrimitiveType.BOOL)
			return new SimpleBooleanProperty((Boolean) v);
		return null;
	}

	public enum Option
	{
		INVERT_MOUSE("invertYMouse", false),
		SENSITIVITY("mouseSensitivity", 0.5F),
		GAMMA("gamma", 1F),
		SATURATION("saturation", 0F),
		VIEW_BOBBING("bobView", true),

		FOV("fov", 70F, f -> f < 110F && f > 30F, 1),
		MAXFPS("maxFps", 120F, f -> f <= 260F && f >= 10F, 10F),
		MIPMAP_LEVELS("mipmapLevels", 4F, f -> f >= 0.0F && f <= 4.0F, 1.0F),
		RENDER_DISTANCE("renderDistance", 12, f -> f >= 2.0F && f <= 16.0F, 1),

		PARTICLES("particles", 0F, f -> f >= 0 && f <= 2, 1F),
		AMBIENT_OCCLUSION("ao", 0F, f -> f >= 0 && f <= 2, 1F),

		ANAGLYPH("anaglyph3d", false),
		ENABLE_VSYNC("vsync", false),
		FBO_ENABLE("fboEnable", true),
		RENDER_CLOUDS("renderClouds", true),
		USE_VBO("vbo", true),
		ENTITY_SHADOWS("entityShadows", true),


		USE_FULLSCREEN("fullscreen", false);


		private String id;
		private PrimitiveType type;
		private Object defaultValue;
		private Predicate<?> validator;
		private float step;

		Option(String id, boolean defaultValue)
		{
			this.id = id;
			this.type = PrimitiveType.BOOL;
			this.defaultValue = defaultValue;
		}


		Option(String id, float defaultValue)
		{
			this.id = id;
			this.type = PrimitiveType.FLOAT;
			this.defaultValue = defaultValue;
		}

		Option(String id, float defaultValue, Predicate<Float> validator, float step)
		{
			this.id = id;
			this.type = PrimitiveType.FLOAT;
			this.defaultValue = defaultValue;
			this.validator = validator;
			this.step = step;
		}

		Option(String id, String defaultValue, Predicate<String> validator)
		{
			this.id = id;
			this.defaultValue = defaultValue;
			this.validator = validator;
		}


//		GRAPHICS("graphics", false, false),
//
//		GUI_SCALE("guiScale", false, false),
//
//
//		CHAT_VISIBILITY("chat.visibility", false, false),
//
//		CHAT_COLOR("chat.color", false, true),
//
//		CHAT_LINKS("chat.links", false, true),
//
//		CHAT_OPACITY("chat.opacity", true, false),
//
//		CHAT_LINKS_PROMPT("chat.links.prompt", false, true),
//
//		SNOOPER_ENABLED("snooper", false, true),
//
//		USE_FULLSCREEN("fullscreen", false, true),
//
//		TOUCHSCREEN("touchscreen", false, true),
//
//		CHAT_SCALE("chat.scale", true, false),
//
//		CHAT_WIDTH("chat.width", true, false),
//
//		CHAT_HEIGHT_FOCUSED("chat.height.focused", true, false),
//
//		CHAT_HEIGHT_UNFOCUSED("chat.height.unfocused", true, false),
//
//		FORCE_UNICODE_FONT("forceUnicodeFont", false, true),
//
//		REDUCED_DEBUG_INFO("reducedDebugInfo", false, true),
//
//		MAIN_HAND("mainHand", false, false),
//
//		ATTACK_INDICATOR("attackIndicator", false, false),
//
//		ENABLE_WEAK_ATTACKS("enableWeakAttacks", false, true),
//
//		SHOW_SUBTITLES("showSubtitles", false, true),
//
//		REALMS_NOTIFICATIONS("realmsNotifications", false, true),
//
//		AUTO_JUMP("autoJump", false, true);
	}
}
