package net.launcher.control;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import net.launcher.game.ResourcePack;

/**
 * @author ci010
 */
public class ResourcePackCell extends ImageCell<ResourcePack>
{
	public ResourcePackCell(ResourcePack resourcePack, Image image)
	{
		super(resourcePack, image);
//		this.setIconHeight(64);
//		this.setIconWidth(64);
//		this.imageContainer.setMaxSize(64, 64);
	}

	@Override
	protected Node buildContent()
	{
		Label name = new Label();
		name.setStyle("-fx-font-weight: BOLD;-fx-font-size:14px;");
		name.textProperty().bind(Bindings.createStringBinding(() -> (getValue() != null ? getValue().getPackName() : ""),
				valueProperty()));
		Label description = new Label();

		description.setWrapText(true);
		description.textProperty().bind(Bindings.createStringBinding(() -> (getValue() != null ? getValue().getDescription() : ""),
				valueProperty()));
		VBox content = new VBox(name, description);
		content.setSpacing(5);
		return content;
	}
}
