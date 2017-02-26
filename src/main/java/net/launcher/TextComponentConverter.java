package net.launcher;

import javafx.scene.paint.Color;
import javafx.scene.text.*;
import net.launcher.game.text.Style;
import net.launcher.game.text.TextComponent;

/**
 * @author ci010
 */
public class TextComponentConverter
{
	private static int[] colorCode = new int[32];

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

	public static TextFlow convert(TextComponent textComponent)
	{
		TextFlow textFlow = new TextFlow();
		convert(textComponent, textFlow);
		return textFlow;
	}

	public static void convert(TextComponent textComponent, TextFlow textFlow)
	{
		textFlow.getChildren().clear();
		for (TextComponent component : textComponent)
		{
			Text text = new Text(component.getUnformattedComponentText());
			Style style = component.getStyle();
			FontWeight bold = style.getBold() ? FontWeight.BOLD : FontWeight.NORMAL;
			FontPosture italic = style.getItalic() ? FontPosture.ITALIC : FontPosture.REGULAR;
			Font font = Font.font(Font.getDefault().getFamily(), bold, italic, Font.getDefault().getSize());
			text.setFont(font);
			if (style.getStrikethrough()) text.setStrikethrough(true);
			if (style.getUnderlined()) text.setUnderline(true);
			if (style.getColor() != null)
			{
				int colorIndex = style.getColor().getColorIndex();
				int code = colorCode[colorIndex];
				text.setFill(new Color((float) (code >> 16) / 255.0F, (float) (code >> 8 & 255) / 255.0F, (float) (code & 255) / 255.0F, 1));
			}
			textFlow.getChildren().add(text);
		}
	}
}
