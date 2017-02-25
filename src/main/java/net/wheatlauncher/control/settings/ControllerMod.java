package net.wheatlauncher.control.settings;

import api.launcher.ARML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.launcher.control.ImageCell;
import net.launcher.game.forge.ForgeMod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ControllerMod
{
	public JFXButton importBtn;
	public JFXTextField search;
	public JFXButton exportBtn;
	public JFXListView<ForgeMod> mods;

	public ResourceBundle resources;

	private Predicate<ForgeMod> textPredicate = mod ->
			search.getText().isEmpty() ||
					mod.getModId().toLowerCase().contains(search.getText()) || mod.getMetaData().getName().toLowerCase()
					.contains(search.getText()) || mod.getMetaData().getDescription().toLowerCase().contains(search.getText());


	public void initialize()
	{
		FilteredList<ForgeMod> modList = new FilteredList<>(ARML.core().getModManager().getAllElement());
		modList.predicateProperty().bind(Bindings.createObjectBinding(() -> textPredicate,
				search.textProperty()));
		mods.setItems(modList);
		exportBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> mods.getSelectionModel().isEmpty(), mods
				.getSelectionModel().selectedIndexProperty()));
		mods.setCellFactory(param -> new JFXListCell<ForgeMod>()
		{
			@Override
			public void updateItem(ForgeMod item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty)
					return;
				try
				{
					this.setGraphic(new ModCell(item, ARML.core().getModManager().getLogo(item)));
				}
				catch (IOException e)
				{
				}
			}
		});
	}

	public void importMod(ActionEvent actionEvent)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("choose mod to import",
				"*.jar"));
		List<File> files = fileChooser.showOpenMultipleDialog(importBtn.getScene().getWindow());
		if (files != null)
		{
			for (File file : files)
				ARML.core().getTaskCenter().runTask(ARML.core().getModManager()
						.importMod(file.toPath()));
		}
	}

	public void exportMod(ActionEvent actionEvent)
	{
		MultipleSelectionModel<ForgeMod> selectionModel = mods.getSelectionModel();
		ObservableList<ForgeMod> selectedItems = selectionModel.getSelectedItems();
		if (selectedItems.size() > 1)
		{
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle(resources.getString("mod.choose.dir"));
			File f = chooser.showDialog(mods.getScene().getWindow());
			if (f == null) return;
			Path path = f.toPath();
			ARML.core().getTaskCenter().runTasks(selectedItems.stream().map(mod ->
					ARML.core().getModManager().exportMod(path, mod)).collect(Collectors.toList()));
		}
		else
		{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(resources.getString("mod.choose.file"));
			File f = fileChooser.showSaveDialog(mods.getScene().getWindow());
			if (f == null) return;
			Path file = f.toPath();
			ARML.core().getTaskCenter().runTask(ARML.core().getModManager().exportMod
					(file, selectedItems.get(0)));
		}
	}

	private class ModCell extends ImageCell<ForgeMod>
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
			Label nameAndID = new Label(), versionLabel = new Label(), descript = new Label();
			nameAndID.textProperty().bind(Bindings.createStringBinding(() ->
					getValue().getMetaData().getName() + " (" + getValue().getModId() + ")", valueProperty()));
			versionLabel.textProperty().bind(Bindings.createStringBinding(() ->
					getValue().getMetaData().getVersion() + " (" + getValue().getMetaData().getAcceptMinecraftVersion
							() + ")", valueProperty()));
			descript.textProperty().bind(Bindings.createStringBinding(() ->
					getValue().getMetaData().getDescription(), valueProperty()));
			box.getChildren().addAll(nameAndID, versionLabel, descript);
//			new CustomMenuItem();
			return box;
		}
	}
}
