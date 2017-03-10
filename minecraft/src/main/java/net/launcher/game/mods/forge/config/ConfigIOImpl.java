package net.launcher.game.mods.forge.config;

import net.launcher.utils.NIOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ci010
 */
public class ConfigIOImpl implements ConfigIO
{
	private static final String CATEGORY_SPLITTER = ".";

	private static String getQualifiedName(String name, ForgeConfigImpl.CateImpl parent)
	{
		return (parent == null ? name : getQualifiedName(parent.getName(), parent.parent) +
				CATEGORY_SPLITTER + name);
	}

	private static ForgeConfig.Property.Type tryParse(char id)
	{
		for (int x = 0; x < ForgeConfig.Property.Type.values().length; x++)
			if (ForgeConfig.Property.Type.values()[x].name().charAt(0) == id)
				return ForgeConfig.Property.Type.values()[x];
		return ForgeConfig.Property.Type.STRING;
	}

	@Override
	public ForgeConfig read(Path path) throws IOException
	{
		String s = NIOUtils.mapToString(path);
		String[] split = s.split(System.getProperty("line.separator"));
		ParseStack parseStack = new ParseStack(path);
		for (String line : split)
		{
			String trim = line.trim();
			for (LineReader reader : this.readers)
				if (reader.read(trim, parseStack)) break;
		}
		return parseStack.build();
	}

	@Override
	public void write(ForgeConfig config) throws IOException
	{

	}

	class ParseStack
	{
		private final Path path;
		private List<String> comments = new ArrayList<>();

		Map<String, ForgeConfig.Category> categoryMap;
		private ForgeConfigImpl.CateImpl curCate;

		boolean inQuote, inList;

		String currentName;
		ForgeConfig.Property.Type currentType;
		private List<String> currentValues = new ArrayList<>();

		ParseStack(Path path)
		{
			this.path = path;
		}

		void pushComment(String comment) {comments.add(comment);}

		void startProperty(String name, ForgeConfig.Property.Type type)
		{
			if (currentName != null) throw new RuntimeException();
			currentName = name;
			currentType = type;
		}

		void pushValue(String value)
		{
			currentValues.add(value);
		}

		void endProperty()
		{
			if (currentValues == null || currentType == null) throw new RuntimeException();
			if (currentValues.isEmpty()) throw new RuntimeException();

			if (currentValues.size() == 1)
				curCate.propertyMap.put(currentName, new ForgeConfigImpl.SingleProp(currentName, grabComment(),
						currentType).setValue(currentValues.get(0)));
			else
				curCate.propertyMap.put(currentName, new ForgeConfigImpl.ListProp(currentName, grabComment(),
						currentType).setValues(currentValues.toArray(new String[currentValues.size()])));
			currentName = null;
			currentType = null;
			comments.clear();
			currentValues.clear();
		}

		String grabComment()
		{
			return this.comments.stream().reduce((s, s2) -> s.concat("\n").concat(s2)).orElse("");
		}

		void startCategory(String category)
		{
			String qualifiedName = getQualifiedName(category, curCate);

			ForgeConfigImpl.CateImpl cat = (ForgeConfigImpl.CateImpl) categoryMap.get(qualifiedName);
			if (cat == null)
				categoryMap.put(qualifiedName, curCate = new ForgeConfigImpl.CateImpl(category, grabComment(), curCate));
			else
				curCate = cat;
		}

		void endCategory()
		{
			if (curCate == null)
				throw new RuntimeException("Config file corrupt, attempted to close to many categories.");
			curCate = curCate.parent;
		}

		ForgeConfig build() {return new ForgeConfigImpl(path, categoryMap);}
	}

	interface LineReader
	{
		boolean read(String line, ParseStack stack);
	}

	private List<LineReader> readers;

	{
		readers = new ArrayList<>();
		readers.add((line, stack) ->
		{
			if (stack.inList /*|| stack.inQuote */ || line.charAt(0) != '#')
				return false;
			stack.pushComment(line.substring(1));
			return true;
		});
		readers.add((line, stack) ->
		{
			if (stack.inList /*|| stack.inQuote*/ || !line.endsWith("{"))
				return false;
			stack.startCategory(line.substring(0, line.lastIndexOf("{")).trim());
			return true;
		});
		readers.add((line, stack) ->
		{
			if (stack.inList /*|| stack.inQuote */ || !line.endsWith("}"))
				return false;
			stack.endCategory();
			return true;
		});
		readers.add((line, stack) ->
		{
			if (!stack.inList) return false;
			if (line.endsWith(">"))
				stack.endProperty();
			else
				stack.pushValue(line);
			return true;
		});
		readers.add((line, stack) ->
		{
			String[] split = line.split(":");
			if (split.length < 2) return false;
			String typeString = split[0];
			String remain = split[1];
			if (typeString.length() != 1) return false;

			ForgeConfig.Property.Type type = tryParse(typeString.charAt(0));

			if (remain.endsWith("<"))
			{
				stack.inList = true;
				return true;
			}
			else if (remain.charAt(0) == '"')//in quote
			{
				stack.inQuote = true;
				int quoteEnd = remain.lastIndexOf('"');
				int firstEqual = remain.indexOf('=');
				if (firstEqual > quoteEnd)
				{
					stack.startProperty(remain.substring(0, quoteEnd).substring(1), type);
					stack.pushValue(remain.substring(firstEqual));
					stack.endProperty();
				}
				else
				{
					throw new RuntimeException();
				}
			}
			throw new RuntimeException();
		});
	}
}
