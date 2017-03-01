package net.wheatlauncher.control.profiles;

import api.launcher.ARML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import net.launcher.control.ModCell;
import net.launcher.game.forge.ForgeMod;

import java.io.IOException;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.TreeSet;
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

	public ResourceBundle resources;
	private ObservableSet<ForgeMod> enabledMod = FXCollections.observableSet(new TreeSet<>(Comparator.comparing(ForgeMod::getModId)));

	public void initialize()
	{
		FilteredList<ForgeMod> modList = new FilteredList<>(ARML.core().getModManager().getAllElement());
		modList.predicateProperty().bind(Bindings.createObjectBinding(() -> textPredicate,
				search.textProperty()));
		SortedList<ForgeMod> sortedList = new SortedList<>(modList);
		sortedList.setComparator((o1, o2) ->
		{
			boolean contains1 = enabledMod.contains(o1),
					contains2 = enabledMod.contains(o2);
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
				if (item == null || empty)
					return;
				try
				{
					this.setGraphic(new ModCell(item, ARML.core().getModManager().getLogo(item))
					{
						{
							disableProperty().bind(Bindings.createBooleanBinding(() ->
									!enabledMod.contains(this.getValue()), enabledMod));
						}
					});
				}
				catch (IOException e)
				{
				}
			}
		});
		enable.textProperty().bind(Bindings.createStringBinding(() ->
						enabledMod.contains(mods.getSelectionModel().getSelectedItem()) ? resources.getString("disable") : resources.getString("enable"),
				mods.getSelectionModel().selectedIndexProperty(), enabledMod));
		enable.disableProperty().bind(Bindings.createBooleanBinding(() -> mods.getSelectionModel().isEmpty(), mods
				.getSelectionModel().selectedIndexProperty()));
	}

	public void enableMod()
	{
		ForgeMod selectedItem = mods.getSelectionModel().getSelectedItem();
		if (!enabledMod.contains(selectedItem))
			enabledMod.add(selectedItem);
		else enabledMod.remove(selectedItem);
	}
}
