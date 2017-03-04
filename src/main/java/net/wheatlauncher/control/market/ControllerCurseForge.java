package net.wheatlauncher.control.market;

import api.launcher.ARML;
import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.fontawesome.Icon;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import net.launcher.control.ImageCell;
import net.launcher.services.curseforge.*;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

/**
 * @author ci010
 */
public class ControllerCurseForge
{
	public JFXListView<CurseForgeCategory> category;
	public JFXListView<CurseForgeProject> list;
	public JFXComboBox<CurseForgeProjectType> projectTypes;
	public JFXComboBox<String> options;
	public JFXComboBox<CurseForgeService.VersionCode> gameVersions;
	public JFXTextField searchField;
	public StackPane listOverlay;

	private CurseForgeService service;
	private Cache<String, Image> projectImageCache;
	private Cache<String, Image> categoryImageCache;

	private JFXSpinner spinner = new JFXSpinner();
	private Service<CurseForgeService.Cache<CurseForgeProject>> refreshService = new Service<CurseForgeService.Cache<CurseForgeProject>>()
	{
		{
			this.addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, event ->
			{
				if (!listOverlay.getChildren().contains(spinner))
					listOverlay.getChildren().add(spinner);
				list.setDisable(true);
			});
			this.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event ->
			{
				CurseForgeService.Cache<CurseForgeProject> cache = (CurseForgeService.Cache<CurseForgeProject>) event.getSource().getValue();
				list.getItems().setAll(cache.getCache());
			});
		}

		@Override
		protected Task<CurseForgeService.Cache<CurseForgeProject>> createTask()
		{
			return ARML.taskCenter().listenTask(new Task<CurseForgeService.Cache<CurseForgeProject>>()
			{
				@Override
				protected void done()
				{
					Platform.runLater(() ->
					{
						list.setDisable(false);
						if (listOverlay.getChildren().contains(spinner))
							listOverlay.getChildren().remove(spinner);
					});
				}

				@Override
				protected CurseForgeService.Cache<CurseForgeProject> call() throws Exception
				{
					CurseForgeService.Option option = new CurseForgeService.Option().setCategory(category.getSelectionModel().getSelectedItem())
							.setGameVersionConstrain(gameVersions.getSelectionModel().getSelectedItem()).setSortOption(options.getSelectionModel()
									.getSelectedItem());
					return service.view(option);
				}
			});
		}
	};

	private Task<Image> loadImage(String url)
	{
		return ARML.taskCenter().runTask(new Task<Image>()
		{
			@Override
			protected Image call() throws Exception {return new Image(url);}
		});
	}

	public void initialize()
	{
		CacheConfiguration<String, Image> cof = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Image.class,
				ResourcePoolsBuilder.newResourcePoolsBuilder().heap(32, MemoryUnit.MB))
				.build();
		CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder().withCache("projectImageCache", cof).withCache
				("categoryImageCache", cof).build(true);

		projectImageCache = manager.getCache("projectImageCache", String.class, Image.class);
		categoryImageCache = manager.getCache("categoryImageCache", String.class, Image.class);

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
						loadImage(item.getImgUrl()).addEventHandler(WorkerStateEvent
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
			private ImageCell<CurseForgeProject> cell = projectCell();

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
						loadImage(item.getImageUrl()).addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
								event ->
								{
									Image img = (Image) event.getSource().getValue();
									projectImageCache.put(item.getName(), img);
									cell.setImage(img);
								});
					setGraphic(cell);
				}
			}
		});
		ARML.taskCenter().runTask(new Task<CurseForgeService>()
		{
			@Override
			protected CurseForgeService call() throws Exception
			{
				return CurseForgeServices.newService(CurseForgeProjectType.TexturePacks);
			}
		}).addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event ->
		{
			Object value = event.getSource().getValue();
			service = (CurseForgeService) value;
			category.getItems().addAll(service.getCategories());
			options.getItems().setAll(service.getSortedOptions());
			gameVersions.getItems().setAll(service.getGameVersionConstrains());
			category.getSelectionModel().select(0);
			options.getSelectionModel().select(0);
			gameVersions.getSelectionModel().select(0);
			refreshService.restart();
		});

		InvalidationListener refresh = observable -> refreshService.restart();
		category.getSelectionModel().selectedIndexProperty().addListener(refresh);
		gameVersions.getSelectionModel().selectedIndexProperty().addListener(refresh);
		options.getSelectionModel().selectedItemProperty().addListener(refresh);
	}

	private ImageCell<CurseForgeProject> projectCell()
	{
		return new ImageCell<CurseForgeProject>()
		{
			{
				JFXButton download = new JFXButton();
				download.prefWidthProperty().bind(imageContainer.widthProperty());
				download.prefHeightProperty().bind(imageContainer.heightProperty());
				Icon icon = new Icon("DOWNLOAD");
				icon.setPadding(new Insets(10));
				icon.setTextFill(Color.WHITE);
				download.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0.8),
						CornerRadii.EMPTY, Insets.EMPTY)));
				download.setGraphic(icon);

				HBox btnOverlay = new HBox();
				btnOverlay.visibleProperty().bind(Bindings.createBooleanBinding(this::isHover, this.hoverProperty()));
				btnOverlay.setAlignment(Pos.CENTER_LEFT);
				btnOverlay.getChildren().addAll(download);
				btnOverlay.setOnMouseClicked(Event::consume);
				btnOverlay.setOnMousePressed(Event::consume);
				btnOverlay.setOnMouseReleased(Event::consume);
				this.getChildren().add(btnOverlay);
			}

			@Override
			protected Node buildContent()
			{
				this.setMaxWidth(400);
				VBox box = new VBox();

				TextFlow textFlow = new TextFlow();
				Text name = new Text();
				box.setPadding(new Insets(0, 0, 0, 5));
				name.textProperty().bind(Bindings.createStringBinding(() ->
						{
							if (getValue() != null)
								return getValue().getName();
							else return "Unknown";
						}, valueProperty()
				));
				name.setStyle("-fx-fill:WHITE; -fx-font-size:14;-fx-font-weight:bold;");
				Text author = new Text();
				author.textProperty().bind(Bindings.createStringBinding(() ->
						{
							if (getValue() != null)
								return " by " + getValue().getAuthor();
							else return " by Unknown";
						}, valueProperty()
				));
				author.setStyle("-fx-fill:WHITE;");

				textFlow.getChildren().addAll(name, author);

				textFlow.setMaxWidth(300);

				Icon down = new Icon("DOWNLOAD", "1em");
				Label downloadCount = new Label();
				downloadCount.textProperty().bind(Bindings.createStringBinding(() ->
				{
					CurseForgeProject value = getValue();
					if (value != null)
						return getValue().getDownloadCount();
					else return "-";
				}, valueProperty()));
				downloadCount.setGraphic(down);
				downloadCount.setContentDisplay(ContentDisplay.LEFT);

				Icon clock = new Icon("CLOCK_ALT", "1em");
				Label date = new Label();
				date.textProperty().bind(Bindings.createStringBinding(() ->
				{
					CurseForgeProject value = getValue();
					if (value != null)
						return getValue().getLastTime().toString();
					else return "-";
				}, valueProperty()));
				date.setGraphic(clock);
				date.setContentDisplay(ContentDisplay.LEFT);

				HBox second = new HBox(downloadCount, date);
				second.setSpacing(5);

				Label description = new Label();
				description.setMaxWidth(300);
				description.setWrapText(true);
				description.textProperty().bind(Bindings.createStringBinding(() ->
						{
							if (getValue() != null)
								return getValue().getDescription();
							else return "Unknown";
						}, valueProperty()
				));
				box.setSpacing(5);
				box.getChildren().addAll(textFlow, second, description);


				return box;
			}
		};
	}

}
