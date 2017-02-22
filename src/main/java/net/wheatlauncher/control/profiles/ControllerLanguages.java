package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import net.launcher.Logger;
import net.launcher.assets.MinecraftAssetsManager;
import net.launcher.assets.MinecraftVersion;
import net.launcher.control.MinecraftOptionButton;
import net.launcher.game.Language;
import net.launcher.profile.LaunchProfile;
import net.launcher.profile.LaunchProfileManager;
import net.wheatlauncher.MainApplication;

import java.util.function.Predicate;

/**
 * @author ci010
 */
public class ControllerLanguages
{
	public StackPane root;
	public JFXTableView<Language> languageTable;
	public TableColumn<Language, String> id;
	public TableColumn<Language, String> name;
	public TableColumn<Language, String> region;
	public TableColumn<Language, String> bidi;

	public JFXTextField search;
	public MinecraftOptionButton useUnicode;
	public JFXButton confirm;

	private InvalidationListener refresh = observable -> refresh();

	private ChangeListener<MinecraftVersion> listener = (observable, oldV, newV) ->
	{
		if (oldV != null) oldV.stateProperty().removeListener(refresh);
		if (newV != null) newV.stateProperty().addListener(refresh);
	};

	private ObservableList<Language> languageLists;

	private void refresh()
	{
		LaunchProfile selecting = MainApplication.getCore().getProfileManager().selecting();
		MinecraftAssetsManager assetsManager = MainApplication.getCore().getAssetsManager();
		MinecraftVersion version = assetsManager.getVersion(selecting.getVersion());
		MainApplication.getLogger().info("refresh lang " + version);
		if (version != null)
		{
			Task<Language[]> task = assetsManager.getRepository().getLanguages(version);
			task.setOnSucceeded(event ->
			{
				Worker<Language[]> source = event.getSource();
				languageLists.setAll(source.getValue());
			});
			MainApplication.getCore().getTaskCenter().runTask(task);
		}
	}

	public void initialize()
	{
		Logger.trace("Language Init");
		languageLists = FXCollections.observableArrayList();
		FilteredList<Language> filteredList = new FilteredList<>(languageLists);
		filteredList.predicateProperty().bind(Bindings.createObjectBinding(() ->
				(Predicate<Language>) language -> language.getId().contains(search.getText()) ||
						language.getName().contains(search.getText()) ||
						language.getRegion().contains(search.getText()), search.textProperty()));
		SortedList<Language> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(languageTable.comparatorProperty());
		languageTable.setItems(sortedList);
		id.setCellValueFactory(param -> Bindings.createStringBinding(() -> param.getValue().getId()));
		region.setCellValueFactory(param -> Bindings.createStringBinding(() -> param.getValue().getRegion()));
		name.setCellValueFactory(param -> Bindings.createStringBinding(() -> param.getValue().getName()));
		bidi.setCellValueFactory(param -> Bindings.createStringBinding(() -> String.valueOf(param.getValue().isBidirectional())));
		confirm.setOnAction(event ->
		{
			//toggle language
		});
		MainApplication.getCore().getProfileManager().selectedProfileProperty().addListener((observable, oldV, newV) ->
		{
			LaunchProfileManager profileManager = MainApplication.getCore().getProfileManager();
			profileManager.getProfile(oldV).ifPresent(profile -> profile.versionBinding().removeListener(listener));
			profileManager.getProfile(newV).ifPresent(profile -> profile.versionBinding().addListener(listener));
		});
		refresh();
	}
}
