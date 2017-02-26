package net.launcher.game.text.components;

import net.launcher.game.text.Style;
import net.launcher.game.text.TextComponent;
import net.launcher.game.text.TextFormatting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ci010
 */
public abstract class TextComponentBase implements TextComponent
{
	protected List<TextComponent> siblings = new ArrayList<>();
	private Style style;

	/**
	 * Adds a new component to the end of the sibling list, setting that component's style's parent style to this
	 * component's style.
	 *
	 * @return This component, for chaining (and not the newly added component)
	 */
	public TextComponent append(TextComponent component)
	{
		component.getStyle().setParentStyle(this.getStyle());
		this.siblings.add(component);
		return this;
	}

	public List<TextComponent> getSiblings()
	{
		return this.siblings;
	}

	public TextComponent append(String text) {return this.append(new TextComponentString(text));}

	public TextComponent style(Style style)
	{
		this.style = style;

		for (TextComponent component : this.siblings)
			component.getStyle().setParentStyle(this.getStyle());

		return this;
	}

	/**
	 * Gets the style of this component. Returns a direct reference; changes to this style will modify the style of this
	 * component (IE, there is no need to call {@link #style(Style)} again after modifying it).
	 * <p>
	 * If this component's style is currently <code>null</code>, it will be initialized to the default style, and the
	 * parent style of all sibling components will be set to that style. (IE, changes to this style will also be
	 * reflected in sibling components.)
	 * <p>
	 * This method never returns <code>null</code>.
	 */
	public Style getStyle()
	{
		if (this.style == null)
		{
			this.style = new Style();
			for (TextComponent component : this.siblings)
				component.getStyle().setParentStyle(this.style);
		}
		return this.style;
	}

	public Iterator<TextComponent> iterator()
	{
		LinkedList<TextComponent> lst = new LinkedList<>(this.siblings);
		lst.add(0, this);
		return lst.iterator();
	}

	/**
	 * Gets the text of this component <em>and all sibling components</em>, with formatting codes added for rendering.
	 */
	public final String getFormattedText()
	{
		StringBuilder stringbuilder = new StringBuilder();

		for (TextComponent component : this)
		{
			String s = component.getUnformattedComponentText();

			if (!s.isEmpty())
			{
				stringbuilder.append(component.getStyle().getFormattingCode());
				stringbuilder.append(s);
				stringbuilder.append(TextFormatting.RESET);
			}
		}

		return stringbuilder.toString();
	}

//	public static Iterator<TextComponent> createDeepCopyIterator(Iterable<TextComponent> components)
//	{
//		Iterator<TextComponent> iterator = Iterators.concat(Iterators.transform(components.iterator(), new Function<TextComponent, Iterator<TextComponent>>()
//		{
//			public Iterator<TextComponent> apply(@Nullable TextComponent p_apply_1_)
//			{
//				return p_apply_1_.iterator();
//			}
//		}));
//		iterator = Iterators.transform(iterator, new Function<TextComponent, TextComponent>()
//		{
//			public TextComponent apply(@Nullable TextComponent p_apply_1_)
//			{
//				TextComponent itextcomponent = p_apply_1_.createCopy();
//				itextcomponent.style(itextcomponent.getStyle().createDeepCopy());
//				return itextcomponent;
//			}
//		});
//		return iterator;
//	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (!(obj instanceof TextComponentBase))
			return false;
		else
		{
			TextComponentBase textcomponentbase = (TextComponentBase) obj;
			return this.siblings.equals(textcomponentbase.siblings) && this.getStyle().equals(textcomponentbase.getStyle());
		}
	}

	public int hashCode()
	{
		return 31 * this.style.hashCode() + this.siblings.hashCode();
	}

	public String toString()
	{
		return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
	}
}
