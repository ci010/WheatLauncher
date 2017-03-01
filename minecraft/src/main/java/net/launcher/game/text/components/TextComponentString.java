package net.launcher.game.text.components;

import net.launcher.game.text.Style;
import net.launcher.game.text.TextComponent;
import net.launcher.game.text.TextFormatting;

import java.util.Locale;

/**
 * @author ci010
 */
public class TextComponentString extends TextComponentBase
{
	private final String text;

	public TextComponentString()
	{
		this.text = "";
	}

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

	public static final int[] colorCode = new int[32];

	static
	{
		for (int i = 0; i < 32; ++i)
		{
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170 + j;
			int l = (i >> 1 & 1) * 170 + j;
			int i1 = (i & 1) * 170 + j;

			if (i == 6)
				k += 85;

			if (i >= 16)
			{
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}
	}

	public static TextComponent convert(String raw)
	{
		TextComponentString string = new TextComponentString();
		StringBuilder builder = new StringBuilder();
		int colorIdx = 0;
		boolean boldStyle = false, strikethroughStyle = false, underlineStyle = false, italicStyle = false;
		for (int i = 0; i < raw.length(); i++)
		{
			char c = raw.charAt(i);
			if (c == 167 && i + 1 < raw.length())// 167 is §
			{
				if (builder.length() != 0)
				{
					string.append(new TextComponentString(builder.toString())
							.style(new Style().setBold(boldStyle)
									.setColor(TextFormatting.fromColorIndex(colorIdx))
									.setStrikethrough(strikethroughStyle)
									.setUnderlined(underlineStyle)
									.setItalic(italicStyle)));
					builder = new StringBuilder();
				}
				int idx = "0123456789abcdefklmnor".indexOf(String.valueOf(raw.charAt(i + 1)).toLowerCase(Locale.ROOT)
						.charAt(0));
				if (idx < 16)//is Color
				{
					if (idx < 0)
						idx = 15;
					colorIdx = idx;
				}
				else if (idx == 17) boldStyle = true;
				else if (idx == 18) strikethroughStyle = true;
				else if (idx == 19) underlineStyle = true;
				else if (idx == 20) italicStyle = true;
				else if (idx == 21)//reset
				{
					boldStyle = false;
					strikethroughStyle = false;
					underlineStyle = false;
					italicStyle = false;
				}

				++i;//ignore the next char
			}
			else builder.append(c);
		}
		if (builder.length() != 0)
			string.append(new TextComponentString(builder.toString())
					.style(new Style().setBold(boldStyle)
							.setColor(TextFormatting.fromColorIndex(colorIdx))
							.setStrikethrough(strikethroughStyle)
							.setUnderlined(underlineStyle)
							.setItalic(italicStyle)));
		return string;
	}
}
