package net.launcher.control.profile.base;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.beans.property.ObjectProperty;
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
import net.launcher.profile.LaunchProfile;


/**
 * @author ci010
 */
public class ProfileSelector extends ComboBoxBase<LaunchProfile>
{
	private ObservableList<LaunchProfile> profiles = FXCollections.observableArrayList();

	protected javafx.scene.control.Skin<?> createDefaultSkin()
	{
		return new ProfileSelectorSimpleSkin(this, new Behav(this));
	}

	public ObservableList<LaunchProfile> getProfiles() {return profiles;}

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

	protected static class Behav extends ComboBoxBaseBehavior<LaunchProfile>
	{
		Behav(ComboBoxBase<LaunchProfile> comboBox)
		{
			super(comboBox, COMBO_BOX_BASE_BINDINGS);
		}
	}

	{
		getStyleClass().add("jfx-date-picker");
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
	}
//	private StyleableObjectProperty<Paint> selectedColor = new SimpleStyleableObjectProperty<Paint>();
}
