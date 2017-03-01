package net.wheatlauncher.control.settings;

import api.launcher.ARML;
import api.launcher.ResourcePackManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.launcher.control.ResourcePackCell;
import net.launcher.game.ResourcePack;

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
		resourcePacks.expandedProperty().bind(Bindings.createBooleanBinding(() -> true, resourcePacks.expandedProperty()));
		resourcePacks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		FilteredList<ResourcePack> resourcePacks = new FilteredList<>(ARML.core().getResourcePackManager().getAllElement());
		resourcePacks.predicateProperty().bind(Bindings.createObjectBinding(() ->
				(Predicate<ResourcePack>) resourcePack -> resourcePack.getPackName().contains(search.getText()), search.textProperty()));
		this.resourcePacks.setItems(resourcePacks);
		this.resourcePacks.setCellFactory(param -> new JFXListCell<ResourcePack>()
		{
			@Override
			public void updateItem(ResourcePack item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty) setGraphic(null);
				else
					this.setGraphic(new ResourcePackCell(item, ARML.core().getResourcePackManager().getIcon(item)));
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
					ARML.core().getTaskCenter().runTask(ARML.core().getResourcePackManager()
							.importResourcePack(file.toPath()));
		});
		exportBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> this.resourcePacks.getSelectionModel()
				.isEmpty(), this.resourcePacks.getSelectionModel().selectedItemProperty()));
		exportBtn.setOnAction(event ->
		{
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("Select save location");
			File file = fileChooser.showDialog(exportBtn.getScene().getWindow());
			if (file == null) return;
			ResourcePackManager manager = ARML.core().getResourcePackManager();
			ARML.core().getTaskCenter().runTask(manager.exportResourcePack(file.toPath(), this
					.resourcePacks.getSelectionModel().getSelectedItems()));
		});
	}
}
