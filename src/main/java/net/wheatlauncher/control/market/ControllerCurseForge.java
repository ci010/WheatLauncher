package net.wheatlauncher.control.market;

import api.launcher.ARML;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import net.launcher.control.ImageCell;
import net.launcher.control.modview.CurseForgeImageCell;
import net.launcher.services.curseforge.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ci010
 */
public class ControllerCurseForge
{
	public JFXListView<CurseForgeCategory> category;
	public JFXListView<CurseForgeProject> list;
	public JFXComboBox<CurseForgeProjectType> projectTypes;
	public JFXComboBox<String> options;
	public JFXComboBox<String> gameVersions;
	public JFXTextField searchField;

	private CurseForgeService service;
	private Map<String, Image> projectImageCache = new HashMap<>();
	private Map<String, Image> categoryImageCache = new HashMap<>();

	private Task<Image> loadImage(String url)
	{
		return new Task<Image>()
		{
			@Override
			protected Image call() throws Exception {return new Image(url);}
		};
	}

	public void initialize()
	{
		JFXDepthManager.setDepth(list, 2);
		projectTypes.getItems().setAll(CurseForgeProjectType.values());
		category.setCellFactory(param -> new JFXListCell<CurseForgeCategory>()
		{
			ImageCell<CurseForgeCategory> cell = new ImageCell<CurseForgeCategory>()
			{
				{
					icon.setFitHeight(32);
					icon.setFitWidth(32);
					imageContainer.setMaxSize(32, 32);
				}

				@Override
				protected Node buildContent()
				{
					Label label = new Label();
					label.setStyle("-fx-font-weight:bold; -fx-font-size:14px");
					label.textProperty().bind(Bindings.createStringBinding(() ->
					{
						CurseForgeCategory value = getValue();
						if (value == null) return "-";
						return value.getDefaultName();
					}, valueProperty()));
					return label;
				}
			};

			@Override
			public void updateItem(CurseForgeCategory item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty) setGraphic(null);
				else
				{
					cell.setValue(item);
					Image image = categoryImageCache.get(item.getDefaultName());
					if (image == null)
						ARML.taskCenter().runTask(loadImage(item.getImgUrl())).addEventHandler(WorkerStateEvent
										.WORKER_STATE_SUCCEEDED,
								event ->
								{
									Image img = (Image) event.getSource().getValue();
									categoryImageCache.put(item.getDefaultName(), img);
									cell.setImage(img);
								});
					cell.setImage(image);
					setGraphic(cell);
				}
			}
		});
		list.setCellFactory(param -> new JFXListCell<CurseForgeProject>()
		{
			private CurseForgeImageCell cell = new CurseForgeImageCell();

			{
				this.setMaxWidth(400);
			}

			@Override
			public void updateItem(CurseForgeProject item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty) setGraphic(null);
				else
				{
					cell.setValue(item);
					Image image = projectImageCache.get(item.getName());
					cell.setImage(image);
					if (image == null)
					{
						ARML.taskCenter().runTask(loadImage(item.getImageUrl())).addEventHandler(WorkerStateEvent
										.WORKER_STATE_SUCCEEDED,
								event ->
								{
									Image img = (Image) event.getSource().getValue();
									projectImageCache.put(item.getName(), img);
									cell.setImage(img);
								});
					}
					setGraphic(cell);
				}
			}
		});
		ARML.taskCenter().runTask(new Task<CurseForgeService.Cache<CurseForgeProject>>()
		{
			@Override
			protected CurseForgeService.Cache<CurseForgeProject> call() throws Exception
			{
				service = CurseForgeServices.newService
						(CurseForgeProjectType.TexturePacks);
				return service.view(null);
			}
		}).addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event ->
		{
			CurseForgeService.Cache<CurseForgeProject> cache = (CurseForgeService.Cache<CurseForgeProject>) event
					.getSource().getValue();
			list.getItems().setAll(cache.getCache());
			category.getItems().addAll(service.getCategories());
			options.getItems().setAll(service.getSortedOptions());
			gameVersions.getItems().setAll(service.getGameVersionConstrains());
		});
	}
}
