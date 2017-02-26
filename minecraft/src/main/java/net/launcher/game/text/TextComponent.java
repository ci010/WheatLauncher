package net.launcher.game.text;

import java.util.List;

/**
 * @author ci010
 */
public interface TextComponent extends Iterable<TextComponent>
{
	TextComponent style(Style style);

	TextComponent append(String text);

	TextComponent append(TextComponent component);

	Style getStyle();

	String getUnformattedComponentText();

	default String getUnformattedText()
	{
		StringBuilder stringbuilder = new StringBuilder();
		for (TextComponent component : this)
			stringbuilder.append(component.getUnformattedComponentText());
		return stringbuilder.toString();
	}

	String getFormattedText();

	List<TextComponent> getSiblings();

	TextComponent createCopy();
}
