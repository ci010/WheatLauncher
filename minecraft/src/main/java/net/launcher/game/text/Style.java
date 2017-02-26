package net.launcher.game.text;

import net.launcher.game.text.event.Action;
import net.launcher.game.text.event.ClickEvent;
import net.launcher.game.text.event.DefaultAction;
import net.launcher.game.text.event.HoverEvent;
import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;


/**
 * @author ci010
 */
public class Style
{
	private Style parentStyle;
	private TextFormatting color;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private ClickEvent clickEvent;
	private HoverEvent hoverEvent;
	private String insertion;

	//@formatter:off
	private static final Style ROOT = new Style()
	{
		public TextFormatting getColor()
		{
			return null;
		}
		public boolean getBold()
		{
			return false;
		}
		public boolean getItalic()
		{
			return false;
		}
		public boolean getStrikethrough()
		{
			return false;
		}
		public boolean getUnderlined()
		{
			return false;
		}
		public boolean getObfuscated()
		{
			return false;
		}
		public ClickEvent getClickEvent()
		{
			return null;
		}
		public HoverEvent getHoverEvent()
		{
			return null;
		}
		public String getInsertion()
		{
			return null;
		}
		public Style setColor(TextFormatting color)
		{
			throw new UnsupportedOperationException();
		}
		public Style setBold(Boolean boldIn)
		{
			throw new UnsupportedOperationException();
		}
		public Style setItalic(Boolean italic)
		{
			throw new UnsupportedOperationException();
		}
		public Style setStrikethrough(Boolean strikethrough)
		{
			throw new UnsupportedOperationException();
		}
		public Style setUnderlined(Boolean underlined)
		{
			throw new UnsupportedOperationException();
		}
		public Style setObfuscated(Boolean obfuscated)
		{
			throw new UnsupportedOperationException();
		}
		public Style setClickEvent(ClickEvent event)
		{
			throw new UnsupportedOperationException();
		}
		public Style setHoverEvent(HoverEvent event)
		{
			throw new UnsupportedOperationException();
		}
		public Style setParentStyle(Style parent)
		{
			throw new UnsupportedOperationException();
		}
		public String toString() {return "Style.ROOT";}
		public Style createShallowCopy()
		{
			return this;
		}
		public Style createDeepCopy()
		{
			return this;
		}
		public String getFormattingCode()
		{
			return "";
		}
	};
	//@formatter:on

	public TextFormatting getColor()
	{
		return this.color == null ? this.getParent().getColor() : this.color;
	}

	/**
	 * Whether or not text of this ChatStyle should be in bold.
	 */
	public boolean getBold()
	{
		return this.bold == null ? this.getParent().getBold() : this.bold;
	}

	/**
	 * Whether or not text of this ChatStyle should be italicized.
	 */
	public boolean getItalic()
	{
		return this.italic == null ? this.getParent().getItalic() : this.italic;
	}

	/**
	 * Whether or not to format text of this ChatStyle using strikethrough.
	 */
	public boolean getStrikethrough()
	{
		return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough;
	}

	/**
	 * Whether or not text of this ChatStyle should be underlined.
	 */
	public boolean getUnderlined()
	{
		return this.underlined == null ? this.getParent().getUnderlined() : this.underlined;
	}

	/**
	 * Whether or not text of this ChatStyle should be obfuscated.
	 */
	public boolean getObfuscated()
	{
		return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated;
	}

	/**
	 * Whether or not this style is empty (inherits everything from the parent).
	 */
	public boolean isEmpty()
	{
		return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
	}

	/**
	 * The effective chat click event.
	 */
	public ClickEvent getClickEvent()
	{
		return this.clickEvent == null ? this.getParent().getClickEvent() : this.clickEvent;
	}

	/**
	 * The effective chat hover event.
	 */
	public HoverEvent getHoverEvent()
	{
		return this.hoverEvent == null ? this.getParent().getHoverEvent() : this.hoverEvent;
	}

	/**
	 * Get the text to be inserted into Chat when the component is shift-clicked
	 */
	public String getInsertion()
	{
		return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
	}

