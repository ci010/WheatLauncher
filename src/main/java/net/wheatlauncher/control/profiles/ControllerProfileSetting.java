package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.*;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.launcher.Bootstrap;
import net.launcher.profile.LaunchProfile;
import net.launcher.utils.Logger;
import net.wheatlauncher.control.utils.FXMLInnerController;
import net.wheatlauncher.control.utils.ReloadableController;
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
import java.util.stream.Collectors;


/**
 * @author ci010
 */
@FXMLController("/fxml/profiles/ProfileSetting.fxml")
public class ControllerProfileSetting implements ReloadableController
{
	public Tab mcTab;

	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	/*Profile*/
	public Label profileLabel;
//	public ValidationFacade validProfile;

	public JFXPopup profilePopup;
	@FXMLInnerController
	public ControllerSettingProfile profilePopupController;

	public JFXRippler editProfileRegion;
	public JFXComboBox<String> profile;

	/*Version*/
	public JFXComboBox<String> versions;
	public ValidationFacade validVersion;

	/*Sub-settings*/
	public JFXTabPane optionsTab;

	@FXMLInnerController
	public ControllerCommonSetting commonSettingController;
	public VBox commonSetting;

	@FXMLInnerController
	public ControllerGameSetting gameSettingController;
	public StackPane gameSetting;

	@FXMLInnerController
	public ControllerLanguages languageSettingController;
	public StackPane languageSetting;

	@FXMLInnerController
	public ControllerResourcePackView resourcePackSettingController;
	public StackPane resourcePackSetting;

	@FXMLInnerController
	public ControllerModView modSettingController;
	public StackPane modSetting;

	/*root*/
	@FXML
	private FlowPane root;

	public JFXDialog rootDialog;

	@PostConstruct
	public void setup()
	{
		Logger.trace("init");
		rootDialog.setOverlayClose(true);
		initProfilePopupMenu();
		initProfile();
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
		versionList = FXCollections.observableArrayList(Versions.getVersions(Bootstrap.getCore().getProfileManager().selecting()
				.getMinecraftLocation()));
		versions.itemsProperty().bind(Bindings.createObjectBinding(() ->
				{
					updateVersionList();
					return versionList;
				},
				Bootstrap.getCore().getProfileManager().selectedProfileProperty()));
		versions.valueProperty().bind(Bindings.createStringBinding(() ->
						Bootstrap.getCore().getProfileManager().selecting().getVersion(),
				Bootstrap.getCore().getProfileManager().selectedProfileProperty()));
		versions.getJFXEditor().textProperty().bind(
				Bindings.createStringBinding(() -> Bootstrap.getCore().getProfileManager().selecting().getVersion(), Bootstrap.getCore()
						.getProfileManager().selectedProfileProperty()));
	}

	private String findIdByName(String name)
	{
		for (Map.Entry<String, LaunchProfile> entry : Bootstrap.getCore().getProfileManager().getAllProfiles().entrySet())
			if (entry.getValue().getDisplayName().equals(name)) return entry.getKey();
		return null;
	}

	private void initProfile()
	{
		ObjectBinding<ObservableList<String>> itemsBinding = Bindings.createObjectBinding(() ->
						FXCollections.observableArrayList(Bootstrap.getCore().getProfileManager().getAllProfiles()
								.values().stream().map(LaunchProfile::getDisplayName)
								.collect(Collectors.toList())),
				Bootstrap.getCore().getProfileManager().getAllProfiles());
		profile.itemsProperty().bind(itemsBinding);
		InvalidationListener listener = (observable) ->
		{
			profile.valueProperty().bind(Bindings.createStringBinding(() ->
					{
						Platform.runLater(itemsBinding::invalidate);
						return Bootstrap.getCore().getProfileManager().selecting().getDisplayName();
					},
					Bootstrap.getCore().getProfileManager().selecting().displayNameProperty()));
		};
		Bootstrap.getCore().getProfileManager().selectedProfileProperty().addListener(listener);
		listener.invalidated(null);

		profile.selectionModelProperty().get().selectedItemProperty().addListener(
				(observable, oldValue, newValue) ->
				{
					String idByName = findIdByName(newValue);
					if (idByName != null) Bootstrap.getCore().getProfileManager().setSelectedProfile(idByName);
				});
	}

	private void initProfilePopupMenu()
	{
		rootDialog.getChildren().remove(profilePopup);
		profilePopup.setPopupContainer(rootDialog);

		profilePopup.setOnClose(e -> rootDialog.setOverlayClose(true));
		profilePopup.setSource(editProfileRegion);
		editProfileRegion.setOnMouseClicked(event ->
		{
			rootDialog.setOverlayClose(false);
			profilePopup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 40, 37);
		});

	}

	@Override
	public void reload()
	{
		Logger.trace("reload");
	}

	@Override
	public void unload()
	{
		profilePopup.close();
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
