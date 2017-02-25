package net.launcher.control.profile.base;

import api.launcher.LaunchProfile;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Callback;


/**
 * @author ci010
 */
public class ProfileSelector extends ComboBoxBase<LaunchProfile>
{
	private ListProperty<LaunchProfile> profiles = new SimpleListProperty<>(FXCollections.observableArrayList());

	protected javafx.scene.control.Skin<?> createDefaultSkin()
	{
		return new ProfileSelectorSimpleSkin(this, new Behav(this));
	}

	public ListProperty<LaunchProfile> profilesProperty() {return profiles;}

	public ObservableList<LaunchProfile> getProfiles()
	{
		return profiles.get();
	}

	public void setProfiles(ObservableList<LaunchProfile> profiles)
	{
		this.profiles.set(profiles);
	}

	private ObjectProperty<Callback<String, LaunchProfile>> profileFactory = new SimpleObjectProperty<>();

	private ObjectProperty<Callback<LaunchProfile, Void>> removeCallback = new SimpleObjectProperty<>();

	public Callback<LaunchProfile, Void> getRemoveCallback()
	{
		return removeCallback.get();
	}

	public ObjectProperty<Callback<LaunchProfile, Void>> removeCallbackProperty()
	{
		return removeCallback;
	}

	public void setRemoveCallback(Callback<LaunchProfile, Void> removeCallback)
	{
		this.removeCallback.set(removeCallback);
	}

	public Callback<String, LaunchProfile> getProfileFactory()
	{
		return profileFactory.get();
	}

	public ObjectProperty<Callback<String, LaunchProfile>> profileFactoryProperty()
	{
		return profileFactory;
	}

	public void setProfileFactory(Callback<String, LaunchProfile> profileFactory)
	{
		this.profileFactory.set(profileFactory);
	}

	static class Behav extends ComboBoxBaseBehavior<LaunchProfile>
	{
		Behav(ComboBoxBase<LaunchProfile> comboBox)
		{
			super(comboBox, COMBO_BOX_BASE_BINDINGS);
		}
	}

	{
		getStyleClass().add("profile-picker");
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

	}

}
