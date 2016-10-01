package net.wheatlauncher.internal.io;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyListProperty;
import net.launcher.ILaunchProfile;
import net.launcher.LaunchProfileManager;
import net.launcher.io.SaveHandler;
import net.launcher.io.SourceObject;
import org.to2mbn.jmccc.internal.org.json.JSONArray;

import java.io.File;
import java.util.Map;

/**
 * @author ci010
 */
class ProfileMangerSaveHandler extends SaveHandler<LaunchProfileManager>
{
	ProfileMangerSaveHandler(File root)
	{
		super(root);
	}

	@Override
	protected void onWatch(LaunchProfileManager value, Map<SourceObject, Observable[]> map)
	{
		map.put(ProfileMangerIO.CONFIG.create(),
				new Observable[]{value.selectedProfileProperty(), value.allProfilesProperty()});
	}

	@Override
	public void decorateMap(LaunchProfileManager value, SourceObject src, Map<String, String> map)
	{
		map.put("selecting", value.selectedProfileProperty().getValue().nameProperty().getValue());
		JSONArray arr = new JSONArray();
		ReadOnlyListProperty<ILaunchProfile> profiles = value.allProfilesProperty();
		for (ILaunchProfile profile : profiles)
			arr.put(profile.nameProperty().getValue());
		map.put("profiles", arr.toString());
	}
}
