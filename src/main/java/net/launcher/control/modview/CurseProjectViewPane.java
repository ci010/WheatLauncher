package net.launcher.control.modview;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import net.launcher.services.curseforge.CurseForgeProject;
import net.launcher.services.curseforge.CurseForgeService;

import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class CurseProjectViewPane extends StackPane
{
	private ScrollPane scrollControl;
	private JFXMasonryPane container;
	private ObjectProperty<CurseForgeService.Cache<CurseForgeProject>> projectCache = new SimpleObjectProperty<>();

	public CurseProjectViewPane() {init();}

	public CurseForgeService.Cache<CurseForgeProject> getProjectCache()
	{
		return projectCache.get();
	}

	public ObjectProperty<CurseForgeService.Cache<CurseForgeProject>> projectCacheProperty()
	{
		return projectCache;
	}

	public void setProjectCache(CurseForgeService.Cache<CurseForgeProject> projectCache) {this.projectCache.set(projectCache);}

	public ScrollPane getScrollControl() {return scrollControl;}

	public JFXMasonryPane getContainer() {return container;}

	private StringProperty filter = new SimpleStringProperty();

	public String getFilter()
	{
		return filter.get();
	}

	public StringProperty filterProperty()
	{
		return filter;
	}

	public void setFilter(String filter)
	{
		this.filter.set(filter);
	}

	private ObservableList<CurseForgeProjectCard> projectCards = FXCollections.observableArrayList();

	protected void init()
	{
		scrollControl = new ScrollPane(container = new JFXMasonryPane());
		scrollControl.setFitToHeight(true);
		scrollControl.setFitToWidth(true);
		this.projectCache.addListener(o ->
		{
			CurseForgeService.Cache<CurseForgeProject> cache = projectCache.get();
			projectCards.setAll(cache.getCache().stream().map(CurseForgeProjectCard::new).collect(Collectors.toList()));
			container.getChildren().setAll(projectCards);
		});
		this.getChildren().add(scrollControl);
		filter.addListener(o ->
		{
			String filter = getFilter();
			if (filter == null || filter.equals("")) { container.getChildren().setAll(projectCards); return;}
			container.getChildren().setAll(new FilteredList<>((ObservableList<CurseForgeProjectCard>) projectCache, card ->
					card.getProject().getName().contains(filter) || card.getProject().getAuthor().contains(filter) ||
							card.getProject().getDescription().contains(filter)));
		});
	}

}
