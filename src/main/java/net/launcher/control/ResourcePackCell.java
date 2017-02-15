package net.launcher.control;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.launcher.game.ResourcePack;

/**
 * @author ci010
 */
public class ResourcePackCell extends ImageCell<ResourcePack>
{
	@Override
	protected Node buildContent()
	{
		Label name = new Label();
		name.setStyle("-fx-font-weight: BOLD;-fx-font-size:14px;");
		name.textProperty().bind(Bindings.createStringBinding(() ->
		{
			ResourcePack resourcePack = getValue();
			if (resourcePack != null) return resourcePack.getPackName();
			return "Unknown";
		}, valueProperty()));
		Label description = new Label();
		description.textProperty().bind(Bindings.createStringBinding(() ->
		{
			ResourcePack resourcePack = getValue();
			if (resourcePack != null) return resourcePack.getDescription();
			return "Unknown";
		}, valueProperty()));
		VBox content = new VBox(name, description);
		content.setSpacing(10);
		content.setPadding(new Insets(10));
		return content;
	}
}