	/**
	 * Sets the color for this ChatStyle to the given value.  Only use color values for this; set other values using the
	 * specific methods.
	 */
	public Style setColor(TextFormatting color)
	{
		this.color = color;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be in bold.  Set to false if, e.g., the parent style is bold
	 * and you want text of this style to be unbolded.
	 */
	public Style setBold(Boolean boldIn)
	{
		this.bold = boldIn;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be italicized.  Set to false if, e.g., the parent style is
	 * italicized and you want to override that for this style.
	 */
	public Style setItalic(Boolean italic)
	{
		this.italic = italic;
		return this;
	}

	/**
	 * Sets whether or not to format text of this ChatStyle using strikethrough.  Set to false if, e.g., the parent
	 * style uses strikethrough and you want to override that for this style.
	 */
	public Style setStrikethrough(Boolean strikethrough)
	{
		this.strikethrough = strikethrough;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be underlined.  Set to false if, e.g., the parent style is
	 * underlined and you want to override that for this style.
	 */
	public Style setUnderlined(Boolean underlined)
	{
		this.underlined = underlined;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be obfuscated.  Set to false if, e.g., the parent style is
	 * obfuscated and you want to override that for this style.
	 */
	public Style setObfuscated(Boolean obfuscated)
	{
		this.obfuscated = obfuscated;
		return this;
	}

	/**
	 * Sets the event that should be run when text of this ChatStyle is clicked on.
	 */
	public Style setClickEvent(ClickEvent event)
	{
		this.clickEvent = event;
		return this;
	}

	/**
	 * Sets the event that should be run when text of this ChatStyle is hovered over.
	 */
	public Style setHoverEvent(HoverEvent event)
	{
		this.hoverEvent = event;
		return this;
	}

	/**
	 * Set a text to be inserted into Chat when the component is shift-clicked
	 */
	public Style setInsertion(String insertion)
	{
		this.insertion = insertion;
		return this;
	}

	/**
	 * Sets the fallback ChatStyle to use if this ChatStyle does not override some value.  Without a parent, obvious
	 * defaults are used (bold: false, underlined: false, etc).
	 */
	public Style setParentStyle(Style parent)
	{
		this.parentStyle = parent;
		return this;
	}

	/**
	 * Gets the equivalent text formatting code for this style, without the initial section sign (U+00A7) character.
	 */
	public String getFormattingCode()
	{
		if (this.isEmpty())
			return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
		else
		{
			StringBuilder stringbuilder = new StringBuilder();

			if (this.getColor() != null)
				stringbuilder.append(this.getColor());
			if (this.getBold())
				stringbuilder.append(TextFormatting.BOLD);
			if (this.getItalic())
				stringbuilder.append(TextFormatting.ITALIC);
			if (this.getUnderlined())
				stringbuilder.append(TextFormatting.UNDERLINE);
			if (this.getObfuscated())
				stringbuilder.append(TextFormatting.OBFUSCATED);
			if (this.getStrikethrough())
				stringbuilder.append(TextFormatting.STRIKETHROUGH);
			return stringbuilder.toString();
		}
	}

	/**
	 * Gets the immediate parent of this ChatStyle.
	 */
	private Style getParent()
	{
		return this.parentStyle == null ? ROOT : this.parentStyle;
	}

	public String toString()
	{
		return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + '}';
	}

	public boolean equals(Object p_equals_1_)
	{
		if (this == p_equals_1_)
		{
			return true;
		}
		else if (!(p_equals_1_ instanceof Style))
		{
			return false;
		}
		else
		{
			boolean flag;
			label0:
			{
				Style style = (Style) p_equals_1_;

				if (this.getBold() == style.getBold() && this.getColor() == style.getColor() && this.getItalic() == style.getItalic() && this.getObfuscated() == style.getObfuscated() && this.getStrikethrough() == style.getStrikethrough() && this.getUnderlined() == style.getUnderlined())
				{
					label85:
					{
						if (this.getClickEvent() != null)
						{
							if (!this.getClickEvent().equals(style.getClickEvent()))
							{
								break label85;
							}
						}
						else if (style.getClickEvent() != null)
						{
							break label85;
						}

						if (this.getHoverEvent() != null)
						{
							if (!this.getHoverEvent().equals(style.getHoverEvent()))
							{
								break label85;
							}
						}
						else if (style.getHoverEvent() != null)
						{
							break label85;
						}

						if (this.getInsertion() != null)
						{
							if (this.getInsertion().equals(style.getInsertion()))
							{
								break label0;
							}
						}
						else if (style.getInsertion() == null)
						{
							break label0;
						}
					}
				}

				flag = false;
				return flag;
			}
			flag = true;
			return flag;
		}
	}

	public int hashCode()
	{
		int i = this.color.hashCode();
		i = 31 * i + this.bold.hashCode();
		i = 31 * i + this.italic.hashCode();
		i = 31 * i + this.underlined.hashCode();
		i = 31 * i + this.strikethrough.hashCode();
		i = 31 * i + this.obfuscated.hashCode();
		i = 31 * i + this.clickEvent.hashCode();
		i = 31 * i + this.hoverEvent.hashCode();
		i = 31 * i + this.insertion.hashCode();
		return i;
	}

	/**
	 * Creates a shallow copy of this style.  Changes to this instance's values will not be reflected in the copy, but
	 * changes to the parent style's values WILL be reflected in both this instance and the copy, wherever either does
	 * not override a value.
	 */
	public Style createShallowCopy()
	{
		Style style = new Style();
		style.bold = this.bold;
		style.italic = this.italic;
		style.strikethrough = this.strikethrough;
		style.underlined = this.underlined;
		style.obfuscated = this.obfuscated;
		style.color = this.color;
		style.clickEvent = this.clickEvent;
		style.hoverEvent = this.hoverEvent;
		style.parentStyle = this.parentStyle;
		style.insertion = this.insertion;
		return style;
	}

	/**
	 * Creates a deep copy of this style.  No changes to this instance or its parent style will be reflected in the
	 * copy.
	 */
	public Style createDeepCopy()
	{
		Style style = new Style();
		style.setBold(this.getBold());
		style.setItalic(this.getItalic());
		style.setStrikethrough(this.getStrikethrough());
		style.setUnderlined(this.getUnderlined());
		style.setObfuscated(this.getObfuscated());
		style.setColor(this.getColor());
		style.setClickEvent(this.getClickEvent());
		style.setHoverEvent(this.getHoverEvent());
		style.setInsertion(this.getInsertion());
		return style;
	}

	public static Deserializer<Style, JSONObject> deserializer()
	{
		return (serialized, context) ->
		{
			Style style = new Style();
			style.setBold(serialized.optBoolean("bold"));
			style.setItalic(serialized.optBoolean("italic"));
			style.setUnderlined(serialized.optBoolean("underlined"));
			style.setStrikethrough(serialized.optBoolean("strikethrough"));
			style.setObfuscated(serialized.optBoolean("obfuscated"));
			style.setInsertion(serialized.optString("insertion"));
			String color = serialized.optString("color");
			if (!color.isEmpty()) style.setColor(TextFormatting.getValueByName(color));
			JSONObject click = serialized.optJSONObject("clickEvent");
			if (click != null)
			{
				String actionString = click.optString("action");
				Action action = actionString.isEmpty() ? null :
						DefaultAction.getValueByCanonicalName(actionString);

				String s = click.optString("value", null);

				if (action != null && s != null && action.shouldAllowInChat())
					style.clickEvent = new ClickEvent(s, action);
			}

			JSONObject hover = serialized.optJSONObject("hoverEvent");
			if (hover != null)
			{
				String actionString = hover.getString("action");
				Action action = actionString.isEmpty() ? null : DefaultAction
						.getValueByCanonicalName(actionString);

				TextComponent itextcomponent = null;
				new TextComponentDeserializer().deserialize(hover.getJSONObject("value"));
				if (action != null && itextcomponent != null && action.shouldAllowInChat())
					style.hoverEvent = new HoverEvent(itextcomponent, action);
			}
			return style;
		};
	}

}
