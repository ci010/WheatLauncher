package net.wheatlauncher.control.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.skins.JFXButtonSkin;
import com.sun.javafx.css.SubCssMetaData;
import com.sun.javafx.css.converters.DurationConverter;
import com.sun.javafx.css.converters.InsetsConverter;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.scene.layout.region.CornerRadiiConverter;
import javafx.animation.Animation;
import javafx.beans.value.WritableValue;
import javafx.css.*;
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

	public ColorTransitionButton()
	{
		this.disableProperty().addListener(observable -> super.impl_processCSS(null));
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
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
					getTransitionDuration());
			button.hoverProperty().addListener((observable, oldValue, newValue) ->
			{
				hoverAnimation.setRate(newValue ? 1 : -1);
				hoverAnimation.play();
			});

		}
	}

	private static class StyleableProperties
	{
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;


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

		static final CssMetaData<ColorTransitionButton, Duration> TRANSITION_DURATION = new
				CssMetaData<ColorTransitionButton, Duration>("-fx-hover-transition-duration",
						DurationConverter.getInstance(),
						Duration.millis(400))
				{
					@Override
					public void set(ColorTransitionButton styleable, Duration value, StyleOrigin origin)
					{
						super.set(styleable, value, origin);
					}

					@Override
					public boolean isSettable(ColorTransitionButton styleable)
					{
						return true;
					}

					@Override
					public StyleableProperty<Duration> getStyleableProperty(ColorTransitionButton styleable)
					{
						return styleable.transitionDuration;
					}
				};

		static
		{
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					BACKGROUND_COLOR, BACKGROUND_INSETS, BACKGROUND_RADIUS, TRANSITION_DURATION
			);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	public Paint getHoverColor()
	{
		return hoverColor.get();
	}

	public StyleableObjectProperty<Paint> hoverColorProperty()
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

	public StyleableObjectProperty<Insets> hoverInsetsProperty()
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

	public StyleableObjectProperty<CornerRadii> hoverRadiiProperty()
	{
		return hoverRadii;
	}

	public void setHoverRadii(CornerRadii hoverRadii)
	{
		this.hoverRadii.set(hoverRadii);
	}

	public Duration getTransitionDuration()
	{
		return transitionDuration.get();
	}

	public StyleableObjectProperty<Duration> transitionDurationProperty()
	{
		return transitionDuration;
	}

	public void setTransitionDuration(Duration transitionDuration)
	{
		this.transitionDuration.set(transitionDuration);
	}

	private StyleableObjectProperty<Paint> hoverColor = new SimpleStyleableObjectProperty<>(StyleableProperties.BACKGROUND_COLOR,
			this, "hoverColor", Color.TOMATO);

	private StyleableObjectProperty<Insets> hoverInsets = new SimpleStyleableObjectProperty<>(
			StyleableProperties.BACKGROUND_INSETS,
			this, "hoverInsets", Insets.EMPTY);

	private StyleableObjectProperty<CornerRadii> hoverRadii = new SimpleStyleableObjectProperty<>(
			StyleableProperties.BACKGROUND_RADIUS,
			this, "hoverRadii", CornerRadii.EMPTY);

	private StyleableObjectProperty<Duration> transitionDuration = new SimpleStyleableObjectProperty<>(
			StyleableProperties.TRANSITION_DURATION,
			this, "transitionDuration", Duration.millis(400));

	private static List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData()
	{
		if (STYLEABLES == null)
		{
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<>();
			styleables.addAll(super.getControlCssMetaData());
			styleables.addAll(getClassCssMetaData());
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData()
	{
		return StyleableProperties.CHILD_STYLEABLES;
	}

	private static final String DEFAULT_STYLE_CLASS = "colored-button";
}

