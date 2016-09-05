package net.wheatlauncher.gui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.util.Duration;
import moe.mickey.minecraft.skin.fx.SkinCanvas;

/**
 * @author ci010
 */
public class AnimationRotate extends Transition
{
	private SkinCanvas canvas;
//	private double[] cache = new double[8001];

	public AnimationRotate(SkinCanvas canvas)
	{
		this.canvas = canvas;
		this.setInterpolator(Interpolator.LINEAR);
		this.setCycleDuration(Duration.millis(8000));
		this.setCycleCount(Animation.INDEFINITE);
//		for (int i = 0; i < 8001; i++)
//		{
//			double frac = i / 8001D;
//			double angle = 180 + 360 * frac;
//			cache[i] = angle;
//		}
	}

	@Override
	protected void interpolate(double frac)
	{
		canvas.getYRotate().angleProperty().setValue(180 + 360 * frac);
//		canvas.getYRotate().angleProperty().set(cache[(int) (frac * 8000)]);
	}
}
