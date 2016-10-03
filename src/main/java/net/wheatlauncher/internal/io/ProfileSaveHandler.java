package net.wheatlauncher.internal.io;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import net.launcher.LaunchProfile;
import net.launcher.io.SaveHandler;
import net.launcher.io.SourceObject;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class ProfileSaveHandler extends SaveHandler<LaunchProfile>
{
	ProfileSaveHandler(File root)
	{
		super(root);
	}

	@Override
	protected void onWatch(LaunchProfile value, Map<SourceObject, Observable[]> map)
	{
		map.put(ProfileMangerIO.PROFILE.create("profiles", value.nameProperty().getValue()),
				new Observable[]{
						value.nameProperty(), value.versionProperty(), value.memoryProperty(),
						value.accountProperty(),
						value.accessToken(), value.authProperty(), value.javaProperty(),
						value.minecraftProperty(), value.resolutionProperty(), value.clientToken()
				});
		value.nameProperty().addListener((observable, oldValue, newValue) ->
		{
			if (oldValue != null) new File(getRoot(), oldValue + File.separator + "profile.json")
					.renameTo(new File(getRoot(), newValue + File.separator + "profile.json"));
		});// handle the rename file location;

		Map<SourceObject.Prototype, Property<?>[]> mapPrototypeWithValue = ProfileMangerIO.convert(value.getAllOption());
		mapPrototypeWithValue.forEach((prototype, properties) ->
		{
			Observable[] observables = new Observable[properties.length];
			System.arraycopy(properties, 0, observables, 0, properties.length);
			map.put(prototype.create(value.nameProperty().getValue()), observables);
		});
	}

	@Override
	protected void decorateMap(LaunchProfile value, SourceObject src, Map<String, String> map)
	{
		if (ProfileMangerIO.PROFILE.isTypeOf(src))
		{
			map.put("name", value.nameProperty().getValue());
			map.put("java", value.javaProperty().getValue().getJavaPath().getAbsolutePath());
			map.put("memory", value.memoryProperty().getValue().toString());
			map.put("minecraft", value.minecraftProperty().getValue().getAbsolutePath());
			map.put("version", value.versionProperty().getValue());
			map.put("auth", value.authProperty().getValue().id());
			map.put("account", value.accountProperty().getValue());
			map.put("windows-size", value.resolutionProperty().getValue().toString());
		}
		else map.putAll(ProfileMangerIO.mapWithPrototype(value.getAllOption()).get(src.getPrototype()).stream()
				.collect(Collectors.toMap(kMap -> kMap.getKey().getName(), vMap -> vMap.getKey().serialize
						(vMap.getValue().getValue()))));
	}
}
