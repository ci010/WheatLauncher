package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.wheatlauncher.Core;
import net.wheatlauncher.launch.LaunchProfile;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author ci010
 */
@FXMLController("/fxml/Setting.fxml")
public class ControllerSetting implements ReloadableController
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private JFXSlider memory;

	@FXML
	private ValidationFacade validJAVALocation;
	@FXML
	private JFXComboBox<String> versions;
	@FXML
	private JFXComboBox<MinecraftDirectory> mcLocation;
	@FXML
	private JFXComboBox<JavaEnvironment> javaLocation;
	@FXML
	private ColorTransitionButton browsMC, browsJAVA;

	@PostConstruct
	public void init()
	{
		Core.INSTANCE.selectLaunchProfile().addListener((observable, oldValue, newValue) -> {
			versions.itemsProperty().bind(newValue.versionList());
			newValue.versionProperty().bind(versions.valueProperty());

			setupMcLocation(newValue);
			setupJavaLocation(newValue);

			memory.valueProperty().bindBidirectional(newValue.memoryProperty());
		});

		browsMC.setOnMouseClicked(event -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(new File("").getAbsoluteFile());
			chooser.setTitle("Choose Minecraft root(.minecraft)");
			Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
			File choose = chooser.showDialog(stage);
			if (choose == null)
				return;
			if (choose.isDirectory())
			{
				MinecraftDirectory directory = new MinecraftDirectory(choose);
				if (!mcLocation.getItems().contains(directory))
					mcLocation.getItems().add(directory);
				mcLocation.getSelectionModel().select(directory);
			}
		});

		browsJAVA.setOnMouseClicked(event -> {
			FileChooser chooser = new FileChooser();
			FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Executable files " +
					"(*.exe)", "*.exe");
			chooser.getExtensionFilters().add(extensionFilter);
			chooser.setInitialFileName("java.exe");
			chooser.setInitialDirectory(new File(System.getProperty("java.home"), "bin"));
			Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
			File choose = chooser.showOpenDialog(stage);
			if (choose == null)
				return;

			if (choose.isFile() && choose.getName().equals("java.exe"))
			{
				JavaEnvironment javaEnvironment = new JavaEnvironment(choose);
				if (!javaLocation.getItems().contains(javaEnvironment))
					javaLocation.getItems().add(javaEnvironment);
				javaLocation.getSelectionModel().select(javaEnvironment);
			}
		});
	}

	private void setupJavaLocation(LaunchProfile current)
	{
		javaLocation.getItems().add(current.javaLocationProperty().getValue());
		javaLocation.getSelectionModel().select(current.javaLocationProperty().getValue());
		current.javaLocationProperty().bind(javaLocation.selectionModelProperty().get().selectedItemProperty());

	}

	private void setupMcLocation(LaunchProfile current)
	{
		ListProperty<MinecraftDirectory> history = Core.INSTANCE.getMinecraftLocationHistory();
		mcLocation.itemsProperty().bindBidirectional(history);

//		validMcLocation.setValidators(new ValidatorBase()
//		{
//			{
//				this.message.set("Please select a valid minecraft location!");
//			}
//			@Override
//			protected void eval()
//			{
//				this.hasErrors.set(true);
//			}
//		});
//
//		mcLocation.focusedProperty().addListener(new InvalidationListener()
//		{
//			@Override
//			public void invalidated(Observable observable)
//			{
//				ValidationFacade.validate(mcLocation);
//			}
//		});

		mcLocation.getItems().add(current.minecraftLocationProperty().getValue());
		mcLocation.getSelectionModel().select(current.minecraftLocationProperty().getValue());
		current.minecraftLocationProperty().bind(mcLocation.selectionModelProperty().get().selectedItemProperty());
	}

	@Override
	public void reload()
	{

	}

	@Override
	public void onProfileChange(LaunchProfile profile)
	{
		setupJavaLocation(profile);
		setupMcLocation(profile);
	}
}
