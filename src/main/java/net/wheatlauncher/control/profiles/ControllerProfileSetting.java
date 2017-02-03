package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.control.profile.base.ProfileTableSelector;
import net.launcher.control.versions.MinecraftVersionPicker;
import net.launcher.profile.LaunchProfile;
import net.launcher.utils.Logger;
import net.launcher.utils.Tasks;
import net.wheatlauncher.control.utils.FXMLInnerController;
import net.wheatlauncher.control.utils.ReloadableController;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.parsing.Versions;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author ci010
 */
@FXMLController("/fxml/profiles/ProfileSetting.fxml")
public class ControllerProfileSetting implements ReloadableController
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	/*Profile*/
	public Label profileLabel;
//	public ValidationFacade validProfile;

	@FXMLInnerController
	public ControllerSettingProfile profilePopupController;

	//	public JFXRippler editProfileRegion;
	public ProfileTableSelector profile;

	/*Version*/
	public MinecraftVersionPicker versions;
	public ValidationFacade validVersion;

	/*Sub-settings*/
	public JFXTabPane optionsTab;

	@FXMLInnerController
	public ControllerCommonSetting commonSettingController;
	public VBox commonSetting;

	@FXMLInnerController
	public ControllerGameSetting gameSettingController;
	public StackPane gameSetting;

//	@FXMLInnerController
//	public ControllerLanguages languageSettingController;
//	public StackPane languageSetting;

	@FXMLInnerController
	public ControllerResourcePackView resourcePackSettingController;
	public StackPane resourcePackSetting;

	@FXMLInnerController
	public ControllerModView modSettingController;
	public StackPane modSetting;

	/*root*/
	@FXML
	private VBox root;

	public JFXDialog rootDialog;

	@PostConstruct
	public void setup()
	{
		Logger.trace("init");
		rootDialog.setOverlayClose(true);
		initProfilePopupMenu();
		initProfile();
		initVersion();
	}

	@PreDestroy
	public void distroy()
	{
		Logger.trace("destroy");
	}

	private ObservableList<String> versionList;

	private void updateVersionList()
	{
		versionList.clear();
		versionList.addAll(Versions.getVersions(Bootstrap.getCore().getProfileManager().selecting()
				.getMinecraftLocation()));
	}

	private void initSettings()
	{

	}


	private void initVersion()
	{
		Bootstrap.getCore().getService().scheduleAtFixedRate(new SimpleFileWatcher(Paths.get("versions"),
						() -> Platform.runLater(ControllerProfileSetting.this::updateVersionList)), 5, 5,
				TimeUnit.SECONDS);
		versions.setUpdateFunction(call ->
		{
			Bootstrap.getCore().getService().submit(() ->
			{
				MinecraftDownloader downloader = MinecraftDownloaderBuilder.buildDefault();
				return downloader.fetchRemoteVersionList(Tasks.adept(call)).get();
			});
			return null;
		});
//		versionList = FXCollections.observableArrayList(Versions.getVersions(Bootstrap.getCore().getProfileManager()
//			.selecting()
//				.getMinecraftLocation()));
//		versions.itemsProperty().bind(Bindings.createObjectBinding(() ->
//				{
//					updateVersionList();
//					return versionList;
//				},
//				Bootstrap.getCore().getProfileManager().selectedProfileProperty()));
//		versions.valueProperty().bind(Bindings.createStringBinding(() ->
//						Bootstrap.getCore().getProfileManager().selecting().getVersion(),
//				Bootstrap.getCore().getProfileManager().selectedProfileProperty()));
//		versions.getJFXEditor().textProperty().bind(
//				Bindings.createStringBinding(() -> Bootstrap.getCore().getProfileManager().selecting().getVersion(), Bootstrap.getCore()
//						.getProfileManager().selectedProfileProperty()));
	}

	private String findIdByName(String name)
	{
		for (Map.Entry<String, LaunchProfile> entry : Bootstrap.getCore().getProfileManager().getProfilesMap().entrySet())
			if (entry.getValue().getDisplayName().equals(name)) return entry.getKey();
		return null;
	}

	private void initProfile()
	{
		ObservableMap<String, LaunchProfile> allProfiles = Bootstrap.getCore().getProfileManager().getProfilesMap();
		profile.getProfiles().addAll(allProfiles.values());
		allProfiles.addListener((MapChangeListener<String, LaunchProfile>) change ->
		{
			LaunchProfile valueAdded = change.getValueAdded();
			if (valueAdded != null)
			{
				profile.getProfiles().add(valueAdded);
			}
			LaunchProfile valueRemoved = change.getValueRemoved();
			if (valueAdded != null)
			{
				profile.getProfiles().remove(valueRemoved);
			}
		});
//		ObjectBinding<ObservableList<String>> itemsBinding = Bindings.createObjectBinding(() ->
//						FXCollections.observableArrayList(Bootstrap.getCore().getProfileManager().getProfilesMap()
//								.values().stream().map(LaunchProfile::getDisplayName)
//								.collect(Collectors.toList())),
//				Bootstrap.getCore().getProfileManager().getProfilesMap());

//		profile.itemsProperty().bind(itemsBinding);
//		InvalidationListener listener = (observable) ->
//		{
//			profile.valueProperty().bind(Bindings.createStringBinding(() ->
//					{
//						Platform.runLater(itemsBinding::invalidate);
//						return Bootstrap.getCore().getProfileManager().selecting().getDisplayName();
//					},
//					Bootstrap.getCore().getProfileManager().selecting().displayNameProperty()));
//		};
//		Bootstrap.getCore().getProfileManager().selectedProfileProperty().addListener(listener);
//		listener.invalidated(null);
//
//		profile.selectionModelProperty().get().selectedItemProperty().addListener(
//				(observable, oldValue, newValue) ->
//				{
//					String idByName = findIdByName(newValue);
//					if (idByName != null) Bootstrap.getCore().getProfileManager().setSelectedProfile(idByName);
//				});
	}

	private void initProfilePopupMenu()
	{
//		rootDialog.getChildren().remove(profilePopup);
//		profilePopup.setPopupContainer(rootDialog);
//		profilePopup.setPrefSize(200,400);
//		profilePopup.setOnClose(e -> rootDialog.setOverlayClose(true));
//		profilePopup.setSource(editProfileRegion);
//		editProfileRegion.setOnMouseClicked(event ->
//		{
//			rootDialog.setOverlayClose(false);
//			profilePopup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT/*, 40, 37*/);
//		});

	}

	@Override
	public void reload()
	{
		Logger.trace("reload");
	}

	@Override
	public void unload()
	{
//		profilePopup.close();
	}

	private class SimpleFileWatcher implements Runnable
	{
		private FileTime last;
		private Path sub;
		private Runnable runnable;

		public SimpleFileWatcher(Path sub, Runnable runnable)
		{
			this.sub = sub;
			this.runnable = runnable;
		}

		@Override
		public void run()
		{
			MinecraftDirectory minecraftLocation = Bootstrap.getCore().getProfileManager().selecting().getMinecraftLocation();

			try
			{
				FileTime lastModifiedTime = Files.getLastModifiedTime(minecraftLocation.getRoot().toPath().resolve(sub));
				if (last == null) last = lastModifiedTime;
				else
				{
					if (last.compareTo(lastModifiedTime) < 0)
						runnable.run();
				}
			}
			catch (IOException e) {}
		}
	}

	public void requestProfilePopup(MouseEvent event)
	{
		profilePopupController.reload();
	}
}
