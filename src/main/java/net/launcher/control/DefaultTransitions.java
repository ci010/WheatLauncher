package net.launcher.control;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * backed by io.datafx.controller.flow.container.ContainerAnimations
 */
public enum DefaultTransitions implements Function<SceneTransitionHandler, List<KeyFrame>>
{
	FADE((c) ->
			Arrays.asList(new KeyFrame(Duration.ZERO, new KeyValue(c.getSnapshot().opacityProperty(), 1.0D, Interpolator.EASE_BOTH)),
					new KeyFrame(c.getDuration(), new KeyValue(c.getSnapshot().opacityProperty(), 0.0D, Interpolator.EASE_BOTH)))),
	ZOOM_IN((c) ->
			Arrays.asList(new KeyFrame(Duration.ZERO,
							new KeyValue(c.getSnapshot().scaleXProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().scaleYProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().opacityProperty(), 1.0D, Interpolator.EASE_BOTH)),
					new KeyFrame(c.getDuration(),
							new KeyValue(c.getSnapshot().scaleXProperty(), 4, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().scaleYProperty(), 4, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().opacityProperty(), 0, Interpolator.EASE_BOTH)))),
	ZOOM_OUT((c) ->
			(Arrays.asList(new KeyFrame(Duration.ZERO,
							new KeyValue(c.getSnapshot().scaleXProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().scaleYProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().opacityProperty(), 1.0D, Interpolator.EASE_BOTH)),
					new KeyFrame(c.getDuration(),
							new KeyValue(c.getSnapshot().scaleXProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().scaleYProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().opacityProperty(), 0, Interpolator.EASE_BOTH))))),
	SWIPE_LEFT((c) ->
			Arrays.asList(new KeyFrame(Duration.ZERO,
							new KeyValue(c.getCurrentRoot().translateXProperty(), c.getCurrentRoot().getWidth(), Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().translateXProperty(), -c.getCurrentRoot().getWidth(), Interpolator.EASE_BOTH)),
					new KeyFrame(c.getDuration(),
							new KeyValue(c.getCurrentRoot().translateXProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().translateXProperty(), -c.getCurrentRoot().getWidth(), Interpolator.EASE_BOTH)))),
	SWIPE_RIGHT((c) ->
			Arrays.asList(new KeyFrame(Duration.ZERO,
							new KeyValue(c.getCurrentRoot().translateXProperty(), -c.getCurrentRoot().getWidth(), Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().translateXProperty(), c.getCurrentRoot().getWidth(), Interpolator.EASE_BOTH)),
					new KeyFrame(c.getDuration(),
							new KeyValue(c.getCurrentRoot().translateXProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(c.getSnapshot().translateXProperty(), c.getCurrentRoot().getWidth(), Interpolator.EASE_BOTH))));

	private Function<SceneTransitionHandler, List<KeyFrame>> animationProducer;

	DefaultTransitions(Function<SceneTransitionHandler, List<KeyFrame>> animationProducer) {this.animationProducer = animationProducer;}

	@Override
	public List<KeyFrame> apply(SceneTransitionHandler sceneTransitionHandler) {return animationProducer.apply(sceneTransitionHandler);}
}
