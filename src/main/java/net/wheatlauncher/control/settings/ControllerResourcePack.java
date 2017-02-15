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
import net.launcher.Bootstrap;
import net.launcher.Logger;
import net.launcher.control.ResourcePackCell;
import net.launcher.game.ResourcePack;
import net.launcher.resourcepack.ResourcePackManager;
import net.wheatlauncher.control.utils.WindowsManager;

import java.io.File;
import java.io.IOException;
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
		FilteredList<ResourcePack> resourcePacks = new FilteredList<>(Bootstrap.getCore().getResourcePackManager().getAllElement());
		resourcePacks.predicateProperty().bind(Bindings.createObjectBinding(() ->
				(Predicate<ResourcePack>) resourcePack -> resourcePack.getPackName().contains(search.getText()) ||
						resourcePack.getDescription().contains(search.getText()), search.textProperty()));
		this.resourcePacks.setItems(resourcePacks);
		this.resourcePacks.setCellFactory(param -> new ListCell<ResourcePack>()
		{
			@Override
			protected void updateItem(ResourcePack item, boolean empty)
			{
				if (item == null || empty) {super.updateItem(item, empty); return;}
				ResourcePackCell ce = new ResourcePackCell();
				ce.setValue(item);
				try {ce.setImage(Bootstrap.getCore().getResourcePackManager().getIcon(item));}
				catch (IOException e)
				{
					WindowsManager.displayError(ControllerResourcePack.this.resourcePacks.getScene(), e);
				}
				this.setGraphic(ce);
				super.updateItem(item, empty);
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
					Bootstrap.getCore().getResourcePackManager().importResourcePack(file.toPath(), null);
		});
		exportBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> this.resourcePacks.getSelectionModel()
				.isEmpty(), this.resourcePacks.getSelectionModel().selectedItemProperty()));
		exportBtn.setOnAction(event ->
		{
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("Select save location");
			File file = fileChooser.showDialog(exportBtn.getScene().getWindow());
			ResourcePackManager manager = Bootstrap.getCore().getResourcePackManager();
			manager.exportResourcePack(file.toPath(), this.resourcePacks.getSelectionModel().getSelectedItems());
		});
	}
}
