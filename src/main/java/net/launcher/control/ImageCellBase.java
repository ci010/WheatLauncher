package net.launcher.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * @author ci010
 */
public class ImageCellBase<T> extends ImageCell<T>
{
	private StackPane rightContainer;
	private ObjectProperty<Node> right = new SimpleObjectProperty<>();

	public ImageCellBase() {bind();}

	public ImageCellBase(T value, Image image) {super(value, image); bind();}

	private void bind()
	{
		right.addListener(observable ->
		{
			rightContainer.getChildren().clear();
			if (right.get() != null)
				rightContainer.getChildren().add(right.get());
		});
	}

	public Node getRight()
	{
		return right.get();
	}

	public ObjectProperty<Node> rightProperty()
	{
		return right;
	}

	public void setRight(Node right)
	{
		this.right.set(right);
	}

	@Override
	protected Node buildContent()
	{
		rightContainer = new StackPane();
		HBox.setHgrow(rightContainer, Priority.ALWAYS);
		rightContainer.setMaxWidth(Integer.MAX_VALUE);
		rightContainer.setAlignment(Pos.CENTER);
		return rightContainer;
	}
}
