package net.wheatlauncher.control.profiles;

import api.launcher.ARML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import net.launcher.control.ModCell;
import net.launcher.game.forge.ForgeMod;
import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

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
			search.getText().isEmpty() || mod.getModId().toLowerCase().contains(search.getText()) || mod.getMetaData().getName().toLowerCase()
					.contains(search.getText()) || mod.getMetaData().getDescription().toLowerCase().contains(search.getText());

	private Predicate<ForgeMod> modVersionPredicate = mod ->
			mod.getMinecraftVersionRange().containsVersion(new DefaultArtifactVersion(ARML.core().getProfileManager()
					.selecting().getVersion()));

	public ResourceBundle resources;
	private ObjectBinding<ObservableList<ForgeMod>> enableModsBinding;

	public void initialize()
	{
		enableModsBinding = Bindings.createObjectBinding(() ->
						ARML.core().getModManager().getIncludeElementContainer(ARML.core().getProfileManager().selecting()),
				(ARML.core().getProfileManager().selectedProfileProperty()));

		FilteredList<ForgeMod> modList = new FilteredList<>(ARML.core().getModManager().getAllElement());
		modList.predicateProperty().bind(Bindings.createObjectBinding(() -> textPredicate, search.textProperty()));
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
			@Override
			public void updateItem(ForgeMod item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty) return;
				this.setGraphic(new ModCell(item, ARML.core().getModManager().getLogo(item))
				{
					{
						enableModsBinding.addListener(observable -> disableProperty().bind(Bindings.createBooleanBinding(() ->
								!enableModsBinding.get().contains(this.getValue()), enableModsBinding.get())));
					}
				});
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
