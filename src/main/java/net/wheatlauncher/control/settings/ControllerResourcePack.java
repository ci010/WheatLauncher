package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.launcher.Logger;
import net.launcher.control.ResourcePackCell;
import net.launcher.game.ResourcePack;
import net.launcher.resourcepack.ResourcePackManager;
import net.wheatlauncher.MainApplication;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author ci010
 */
public class ControllerResourcePack
{
	public JFXListView<ResourcePack> resourcePacks;

	public JFXButton importBtn;
	public JFXButton exportBtn;
	public JFXTextField search;

	public void initialize()
	{
		Logger.trace("resource packs setting init");
		resourcePacks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		FilteredList<ResourcePack> resourcePacks = new FilteredList<>(MainApplication.getCore().getResourcePackManager().getAllElement());
		resourcePacks.predicateProperty().bind(Bindings.createObjectBinding(() ->
				(Predicate<ResourcePack>) resourcePack -> resourcePack.getPackName().contains(search.getText()) ||
						resourcePack.getDescription().contains(search.getText()), search.textProperty()));
		this.resourcePacks.setItems(resourcePacks);
		this.resourcePacks.setCellFactory(param -> new ListCell<ResourcePack>()
		{
			@Override
			protected void updateItem(ResourcePack item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty) setGraphic(null);
				else
					this.setGraphic(new ResourcePackCell(item, MainApplication.getCore().getResourcePackManager().getIcon(item)));
			}
		});
		importBtn.setOnAction(event ->
		{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select resource pack");
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Resource Packs",
					"*.zip"));
			List<File> files = fileChooser.showOpenMultipleDialog(importBtn.getScene().getWindow());
			if (files != null && !files.isEmpty())
				for (File file : files)
					MainApplication.getCore().getTaskCenter().runTask(MainApplication.getCore().getResourcePackManager()
							.importResourcePack(file.toPath()));
		});
		exportBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> this.resourcePacks.getSelectionModel()
				.isEmpty(), this.resourcePacks.getSelectionModel().selectedItemProperty()));
		exportBtn.setOnAction(event ->
		{
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("Select save location");
			File file = fileChooser.showDialog(exportBtn.getScene().getWindow());
			ResourcePackManager manager = MainApplication.getCore().getResourcePackManager();
			MainApplication.getCore().getTaskCenter().runTask(manager.exportResourcePack(file.toPath(), this
					.resourcePacks.getSelectionModel().getSelectedItems()));
		});
	}
}
