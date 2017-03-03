package net.launcher.control.modview;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.BorderPane;
import net.launcher.services.curseforge.CurseForgeProject;
import net.launcher.services.curseforge.CurseForgeService;

/**
 * @author ci010
 */
public class CurseForgeViewSlider extends BorderPane
{
	private ObjectProperty<CurseForgeService.Cache<CurseForgeProject>> projectCache = new SimpleObjectProperty<>();

	public CurseForgeService.Cache<CurseForgeProject> getProjectCache()
	{
		return projectCache.get();
	}

	public ObjectProperty<CurseForgeService.Cache<CurseForgeProject>> projectCacheProperty()
	{
		return projectCache;
	}

	public void setProjectCache(CurseForgeService.Cache<CurseForgeProject> projectCache)
	{
		this.projectCache.set(projectCache);
		selectionModel.select(0);
		this.centerProperty().bind(Bindings.createObjectBinding(() ->
		{
			CurseForgeProject item = selectionModel.getSelectedItem();
			if (item != null)
				return new CurseForgeProjectCard(item);
			return null;
		}, selectionModel.selectedIndexProperty()));
	}

	private SelectionModel<CurseForgeProject> selectionModel = new SingleSelectionModel<CurseForgeProject>()
	{
		@Override
		protected CurseForgeProject getModelItem(int index)
		{
			CurseForgeService.Cache<CurseForgeProject> cache = projectCache.get();
			if (cache != null)
				return cache.getCache().get(index);
			return null;
		}

		@Override
		protected int getItemCount()
		{
			CurseForgeService.Cache<CurseForgeProject> cache = projectCache.get();
			if (cache != null)
				return cache.getCache().size();
			return 0;
		}
	};

	public CurseForgeViewSlider()
	{
		Icon left = new Icon("ANGLE_LEFT");
		Icon right = new Icon("ANGLE_RIGHT");
		JFXButton left1 = new JFXButton();
		left1.setGraphic(left);
		JFXButton right1 = new JFXButton();
		right1.setGraphic(right);

		BorderPane.setAlignment(left1, Pos.CENTER);
		BorderPane.setAlignment(right1, Pos.CENTER);

		left1.setOnAction(event -> selectionModel.selectPrevious());
		right1.setOnAction(event -> selectionModel.selectNext());

		this.setLeft(left1);
		this.centerProperty().bind(Bindings.createObjectBinding(() ->
		{
			CurseForgeProject item = selectionModel.getSelectedItem();
			if (item != null)
				return new CurseForgeProjectCard(item);
			return null;
		}, selectionModel.selectedIndexProperty()));
		this.setRight(right1);
		this.setPrefHeight(400);
		this.setMinHeight(400);
	}

}
