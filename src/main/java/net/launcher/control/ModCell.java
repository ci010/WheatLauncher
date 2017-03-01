package net.launcher.control;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import net.launcher.game.forge.ForgeMod;

/**
 * @author ci010
 */
public class ModCell extends ImageCell<ForgeMod>
{
	public ModCell(ForgeMod value, Image image)
	{
		super(value, image);
		double scale = 64 / icon.getImage().getHeight();
		double width = icon.getImage().getWidth() * scale;
		icon.setFitHeight(64);
		icon.setFitWidth(width);
	}

	@Override
	protected Node buildContent()
	{
		VBox box = new VBox();
		box.setSpacing(5);
		Label nameAndID = new Label(), versionLabel = new Label(), descript = new Label();
		nameAndID.setStyle("-fx-font-weight: BOLD;-fx-font-size:14px;");
		nameAndID.textProperty().bind(Bindings.createStringBinding(() ->
				getValue().getMetaData().getName() + " (" + getValue().getModId() + ")", valueProperty()));
		versionLabel.textProperty().bind(Bindings.createStringBinding(() ->
				getValue().getMetaData().getVersion() + " (" + getValue().getMetaData().getAcceptMinecraftVersion
						() + ")", valueProperty()));
		descript.textProperty().bind(Bindings.createStringBinding(() ->
				getValue().getMetaData().getDescription(), valueProperty()));
		box.getChildren().addAll(nameAndID, versionLabel, descript);
		return box;
	}
}
