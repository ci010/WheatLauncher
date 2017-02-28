package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.launcher.game.ResourcePack;

/**
 * @author ci010
 */
public class MovableResourcePackCell extends ResourcePackCell
{
	private BooleanProperty left;

	public MovableResourcePackCell(ResourcePack resourcePack, Image image)
	{
		super(resourcePack, image);
	}

	public MovableResourcePackCell(ResourcePack resourcePack, Image image, boolean left)
	{
		super(resourcePack, image);
		setLeft(left);
	}

	public boolean getLeft()
	{
		return left.get();
	}

	public BooleanProperty leftProperty()
	{
		return left;
	}

	public void setLeft(boolean left)
	{
		this.left.set(left);
	}

	private HBox btnOverlay;

	protected void init()
	{
		left = new SimpleBooleanProperty(false);
		super.init();
		rebuild();
		leftProperty().addListener(observable -> rebuild());
	}

	public static final EventType<MoveResourcePackEvent> MOVE_RESOURCE_PACK_EVENT =
			new EventType<>(EventType.ROOT, "move_resource_pack");


	public static class MoveResourcePackEvent extends Event
	{
		public enum Type
		{
			UP, DOWN, SWITCH
		}

		private Type type;
		private ResourcePack resourcePack;

		public Type getType() {return type;}

		public ResourcePack getResourcePack() {return resourcePack;}

		public MoveResourcePackEvent(Type type, ResourcePack resourcePack)
		{
			super(MOVE_RESOURCE_PACK_EVENT);
			this.type = type;
			this.resourcePack = resourcePack;
		}
	}

	private void rebuild()
	{
		if (btnOverlay != null && imageContainer.getChildren().contains(btnOverlay))
			imageContainer.getChildren().remove(btnOverlay);
		btnOverlay = createBtnOverlay();
		btnOverlay.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0.4),
				CornerRadii.EMPTY, Insets.EMPTY)));
		btnOverlay.visibleProperty().bind(Bindings.createBooleanBinding(imageContainer::isHover, imageContainer.hoverProperty()));
		btnOverlay.setAlignment(Pos.CENTER);
		imageContainer.getChildren().add(btnOverlay);
	}

	private HBox createBtnOverlay()
	{
		HBox btnRoot = new HBox();

		JFXButton choose = new JFXButton();
		choose.setOnAction(event -> Event.fireEvent(this, new MoveResourcePackEvent(MoveResourcePackEvent.Type
				.SWITCH, this.getValue())));
		Icon icon;
		if (getLeft()) icon = new Icon("CARET_RIGHT");
		else icon = new Icon("CARET_LEFT");
		icon.setTextFill(Color.WHEAT);
		choose.setGraphic(icon);

		VBox moveBtnPanel = new VBox();
		JFXButton moveUp = new JFXButton(), moveDown = new JFXButton();
		moveUp.setOnAction(event -> Event.fireEvent(this, new MoveResourcePackEvent(MoveResourcePackEvent.Type.UP,
				this.getValue())));
		moveDown.setOnAction(event -> Event.fireEvent(this, new MoveResourcePackEvent(MoveResourcePackEvent.Type
				.DOWN, getValue())));
		icon = new Icon("CARET_UP");
		icon.setTextFill(Color.WHEAT);
		moveUp.setGraphic(icon);
		icon = new Icon("CARET_DOWN");
		icon.setTextFill(Color.WHEAT);
		moveDown.setGraphic(icon);

		moveBtnPanel.getChildren().add(moveUp);
		moveBtnPanel.getChildren().add(moveDown);

		if (getLeft())
		{
			btnRoot.getChildren().add(moveBtnPanel);
			btnRoot.getChildren().add(choose);
		}
		else
		{
			btnRoot.getChildren().add(choose);
			btnRoot.getChildren().add(moveBtnPanel);
		}
		return btnRoot;
	}
}
