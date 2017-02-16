package net.launcher.control;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Function;

/**
 * backed by io.datafx.controller.flow.container.AnimatedFlowContainer
 */
public class SceneTransitionHandler
{
	private Function<SceneTransitionHandler, List<KeyFrame>> transition;
	private ImageView snapshot;
	private Duration duration;
	private Timeline animation;

	private Pane root;

	public SceneTransitionHandler(Pane root, Function<SceneTransitionHandler, List<KeyFrame>> transition)
	{
		this(root, transition, Duration.millis(320.0D));
	}

	public SceneTransitionHandler(Pane root, Function<SceneTransitionHandler, List<KeyFrame>> transition, Duration duration)
	{
		this.root = root;
		this.transition = transition;
		this.duration = duration;
		this.snapshot = new ImageView();
		this.snapshot.setSmooth(true);
		this.snapshot.setPreserveRatio(true);
	}

	public ImageView getSnapshot() {return snapshot;}

	public Duration getDuration() {return duration;}

	public Pane getCurrentRoot() {return root;}

	public void transition(Pane newView)
	{
		if (this.animation != null) this.animation.stop();
		this.updateSnapshot(newView);

		this.animation = new Timeline();
		this.animation.getKeyFrames().addAll(this.transition.apply(this));
		this.animation.getKeyFrames().add(new KeyFrame(this.duration, (e) ->
		{
			this.snapshot.setImage(null);
			this.snapshot.setVisible(false);
		}));
		this.animation.play();
	}

	private void updateSnapshot(Pane newView)
	{
		if (root.getWidth() > 0D && root.getHeight() > 0D)
		{
			WritableImage placeholderImage = root.snapshot(new SnapshotParameters(),
					new WritableImage((int) root.getWidth(), (int) root.getHeight()));
			this.snapshot.setImage(placeholderImage);
			this.snapshot.setFitWidth(placeholderImage.getWidth());
			this.snapshot.setFitHeight(placeholderImage.getHeight());
		}
		else this.snapshot.setImage(null);

		this.snapshot.setVisible(true);
		this.snapshot.setOpacity(1.0D);
		root.getChildren().setAll(snapshot);
		root.getChildren().add(newView);
		this.snapshot.toFront();
	}
}
