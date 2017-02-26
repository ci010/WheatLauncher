package net.launcher.game.text.components;

import net.launcher.game.text.TextComponent;

/**
 * @author ci010
 */
public class TextComponentString extends TextComponentBase
{
	private final String text;

	public TextComponentString(String msg)
	{
		this.text = msg;
	}

	public String getText() {return this.text;}

	public String getUnformattedComponentText() {return this.text;}

	public TextComponentString createCopy()
	{
		TextComponentString str = new TextComponentString(this.text);
		str.style(this.getStyle().createShallowCopy());
		for (TextComponent sibling : this.getSiblings())
			str.append(sibling.createCopy());
		return str;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (!(obj instanceof TextComponentString))
			return false;
		else
		{
			TextComponentString textcomponentstring = (TextComponentString) obj;
			return this.text.equals(textcomponentstring.getText()) && super.equals(obj);
		}
	}

	public String toString()
	{
		return "TextComponent{text=\'" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}
}
