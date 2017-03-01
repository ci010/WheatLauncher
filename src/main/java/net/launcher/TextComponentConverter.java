package net.launcher;

import javafx.scene.paint.Color;
import javafx.scene.text.*;
import net.launcher.game.text.Style;
import net.launcher.game.text.TextComponent;
import net.launcher.game.text.components.TextComponentString;

/**
 * @author ci010
 */
public class TextComponentConverter
{
	private static int[] colorCode = TextComponentString.colorCode;

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
