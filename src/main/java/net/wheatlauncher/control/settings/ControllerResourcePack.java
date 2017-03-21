package net.wheatlauncher.control.settings;

import api.launcher.Shell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.launcher.control.ResourcePackCell;
import net.launcher.game.ResourcePack;
import net.launcher.utils.resource.Resource;
import net.wheatlauncher.ImageCache;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author ci010
 */
public class ControllerResourcePack
{
	public JFXListView<Resource<ResourcePack>> resourcePacks;

	public JFXButton importBtn;
	public JFXButton exportBtn;
	public JFXTextField search;

	private Cache<String, byte[]> imageCache;

	private Image getImage(Resource<ResourcePack> resourcePack)
	{
		Image cache = ImageCache.getCache(imageCache, resourcePack.getHash());
		if (cache == null)
		{
			Shell.instance().buildAndExecute("resourcepacks.icon", resourcePack.getHash()).setOnSucceeded(event ->
					Shell.instance().executeImmediately("PUT_CACHE",
							() -> ImageCache.putCache(imageCache, resourcePack.getHash(), (Image) event.getSource()
									.getValue()))
			);
		}
		return cache;
	}

	public void initialize()
	{
		imageCache = ImageCache.getOrCreate(Shell.instance().getInstance(CacheManager.class), "resourcepacks");

		resourcePacks.expandedProperty().bind(Bindings.createBooleanBinding(() -> true, resourcePacks.expandedProperty()));
		resourcePacks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		FilteredList<Resource<ResourcePack>> resourcePacks = new FilteredList<>(Shell.instance().getAllResourcePacksResources());
		resourcePacks.predicateProperty().bind(Bindings.createObjectBinding(() ->
				(Predicate<Resource<ResourcePack>>) resourcePack ->
						resourcePack.getContainData().getPackName().contains(search.getText()), search.textProperty()));
		this.resourcePacks.setItems(resourcePacks);
		this.resourcePacks.setCellFactory(param -> new JFXListCell<Resource<ResourcePack>>()
		{
			private ResourcePackCell cell = new ResourcePackCell();

			@Override
			public void updateItem(Resource<ResourcePack> item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty) setGraphic(null);
				else
				{
					cell.setValue(item.getContainData());
					cell.setImage(getImage(item));
					setGraphic(cell);
				}
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
					Shell.instance().buildAndExecute("resourcepacks.import", file.getAbsolutePath());
//							.taskCenter().runTask(ARML.core()
//							.getResourcePackManager()
//							.importResourcePack(file.toPath()));
		});
		exportBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> this.resourcePacks.getSelectionModel()
				.isEmpty(), this.resourcePacks.getSelectionModel().selectedItemProperty()));
		exportBtn.setOnAction(event ->
		{
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("Select save location");
			File file = fileChooser.showDialog(exportBtn.getScene().getWindow());
			if (file == null) return;
			ObservableList<Resource<ResourcePack>> items = this.resourcePacks.getSelectionModel().getSelectedItems();
			String[] arr = new String[items.size() + 1];
			arr[0] = file.getAbsolutePath();
			for (int i = 0; i < items.size(); i++)
				arr[i + 1] = items.get(i).getHash();
			Shell.instance().buildAndExecute("resourcepacks.export", arr);
//			ResourcePackManager manager = ARML.core().getResourcePackManager();
//			ARML.taskCenter().runTask(manager.exportResourcePack(file.toPath(), this
//					.resourcePacks.getSelectionModel().getSelectedItems()));
		});
	}
}
