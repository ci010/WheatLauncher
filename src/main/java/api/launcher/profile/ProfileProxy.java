package api.launcher.profile;

import api.launcher.Context;
import api.launcher.View;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import net.launcher.game.GameSettings;
import net.launcher.game.Language;
import net.launcher.game.ResourcePack;
import net.launcher.game.mods.ModContainer;
import net.launcher.model.Profile;
import net.launcher.utils.resource.Resource;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.WindowSize;

/**
 * @author ci010
 */
public interface ProfileProxy extends Profile, Context
{
	Profile getDelegate();

	void load(Profile profile);

	ReadOnlyStringProperty idProperty();

	ReadOnlyIntegerProperty minMemoryProperty();

	ReadOnlyIntegerProperty maxMemoryProperty();

	ReadOnlyStringProperty nameProperty();

	ReadOnlyObjectProperty<WindowSize> resolutionProperty();

	ReadOnlyObjectProperty<JavaEnvironment> javaLocationProperty();

	default GameSettings getGameSettings() {return getInstance(GameSettings.class);}

	default View<Language> getAllLanguages() {return getView(Language.class);}

	default View<Resource<ResourcePack>> getAllResourcePacksResources()
	{
		View resourceView = getView("resourcepacks", Resource.class);
		return (View<Resource<ResourcePack>>) resourceView;
	}

	default View<Resource<ModContainer<?>>> getAllModsResources()
	{
		View resourceView = getView("mods", Resource.class);
		return (View<Resource<ModContainer<?>>>) resourceView;
	}
}
