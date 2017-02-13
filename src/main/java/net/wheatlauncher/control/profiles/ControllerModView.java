package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
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

	public JFXTableView<ForgeMod> mods;

	public TableColumn<ForgeMod, String> name;
	public TableColumn<ForgeMod, String> version;
	public TableColumn<ForgeMod, String> minecraftVersion;
	public TableColumn<ForgeMod, Boolean> enabled;

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
//		ObservableList<ModCol> modCols = FXCollections.observableArrayList();
//
//		new FilteredList<>(modCols);
//		modCols.addAll(Core.INSTANCE.().getAllElement().stream().map(ModCol::new).collect(Collectors.toList()));

//		mods.setRoot(new RecursiveTreeItem<>(modCols, RecursiveTreeObject::getChildren));
//		mods.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
//		mods.setShowRoot(false);

//		searchField.textProperty().addListener((o, oldVal, newVal) ->
//				mods.setPredicate(modCol -> modCol.getValue().release.getModId().contains(newVal) ||
//						modCol.getValue().name.get().contains(newVal) ||
//						modCol.getValue().version.get().contains(newVal) ||
//						modCol.getValue().mcVersion.get().contains(newVal) ||
//						modCol.getValue().description.get().contains(newVal)));
//		name.setCellValueFactory(feature -> feature.getValue().getValue().name);
//		version.setCellValueFactory(feature -> feature.getValue().getValue().version);
//		enabled.setCellValueFactory(feature -> feature.getValue().getValue().enabled);
//		description.setCellValueFactory(feature -> feature.getValue().getValue().description);
//		minecraftVersion.setCellValueFactory(feature -> feature.getValue().getValue().mcVersion);

	}
}
