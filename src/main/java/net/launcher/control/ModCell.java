package net.launcher.control;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.launcher.game.mods.forge.ForgeMod;
import net.launcher.utils.StringUtils;

/**
 * @author ci010
 */
public class ModCell extends ImageCell<ForgeMod>
{
	public ModCell() {}

	@Override
	protected Node buildContent()
	{
		VBox box = new VBox();
		box.setSpacing(5);
		Label nameAndID = new Label(), versionLabel = new Label(), descript = new Label();
		nameAndID.setStyle("-fx-font-weight: BOLD;-fx-font-size:14px;");
		nameAndID.textProperty().bind(Bindings.createStringBinding(() ->
		{
			if (getValue() == null) return "Unknown";
			return getValue().getMetaData().getName() + " (" + getValue().getModId() + ")";
		}, valueProperty()));
		versionLabel.textProperty().bind(Bindings.createStringBinding(() ->
				{
					String mcVer = StringUtils.EMPTY;
					if (getValue() == null) return mcVer;
					if (!StringUtils.isEmpty(getValue().getMetaData().getMcVersion()))
						mcVer = " (" + getValue().getMetaData().getMcVersion() + ")";
					else if (!StringUtils.isEmpty(getValue().getMetaData().getAcceptMinecraftVersion()))
						mcVer = " " + getValue().getMetaData().getAcceptMinecraftVersion();
					return getValue().getMetaData().getVersion() + mcVer;
				},
				valueProperty()));
		descript.textProperty().bind(Bindings.createStringBinding(() ->
		{
			if (getValue() == null) return "";
			return getValue().getMetaData().getDescription();
		}, valueProperty()));
		descript.setWrapText(true);
		descript.setMaxWidth(350);
		box.getChildren().addAll(nameAndID, versionLabel, descript);
		return box;
	}
}
