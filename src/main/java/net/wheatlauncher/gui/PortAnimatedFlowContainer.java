package net.wheatlauncher.gui;

import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.FlowContainer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import net.wheatlauncher.utils.Function;

import java.util.List;

/**
 * @author ci010
 */
public class PortAnimatedFlowContainer implements FlowContainer<StackPane>
{
	private StackPane root;
	private Duration duration;
	private Function<PortAnimatedFlowContainer, List<KeyFrame>> animationProducer;
	private Timeline animation;
	private ImageView placeholder;

	public PortAnimatedFlowContainer(Duration duration, Function<PortAnimatedFlowContainer, List<KeyFrame>> animationProducer)
	{
		this.root = new StackPane();
		root.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		this.duration = duration;
		this.animationProducer = animationProducer;
		this.placeholder = new ImageView();
		this.placeholder.setPreserveRatio(true);
		this.placeholder.setSmooth(true);
	}

	public <U> void setViewContext(ViewContext<U> context)
	{
		this.updatePlaceholder(context.getRootNode());
		if (this.animation != null)
			this.animation.stop();

		this.animation = new Timeline();
		this.animation.getKeyFrames().addAll(this.animationProducer.apply(this));
		this.animation.getKeyFrames().add(new KeyFrame(this.duration, new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent e)
			{
				PortAnimatedFlowContainer.this.clearPlaceholder();
			}
		}));
		this.animation.play();
	}

	public ImageView getPlaceholder()
	{
		return this.placeholder;
	}

	public Duration getDuration()
	{
		return this.duration;
	}

	public StackPane getView()
	{
		return this.root;
	}

	private void clearPlaceholder()
	{
		this.placeholder.setImage(null);
		this.placeholder.setVisible(false);
	}

	private void updatePlaceholder(Node newView)
	{
		if (this.root.getWidth() > 0.0D && this.root.getHeight() > 0.0D)
		{
			WritableImage placeholderImage = this.root.snapshot(null, new WritableImage((int) this.root.getWidth(), (int) this.root.getHeight()));
			this.placeholder.setImage(placeholderImage);
			this.placeholder.setFitWidth(placeholderImage.getWidth());
			this.placeholder.setFitHeight(placeholderImage.getHeight());
		}
		else
		{
			this.placeholder.setImage(null);
		}

		this.placeholder.setVisible(true);
		this.placeholder.setOpacity(1.0D);
		this.root.getChildren().setAll(this.placeholder);
		this.root.getChildren().add(newView);
		this.placeholder.toFront();
	}
}
