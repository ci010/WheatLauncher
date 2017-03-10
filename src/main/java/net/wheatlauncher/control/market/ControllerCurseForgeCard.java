package net.wheatlauncher.control.market;

import api.launcher.ARML;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import net.launcher.services.curseforge.CurseForgeProject;
import net.launcher.services.curseforge.CurseForgeProjectArtifact;
import net.launcher.services.curseforge.CurseForgeService;
import net.launcher.utils.DataSizeUnit;

import java.text.DateFormat;
import java.util.Optional;

/**
 * @author ci010
 */
public class ControllerCurseForgeCard
{
	public Label projectName, projectAuthor;
	public Label size, mcVersion, releaseType, releaseDate;
	public StackPane listOverlay;
	public StackPane root;
	public JFXListView<CurseForgeProjectArtifact> items;

	public SimpleObjectProperty<CurseForgeProject> project = new SimpleObjectProperty<>();
	private ObservableList<CurseForgeProjectArtifact> artifactList;

	private CurseForgeService service;

	private JFXSpinner spinner = new JFXSpinner();

	public void initialize()
	{
		Optional<CurseForgeService> service = ARML.instance().getComponent(CurseForgeService.class);
		if (!service.isPresent())
		{
			root.setDisable(true);
			return;
		}
		this.service = service.get();
		items.setCellFactory(param -> new JFXListCell<CurseForgeProjectArtifact>()
		{
			private BorderPane graphic = new BorderPane();

			{
				Label fName = new Label();
				fName.textProperty().bind(Bindings.createStringBinding(() ->
				{
					CurseForgeProjectArtifact item = getItem();
					if (item != null)
						return item.getFileName();
					return "-";
				}, itemProperty()));

				JFXRippler button = new JFXRippler();
				Icon icon = new Icon("DOWNLOAD");
				icon.setPadding(new Insets(8));
				button.setOnMouseReleased(event ->
				{
//					ARML.bus().postEvent(n)
				});
				button.setControl(icon);
				button.setMaskType(JFXRippler.RipplerMask.CIRCLE);
				graphic.setLeft(fName);
				graphic.setRight(button);
			}

			@Override
			public void updateItem(CurseForgeProjectArtifact item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item != null && !empty) setGraphic(graphic);
				else setGraphic(null);
			}
		});
		artifactList = items.getItems();

		size.textProperty().bind(Bindings.createStringBinding(() ->
		{
			CurseForgeProjectArtifact item = items.getSelectionModel().getSelectedItem();
			if (item != null)
				return DataSizeUnit.MB.fromByte(item.getFileSize()) + " MB";
			return "-";
		}, items.getSelectionModel().selectedIndexProperty()));
		mcVersion.textProperty().bind(Bindings.createStringBinding(() ->
				{
					CurseForgeProjectArtifact item = items.getSelectionModel().getSelectedItem();
					if (item != null)
						return item.getGameVersion();
					return "-";
				},
				items.getSelectionModel().selectedIndexProperty()));
		releaseType.textProperty().bind(Bindings.createStringBinding(() ->
		{
			CurseForgeProjectArtifact item = items.getSelectionModel().getSelectedItem();
			if (item != null)
				return item.getReleaseType();
			return "-";
		}, items.getSelectionModel().selectedIndexProperty()));
		releaseDate.textProperty().bind(Bindings.createStringBinding(() ->
		{
			CurseForgeProjectArtifact item = items.getSelectionModel().getSelectedItem();
			if (item != null && item.getDate().isPresent())
				return DateFormat.getInstance().format(item.getDate().get());
			return "-";
		}, items.getSelectionModel().selectedIndexProperty()));

		projectName.textProperty().bind(Bindings.createStringBinding(() ->
		{
			CurseForgeProject curseForgeProject = project.get();
			if (curseForgeProject == null) return "Unknown";
			return curseForgeProject.getName();
		}, project));
		projectAuthor.textProperty().bind(Bindings.createStringBinding(() ->
		{
			CurseForgeProject curseForgeProject = project.get();
			if (curseForgeProject == null) return "Unknown";
			return curseForgeProject.getAuthor();
		}, project));
//		projectDownloadCount.textProperty().bind(Bindings.createStringBinding(() ->
//		{
//			CurseForgeProject curseForgeProject = project.get();
//			if (curseForgeProject == null) return "-";
//			return curseForgeProject.getDownloadCount();
//		}, project));
//		projectDate.textProperty().bind(Bindings.createStringBinding(() ->
//		{
//			CurseForgeProject curseForgeProject = project.get();
//			if (curseForgeProject == null) return "-";
//			return DateFormat.getInstance().format(curseForgeProject.getLastTime());
//		}, project));
		project.addListener(observable ->
		{
			Task<CurseForgeService.Cache<CurseForgeProjectArtifact>> task = new Task<CurseForgeService.Cache<CurseForgeProjectArtifact>>()
			{
				{
					this.updateTitle("CurseForgeCardUpdate");
				}

				@Override
				protected CurseForgeService.Cache<CurseForgeProjectArtifact> call() throws Exception
				{
					return ControllerCurseForgeCard.this.service.artifact(project.get());
				}
			};
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event ->
			{
				CurseForgeService.Cache<CurseForgeProjectArtifact> value = (CurseForgeService.Cache<CurseForgeProjectArtifact>) event.getSource().getValue();
				artifactList.setAll(value.getCache());
				loadFinish();
			});
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, event -> loadFinish());
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> loadFinish());
			loading();
			ARML.taskCenter().runTask(task);
		});
	}

	private void loading()
	{
		if (!listOverlay.getChildren().contains(spinner))
			listOverlay.getChildren().add(spinner);
		items.setDisable(true);
	}

	private void loadFinish()
	{
		if (listOverlay.getChildren().contains(spinner))
			listOverlay.getChildren().remove(spinner);
		items.setDisable(false);
		items.getSelectionModel().select(0);
	}

}
