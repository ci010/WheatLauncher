package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.launcher.control.ModCell;
import net.launcher.game.mods.forge.ForgeMod;
import net.wheatlauncher.ImageCache;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

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
			search.getText().isEmpty() || mod.getModId().toLowerCase().contains(search.getText()) || mod.getMetaData().getName().toLowerCase()
					.contains(search.getText()) || mod.getMetaData().getDescription().toLowerCase().contains(search.getText());

	private Cache<String, byte[]> cache;

	private Image ensureImage(ModCell cell, ForgeMod mod)
	{
		Image image = null;
		String key = mod.getModId() + ":" + mod.getVersion().getVersionString();
		if (cache != null)
		{
			image = ImageCache.getCache(this.cache, key);
			if (image == null) loadImage(cell, mod);
		}
		else loadImage(cell, mod);
		return image;
	}

	private void loadImage(ModCell cell, ForgeMod mod)
	{
		String key = mod.getModId() + ":" + mod.getVersion().getVersionString();
		ARML.taskCenter().runTask(new Task<Image>()
		{
			@Override
			protected Image call() throws Exception
			{
				return ARML.core().getModManager().getLogo(mod);
			}
		}).addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event ->
		{
			Image image = (Image) event.getSource().getValue();
			try {ImageCache.putCache(cache, key, image);}
			catch (IOException e) {ARML.taskCenter().reportError("PutImageCache", e);}
			cell.setImage(image);
		});
	}

	public void initialize()
	{
		ARML.instance().getComponent(CacheManager.class)
				.ifPresent(cacheManager -> cache = ImageCache.getOrCreate(cacheManager, ImageCache.FORGE));
		FilteredList<ForgeMod> modList = new FilteredList<>(ARML.core().getModManager().getAllElement());
		modList.predicateProperty().bind(Bindings.createObjectBinding(() -> textPredicate,
				search.textProperty()));
		mods.setItems(modList);
		exportBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> mods.getSelectionModel().isEmpty(), mods
				.getSelectionModel().selectedIndexProperty()));
		mods.setCellFactory(param -> new JFXListCell<ForgeMod>()
		{
			private ModCell cell = new ModCell();

			@Override
			public void updateItem(ForgeMod item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty)
				{
					setGraphic(null);
					return;
				}
				if (cell.getValue() != item)
				{
					cell.setValue(item);
					cell.setImage(ensureImage(cell, item));
				}
				this.setGraphic(cell);
			}
		});
	}

	public void importMod()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("choose mod to import",
				"*.jar"));
		List<File> files = fileChooser.showOpenMultipleDialog(importBtn.getScene().getWindow());
		if (files != null)
		{
			for (File file : files)
				ARML.taskCenter().runTask(ARML.core().getModManager()
						.importMod(file.toPath()));
		}
	}

	public void exportMod()
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
			ARML.taskCenter().runTasks(selectedItems.stream().map(mod ->
					ARML.core().getModManager().exportMod(path, mod)).collect(Collectors.toList()));
		}
		else
		{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(resources.getString("mod.choose.file"));
			File f = fileChooser.showSaveDialog(mods.getScene().getWindow());
			if (f == null) return;
			Path file = f.toPath();
			ARML.taskCenter().runTask(ARML.core().getModManager().exportMod
					(file, selectedItems.get(0)));
		}
	}

}
