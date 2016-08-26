package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.wheatlauncher.Core;
import net.wheatlauncher.utils.ListenerUtils;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.parsing.Versions;

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
			if (oldValue != null)
			{
				oldValue.versionProperty().unbind();
				memory.valueProperty().unbindBidirectional(oldValue.memoryProperty());
			}

			ListenerUtils.addListenerAndNotify(newValue.minecraftLocationProperty(), o -> {
				versions.itemsProperty().get().clear();
				versions.itemsProperty().get().addAll(Versions.getVersions(newValue.minecraftLocationProperty().getValue()));
			});

			newValue.versionProperty().bind(versions.valueProperty());
			memory.valueProperty().bindBidirectional(newValue.memoryProperty());
		});

		javaLocation.setItems(Core.INSTANCE.getJavaHistory());
		mcLocation.setItems(Core.INSTANCE.getMinecraftLocationHistory());

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

	@Override
	public void reload()
	{

	}
}
