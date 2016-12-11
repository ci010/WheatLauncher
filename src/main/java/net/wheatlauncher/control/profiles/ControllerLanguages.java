package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import net.launcher.Bootstrap;
import net.launcher.LaunchCore;
import net.launcher.game.Language;
import net.launcher.profile.LaunchProfile;
import net.launcher.utils.Logger;
import net.wheatlauncher.control.WindowsManager;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.IOUtils;
import org.to2mbn.jmccc.version.Asset;
import org.to2mbn.jmccc.version.Version;
import org.to2mbn.jmccc.version.parsing.Versions;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ControllerLanguages
{
	@FXMLViewFlowContext
	public ViewFlowContext context;
	public StackPane root;
	public JFXToggleButton useUnicode;
	public JFXTreeTableView<LanguageCell> languageTable;
	public JFXTreeTableColumn<LanguageCell, String> id;
	public JFXTreeTableColumn<LanguageCell, String> name;
	public JFXTreeTableColumn<LanguageCell, String> region;
	public JFXTreeTableColumn<LanguageCell, String> bidi;

	static class LanguageCell extends RecursiveTreeObject<LanguageCell>
	{
		private StringProperty id = new SimpleStringProperty(), name = new SimpleStringProperty(),
				region = new SimpleStringProperty(), bidi = new SimpleStringProperty();

		public LanguageCell(Language language)
		{
			this.id.set(language.getId());
			this.name.set(language.getName());
			this.region.set(language.getRegion());
			this.bidi.set(Boolean.toString(language.isBidirectional()));
		}
	}

	private ObservableList<LanguageCell> languageLists;

	@PostConstruct
	public void init()
	{
		languageLists = FXCollections.observableArrayList();
		RecursiveTreeItem<LanguageCell> recursiveTreeItem = new RecursiveTreeItem<>(languageLists,
				RecursiveTreeObject::getChildren);
		try
		{
			updateLanguageList();
		}
		catch (IOException | NoSuchAlgorithmException e)
		{
			context.getRegisteredObject(WindowsManager.Page.class).displayError(e);
		}
		Logger.trace("");
		System.out.println(languageLists.isEmpty());
		root.disableProperty().bind(Bindings.createBooleanBinding(() ->
				languageLists.isEmpty(), languageLists));
		languageTable.setRoot(recursiveTreeItem);
	}

	private void updateLanguageList() throws IOException, NoSuchAlgorithmException
	{
		languageLists.clear();
		LaunchProfile p = LaunchCore.getCurrentProfile(Bootstrap.getCore());
		Version version = Versions.resolveVersion(p.getMinecraftLocation(), p.getVersion());
		if (version == null) return;
		Asset languageIndex = Versions.resolveAssets(p.getMinecraftLocation(), version).stream().filter(a -> a
				.getPath().equals("pack.mcmeta")).collect(Collectors.toList()).get(0);
		if (languageIndex.isValid(p.getMinecraftLocation()))
		{
			File asset = p.getMinecraftLocation().getAsset(languageIndex);
			JSONObject jsonObject = IOUtils.toJson(asset);
			Language[] languages = Language.deserializer().deserialize(jsonObject);
			languageLists.addAll(Arrays.stream(languages).map(LanguageCell::new).collect(Collectors.toList()));
		}
	}
}
