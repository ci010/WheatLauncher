package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.skins.JFXButtonSkin;
import com.sun.javafx.css.SubCssMetaData;
import com.sun.javafx.css.converters.InsetsConverter;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.scene.layout.region.CornerRadiiConverter;
import javafx.animation.Animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class ColorTransitionButton extends JFXButton
{
	private boolean shouldApplyCss = true;

	{
		this.disableProperty().addListener(observable -> shouldApplyCss = true);
	}

	@Override
	protected Skin<?> createDefaultSkin()
	{
		return new SPSkin(this);
	}

	@Override
	protected void impl_processCSS(WritableValue<Boolean> unused)
	{
		if (shouldApplyCss)
			super.impl_processCSS(unused);
		shouldApplyCss = false;
	}

	public class SPSkin extends JFXButtonSkin
	{
		private Animation hoverAnimation;

		public SPSkin(JFXButton button)
		{
			super(button);
			button.setRipplerFill(button.getBackground().getFills().get(0).getFill());
			hoverAnimation = RegionAnimation.create(button,
					new BackgroundFill(getHoverColor(), getHoverRadii(), getHoverInsets()),
					Duration.millis(400));
			button.hoverProperty().addListener((observable, oldValue, newValue) -> {
				hoverAnimation.setRate(newValue ? 1 : -1);
				hoverAnimation.play();
			});

		}
	}

	private static class StyleableProperties
	{
		static final CssMetaData<Node, Paint> BACKGROUND_COLOR =
				new SubCssMetaData<>("-fx-hover-background-color",
						PaintConverter.getInstance(),
						Color.TOMATO);

		static final CssMetaData<Node, CornerRadii> BACKGROUND_RADIUS =
				new SubCssMetaData<>("-fx-hover-background-radius",
						CornerRadiiConverter.getInstance(),
						CornerRadii.EMPTY);

		static final CssMetaData<Node, Insets> BACKGROUND_INSETS =
				new SubCssMetaData<>("-fx-hover-background-insets",
						InsetsConverter.getInstance(),
						Insets.EMPTY);
	}

	public Paint getHoverColor()
	{
		return hoverColor.get();
	}

	public ObjectProperty<Paint> hoverColorProperty()
	{
		return hoverColor;
	}

	public void setHoverColor(Paint hoverColor)
	{
		this.hoverColor.set(hoverColor);
	}

	public Insets getHoverInsets()
	{
		return hoverInsets.get();
	}

	public ObjectProperty<Insets> hoverInsetsProperty()
	{
		return hoverInsets;
	}

	public void setHoverInsets(Insets hoverInsets)
	{
		this.hoverInsets.set(hoverInsets);
	}

	public CornerRadii getHoverRadii()
	{
		return hoverRadii.get();
	}

	public ObjectProperty<CornerRadii> hoverRadiiProperty()
	{
		return hoverRadii;
	}

	public void setHoverRadii(CornerRadii hoverRadii)
	{
		this.hoverRadii.set(hoverRadii);
	}

	private ObjectProperty<Paint> hoverColor = new SimpleStyleableObjectProperty<>(StyleableProperties.BACKGROUND_COLOR,
			this, "hoverColor", Color.TOMATO);

	private ObjectProperty<Insets> hoverInsets = new SimpleStyleableObjectProperty<>(
			StyleableProperties.BACKGROUND_INSETS,
			this, "hoverInsets", Insets.EMPTY);

	private ObjectProperty<CornerRadii> hoverRadii = new SimpleStyleableObjectProperty<>(
			StyleableProperties.BACKGROUND_RADIUS,
			this, "hoverRadii", CornerRadii.EMPTY);
	//
	private static List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData()
	{
		if (STYLEABLES == null)
		{
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<>(Control.getClassCssMetaData());
			styleables.addAll(super.getControlCssMetaData());
			styleables.add(StyleableProperties.BACKGROUND_COLOR);
			styleables.add(StyleableProperties.BACKGROUND_RADIUS);
			styleables.add(StyleableProperties.BACKGROUND_INSETS);
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}

}
