package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.image.Image;
import net.launcher.control.ModCell;
import net.launcher.game.mods.forge.ForgeMod;
import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.wheatlauncher.CommonBindings;
import net.wheatlauncher.ImageCache;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * @author ci010
 */
public class ControllerModView
{
	public JFXTextField search;
	public JFXListView<ForgeMod> mods;
	public JFXButton enable;
	public JFXButton config;
	private Predicate<ForgeMod> textPredicate = mod ->
			mod != null &&
					search.getText().isEmpty() || mod.getModId().toLowerCase().contains(search.getText()) || mod.getMetaData().getName().toLowerCase()
					.contains(search.getText()) || mod.getMetaData().getDescription().toLowerCase().contains(search.getText());

	private Predicate<ForgeMod> modVersionPredicate = mod ->
			mod != null && mod.getMinecraftVersionRange().containsVersion(new DefaultArtifactVersion(ARML.core().getProfileManager()
					.selecting().getVersion()));

	public ResourceBundle resources;
	private ObjectBinding<ObservableList<ForgeMod>> enableModsBinding;

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
		enableModsBinding = Bindings.createObjectBinding(() ->
						ARML.core().getModManager().getIncludeElementContainer(ARML.core().getProfileManager().selecting()),
				(ARML.core().getProfileManager().selectedProfileProperty()));

		FilteredList<ForgeMod> modList = new FilteredList<>(ARML.core().getModManager().getAllElement());
		modList.predicateProperty().bind(Bindings.createObjectBinding(() -> textPredicate.and(modVersionPredicate),
				search.textProperty(), CommonBindings.VERSION));
		SortedList<ForgeMod> sortedList = new SortedList<>(modList);
		sortedList.setComparator((o1, o2) ->
		{
			boolean contains1 = enableModsBinding.get().contains(o1),
					contains2 = enableModsBinding.get().contains(o2);
			if (contains1 && contains2) return 0;
			if (contains1) return 1;
			if (contains2) return -1;
			return 0;
		});
		mods.setItems(sortedList);
		mods.setCellFactory(param -> new JFXListCell<ForgeMod>()
		{
			ModCell cell = new ModCell()
			{
				{
					enableModsBinding.addListener(observable -> disableProperty().bind(Bindings.createBooleanBinding(() ->
							!enableModsBinding.get().contains(this.getValue()), enableModsBinding.get())));
				}
			};

			@Override
			public void updateItem(ForgeMod item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty)
				{
					this.setGraphic(null);
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
		enableModsBinding.addListener(observable ->
				enable.textProperty().bind(Bindings.createStringBinding(() ->
								enableModsBinding.get().contains(mods.getSelectionModel().getSelectedItem()) ?
										resources.getString("disable") :
										resources.getString("enable"),
						mods.getSelectionModel().selectedIndexProperty(), enableModsBinding.get())));

		enable.disableProperty().bind(Bindings.createBooleanBinding(() -> mods.getSelectionModel().isEmpty(), mods
				.getSelectionModel().selectedIndexProperty()));
		enableModsBinding.invalidate();
	}

	public void enableMod()
	{
		ForgeMod selectedItem = mods.getSelectionModel().getSelectedItem();
		if (!enableModsBinding.get().contains(selectedItem))
			enableModsBinding.get().add(selectedItem);
		else enableModsBinding.get().remove(selectedItem);
	}
}
