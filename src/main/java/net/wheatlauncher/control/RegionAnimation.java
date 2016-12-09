package net.wheatlauncher.control;

import javafx.animation.*;
import javafx.beans.value.WritableValue;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author ci010
 */
public class RegionAnimation
{
	public static Animation create(Region node, BackgroundFill target, Duration duration)
	{
		InterpolatableFill interpolatableFill = new InterpolatableFill(node.getBackground().getFills().get(0), Interpolator.EASE_BOTH);
		InterpolatableFill targetFill = new InterpolatableFill(target, Interpolator.EASE_BOTH);

		WritableValue<InterpolatableFill> wrap = new WritableFill(node);

		return new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(wrap, interpolatableFill)),
				new KeyFrame(duration, new KeyValue(wrap, targetFill)));
	}

	public static Animation create(WritableValue<InterpolatableFill> wrap, BackgroundFill target, Duration duration)
	{
		InterpolatableFill interpolatableFill = wrap.getValue();
		InterpolatableFill targetFill = new InterpolatableFill(target, Interpolator.EASE_BOTH);

		return new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(wrap, interpolatableFill)),
				new KeyFrame(duration, new KeyValue(wrap, targetFill)));
	}

	public static class WritableFill implements WritableValue<InterpolatableFill>
	{
		protected Region node;

		public WritableFill(Region node)
		{
			this.node = node;
		}

		@Override
		public InterpolatableFill getValue()
		{
			BackgroundFill backgroundFill = node.getBackground().getFills().get(0);
			return new InterpolatableFill(backgroundFill, Interpolator.EASE_BOTH);
		}

		@Override
		public void setValue(InterpolatableFill value)
		{
			node.setBackground(new Background(value.fill));
		}
	}

	public static class InterpolatableFill implements Interpolatable<InterpolatableFill>
	{
		private BackgroundFill fill;
		private Interpolator interpolator;

		public InterpolatableFill(BackgroundFill fill, Interpolator interpolator)
		{
			this.fill = fill;
			this.interpolator = interpolator;
		}

		public BackgroundFill getFill()
		{
			return fill;
		}

		@Override
		public InterpolatableFill interpolate(InterpolatableFill endValue, double t)
		{
			Color c = (Color) fill.getFill();
			Color color = c.interpolate((Color) endValue.fill.getFill(), t);

			Insets insets = new Insets(
					interpolator.interpolate(fill.getInsets().getTop(), endValue.fill.getInsets().getTop(), t),
					interpolator.interpolate(fill.getInsets().getRight(), endValue.fill.getInsets().getRight(), t),
					interpolator.interpolate(fill.getInsets().getBottom(), endValue.fill.getInsets().getBottom(), t),
					interpolator.interpolate(fill.getInsets().getLeft(), endValue.fill.getInsets().getLeft(), t)
			);

			CornerRadii cornerRadii = new CornerRadii(
					interpolator.interpolate(fill.getRadii().getTopLeftHorizontalRadius(),
							endValue.fill.getRadii().getTopLeftHorizontalRadius(), t),
					interpolator.interpolate(fill.getRadii().getTopLeftVerticalRadius(),
							endValue.fill.getRadii().getTopLeftVerticalRadius(), t),

					interpolator.interpolate(fill.getRadii().getTopRightVerticalRadius(),
							endValue.fill.getRadii().getTopRightVerticalRadius(), t),
					interpolator.interpolate(fill.getRadii().getTopRightHorizontalRadius(),
							endValue.fill.getRadii().getTopRightHorizontalRadius(), t),


					interpolator.interpolate(fill.getRadii().getBottomRightHorizontalRadius(),
							endValue.fill.getRadii().getBottomRightHorizontalRadius(), t),
					interpolator.interpolate(fill.getRadii().getBottomRightVerticalRadius(),
							endValue.fill.getRadii().getBottomRightVerticalRadius(), t),

					interpolator.interpolate(fill.getRadii().getBottomLeftVerticalRadius(),
							endValue.fill.getRadii().getBottomLeftVerticalRadius(), t),
					interpolator.interpolate(fill.getRadii().getBottomLeftHorizontalRadius(),
							endValue.fill.getRadii().getBottomLeftHorizontalRadius(), t),

					fill.getRadii().isTopLeftHorizontalRadiusAsPercentage(),
					fill.getRadii().isTopLeftVerticalRadiusAsPercentage(),
					fill.getRadii().isTopRightVerticalRadiusAsPercentage(),
					fill.getRadii().isTopRightHorizontalRadiusAsPercentage(),
					fill.getRadii().isBottomRightHorizontalRadiusAsPercentage(),
					fill.getRadii().isBottomRightVerticalRadiusAsPercentage(),
					fill.getRadii().isBottomLeftVerticalRadiusAsPercentage(),
					fill.getRadii().isBottomLeftHorizontalRadiusAsPercentage()
			);

			return new InterpolatableFill(new BackgroundFill(color, cornerRadii, insets), interpolator);
		}
	}
}
