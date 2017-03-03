package net.launcher.control.modview;

import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.launcher.control.ImageCell;
import net.launcher.services.curseforge.CurseForgeProject;

/**
 * @author ci010
 */
public class CurseForgeImageCell extends ImageCell<CurseForgeProject>
{
	public CurseForgeImageCell()
	{
		this.setMaxWidth(400);
	}

	@Override
	protected Node buildContent()
	{
		VBox box = new VBox();
		Label name = new Label();
		box.setPadding(new Insets(0, 0, 0, 5));

		name.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (getValue() != null)
						return getValue().getName();
					else return "Unknown";
				}, valueProperty()
		));
		name.setStyle("-fx-font-size:14;-fx-font-weight:bold;");
		Label author = new Label();
		author.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (getValue() != null)
						return "by " + getValue().getAuthor();
					else return "by Unknown";
				}, valueProperty()
		));
		HBox first = new HBox(name, author);
		first.setSpacing(5);

		Icon down = new Icon("DOWNLOAD", "1em");
		Label downloadCount = new Label();
		downloadCount.textProperty().bind(Bindings.createStringBinding(() ->
		{
			CurseForgeProject value = getValue();
			if (value != null)
				return getValue().getDownloadCount();
			else return "-";
		}, valueProperty()));
		downloadCount.setGraphic(down);
		downloadCount.setContentDisplay(ContentDisplay.LEFT);

		Icon clock = new Icon("CLOCK_ALT", "1em");
		Label date = new Label();
		date.textProperty().bind(Bindings.createStringBinding(() ->
		{
			CurseForgeProject value = getValue();
			if (value != null)
				return getValue().getLastTime().toString();
			else return "-";
		}, valueProperty()));
		date.setGraphic(clock);
		date.setContentDisplay(ContentDisplay.LEFT);

		HBox second = new HBox(downloadCount, date);
		second.setSpacing(5);

		Label description = new Label();

		description.setWrapText(true);
		description.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (getValue() != null)
						return getValue().getDescription();
					else return "Unknown";
				}, valueProperty()
		));
		box.setSpacing(5);
		box.getChildren().addAll(first, second, description);
		return box;
	}
}
