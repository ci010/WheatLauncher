package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.skins.JFXButtonSkin;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class ColorTransitionButton extends JFXButton
{
	@Override
	protected Skin<?> createDefaultSkin()
	{
		return new SPSkin(this);
	}

	private StyleableObjectProperty<Paint> targetColor = new SimpleStyleableObjectProperty<Paint>(null);
	private static CssMetaData<ColorTransitionButton, Paint> COLOR = new CssMetaData<ColorTransitionButton, Paint>
			("-fx-button-target-color", new StyleConverter<Object, Paint>()
			{
				@Override
				public Paint convert(ParsedValue<Object, Paint> value, Font font)
				{
					if (value.getValue() instanceof Paint)
						return (Paint) value.getValue();
					if (value.getValue() instanceof String)
						return Color.valueOf((String) value.getValue());
					return null;
				}
			}, Color.WHEAT)
	{
		@Override
		public boolean isSettable(ColorTransitionButton styleable)
		{
			return styleable.targetColor == null || !styleable.targetColor.isBound();
		}

		@Override
		public StyleableProperty<Paint> getStyleableProperty(ColorTransitionButton styleable) {return styleable.targetColorProperty();}
	};

	public final StyleableObjectProperty<Paint> targetColorProperty() {return targetColor;}

	public Paint getTargetColor() {return targetColor.get();}

	public final void setTargetColor(Paint paint) {targetColor.set(paint);}

	private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

	static
	{
		final List<CssMetaData<? extends Styleable, ?>> styleables =
				new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
		styleables.add(COLOR);
		CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
	}

	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData()
	{
		if (STYLEABLES == null)
		{
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			styleables.addAll(getClassCssMetaData());
			styleables.addAll(super.getControlCssMetaData());
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData()
	{
		return CHILD_STYLEABLES;
	}

	public static class SPSkin extends JFXButtonSkin
	{
		private Animation clickedAnimation;

		public SPSkin(ColorTransitionButton button)
		{
			super(button);
			button.setPickOnBounds(false);
			button.targetColorProperty().addListener(new ChangeListener<Paint>()
			{
				@Override
				public void changed(ObservableValue<? extends Paint> observable, Paint oldValue, Paint newValue)
				{
					ColorTransitionButton skinnable = (ColorTransitionButton) getSkinnable();
					clickedAnimation = create(skinnable, (Color) getSkinnable().getBackground().getFills().get(0).getFill(),
							(Color) skinnable.targetColor.get(), Duration.millis(500));
				}
			});
			if (button.getTargetColor() == null)
			{
//				System.out.println("null");
			}
			else
				clickedAnimation = create(button, (Color) button.getBackground().getFills().get(0).getFill(), (Color) button.getTargetColor(),
						Duration.millis(400));
//			button.ripplerFillProperty().bind(button.targetColorProperty());

			button.setOnMouseEntered(e -> {
				if (clickedAnimation != null)
				{
					clickedAnimation.setRate(1);
					clickedAnimation.play();
				}
			});
			button.setOnMouseExited(event -> {
				if (clickedAnimation != null)
				{
					clickedAnimation.setRate(-1);
					clickedAnimation.play();
				}
			});

		}

	}

	public static Animation create(final JFXButton node, Color color, Color colorTarget, Duration duration)
	{
		WritableValue<Color> wrap = new WritableValue<Color>()
		{
			@Override
			public Color getValue()
			{
				BackgroundFill backgroundFill = node.getBackground().getFills().get(0);

				if (backgroundFill == null)
					return Color.WHITE;
				return (Color) backgroundFill.getFill();
			}

			@Override
			public void setValue(Color value)
			{
				Background background = node.getBackground();
				List<BackgroundFill> lst = new ArrayList<BackgroundFill>();
				lst.add(new BackgroundFill(value, new CornerRadii(1), new Insets(1)));
				node.ripplerFillProperty().setValue(value);
				node.setBackground(new Background(lst, background.getImages()));
			}
		};
		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(wrap, color)),
				new KeyFrame(duration, new KeyValue(wrap, colorTarget)));
		return timeline;
	}
}
