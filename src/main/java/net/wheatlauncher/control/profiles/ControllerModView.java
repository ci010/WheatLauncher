package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TreeSortMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.launcher.game.forge.ForgeMod;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
public class ControllerModView
{
	public StackPane root;

	public JFXTreeTableView<ModCol> mods;

	public JFXTreeTableColumn<ModCol, String> name;
	public JFXTreeTableColumn<ModCol, String> version;
	public JFXTreeTableColumn<ModCol, String> minecraftVersion;
	public JFXTreeTableColumn<ModCol, Boolean> enabled;

	public JFXTextField searchField;
	public Label description;
	public VBox descriptionBox;
	public JFXButton details;

	@PostConstruct
	public void init()
	{


	}

	public void reload()
	{
		//		modId.setCellValueFactory(features -> features.getValue().getValue().modid);
		ObservableList<ModCol> modCols = FXCollections.observableArrayList();

//		modCols.addAll(Core.INSTANCE.().getAllElement().stream().map(ModCol::new).collect(Collectors.toList()));

		mods.setRoot(new RecursiveTreeItem<>(modCols, RecursiveTreeObject::getChildren));
		mods.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		mods.setShowRoot(false);

		searchField.textProperty().addListener((o, oldVal, newVal) ->
				mods.setPredicate(modCol -> modCol.getValue().release.getModId().contains(newVal) ||
						modCol.getValue().name.get().contains(newVal) ||
						modCol.getValue().version.get().contains(newVal) ||
						modCol.getValue().mcVersion.get().contains(newVal) ||
						modCol.getValue().description.get().contains(newVal)));
		name.setCellValueFactory(feature -> feature.getValue().getValue().name);
		version.setCellValueFactory(feature -> feature.getValue().getValue().version);
		enabled.setCellValueFactory(feature-> feature.getValue().getValue().enabled);
//		description.setCellValueFactory(feature -> feature.getValue().getValue().description);
		minecraftVersion.setCellValueFactory(feature -> feature.getValue().getValue().mcVersion);

	}

	private enum ModType
	{
		LOCAL, REMOTE, ARCHIVE
	}

	private class ModCol extends RecursiveTreeObject<ModCol>
	{
		StringProperty name, version, description, mcVersion;
		ObjectProperty<ModType> type;
		ForgeMod release;
		BooleanProperty enabled;

		ModCol(ForgeMod release)
		{
			this.release = release;
			name = new SimpleStringProperty(release.getMetaData().getName());
			version = new SimpleStringProperty(release.getMetaData().getVersion());
			description = new SimpleStringProperty(release.getMetaData().getDescription());
			mcVersion = new SimpleStringProperty(release.getMetaData().getAcceptMinecraftVersion());
			enabled = new SimpleBooleanProperty(false);
		}
	}
}
