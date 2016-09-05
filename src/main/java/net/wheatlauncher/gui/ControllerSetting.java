package net.wheatlauncher.gui;

import com.jfoenix.controls.*;
import com.jfoenix.validation.ValidationFacade;
import de.jensd.fx.fontawesome.Icon;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import javax.annotation.PostConstruct;


/**
 * @author ci010
 */
public class ControllerSetting implements ReloadableController
{
	public ValidationFacade validProfile;
	public ValidationFacade validMcLocation;
	public ValidationFacade validVersion;

	public StackPane rightBody;
	public Label profileLabel;
	public JFXPopup profilePopup;
	public Label renameProfile;
	public Label newProfile;
	public Label deleteProfile;

	@FXML
	public Icon editProfile;
	public JFXRippler optionsRippler;

	private JFXSlider memory;
	private ValidationFacade validJAVALocation;

	@FXML
	private JFXComboBox<String> profile;

	@FXML
	private JFXComboBox<String> versions;

	@FXML
	private JFXComboBox<MinecraftDirectory> mcLocation;

	@FXML
	private JFXListView<Label> options;

	private JFXComboBox<JavaEnvironment> javaLocation;
	private ColorTransitionButton browsMC, browsJAVA;

	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private FlowPane root;

//	Color freshColor = new Color(Color.BROWN.getRed(), Color.BROWN.getRed(), Color.BROWN.getRed(), 1);
//	Background brown = new Background(
//			new BackgroundFill(new Color(Color.BROWN.getRed(), Color.BROWN.getGreen(), Color.BROWN.getBlue(), 0.7),
//					new CornerRadii(5),
//					new Insets(-2, -2, -2, -2)));

	@PostConstruct
	public void setup()
	{
		root.getChildren().remove(profilePopup);
		profilePopup.setPopupContainer(root);
		profilePopup.setSource(optionsRippler);
		optionsRippler.setOnMouseEntered(event -> {
			profilePopup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT,
					-150, -80);
		});
		optionsRippler.setOnMouseExited(event -> profilePopup.close());

//		profileButton.setOnAction(event -> profilePopup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT,
//				-12, 15));
//		Core.INSTANCE.selectLaunchProfile().addListener((observable, oldValue, newValue) -> {
//			if (oldValue != null)
//			{
//				oldValue.versionProperty().unbindBidirectional(versions.valueProperty());
////				oldValue.javaLocationProperty().unbindBidirectional(javaLocation.valueProperty());
//				oldValue.minecraftLocationProperty().unbindBidirectional(mcLocation.valueProperty());
////				memory.valueProperty().unbindBidirectional(oldValue.memoryProperty());
//			}
//
//			ListenerUtils.addListenerAndNotify(newValue.minecraftLocationProperty(), o -> {
//				Logger.trace("try refresh version");
//				versions.itemsProperty().get().clear();
//				versions.itemsProperty().get().addAll(Versions.getVersions(newValue.minecraftLocationProperty().getValue()));
//			});
//
//			mcLocation.valueProperty().bindBidirectional(newValue.minecraftLocationProperty());
////			javaLocation.valueProperty().bindBidirectional(newValue.javaLocationProperty());
//
//			versions.valueProperty().bindBidirectional(newValue.versionProperty());
////			memory.valueProperty().bindBidirectional(newValue.memoryProperty());
//		});

//		javaLocation.setItems(Core.INSTANCE.getJavaHistory());
//		mcLocation.setItems(Core.INSTANCE.getMinecraftLocationHistory());

//		browsMC.setOnMouseClicked(event -> {
//			DirectoryChooser chooser = new DirectoryChooser();
//			chooser.setInitialDirectory(new File("").getAbsoluteFile());
//			chooser.setTitle("Choose Minecraft root(.minecraft)");
//			Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
//			File choose = chooser.showDialog(stage);
//			if (choose == null)
//				return;
//			if (choose.isDirectory())
//			{
//				Logger.trace("choose " + choose);
//				MinecraftDirectory directory = new MinecraftDirectory(choose);
//				if (!mcLocation.getItems().contains(directory))
//					mcLocation.getItems().add(directory);
//				mcLocation.getSelectionModel().select(directory);
//			}
//		});

//		browsJAVA.setOnMouseClicked(event -> {
//			FileChooser chooser = new FileChooser();
//			FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Executable files " +
//					"(*.exe)", "*.exe");
//			chooser.getExtensionFilters().add(extensionFilter);
//			chooser.setInitialFileName("java.exe");
//			chooser.setInitialDirectory(new File(System.getProperty("java.home"), "bin"));
//			Stage stage = (Stage) flowContext.getRegisteredObject("Stage");
//			File choose = chooser.showOpenDialog(stage);
//			if (choose == null)
//				return;
//
//			if (choose.isFile() && choose.getName().equals("java.exe"))
//			{
//				JavaEnvironment javaEnvironment = new JavaEnvironment(choose);
//				if (!javaLocation.getItems().contains(javaEnvironment))
//					javaLocation.getItems().add(javaEnvironment);
//				javaLocation.getSelectionModel().select(javaEnvironment);
//			}
//		});
	}


//	private Background small = new Background(new BackgroundFill(new Color(Color.WHEAT.getRed(), Color.WHEAT.getGreen(),
//			Color.WHEAT.getBlue(), 0.7), new CornerRadii(5),
//			new Insets(0, 0, 0, 0)));

	private boolean first = true;

	@Override
	public void reload()
	{
		options.setExpanded(true);
		if (first)
		{
			first = false;
//			Callback<ListView<Label>, ListCell<Label>> cellFactory = options.getCellFactory();
//			Callback<ListView<Label>, ListCell<Label>> wrap = (view) -> {
//				ListCell<Label> call = cellFactory.call(view);
//				call.setStyle("-fx-selection-bar:red;");
////				call.backgroundProperty().addListener(observable -> {
////					if (call.backgroundProperty().get() != small)
////						call.setBackground(small);
////				});
//				return call;
//			};
//			options.setCellFactory(wrap);
		}
		if (versions.itemsProperty().get().isEmpty())
		{

		}
	}

	public Runnable close;

	public void exit()
	{
		close.run();
	}

	@Override
	public void unload()
	{
		options.setExpanded(false);
	}
}
