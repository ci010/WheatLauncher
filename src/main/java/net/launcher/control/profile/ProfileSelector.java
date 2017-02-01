package net.launcher.control.profile;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBoxBase;
import net.launcher.profile.LaunchProfile;

/**
 * @author ci010
 */
public class ProfileSelector extends ComboBoxBase<LaunchProfile>
{
	private ObservableList<LaunchProfile> profiles;

	protected javafx.scene.control.Skin<?> createDefaultSkin()
	{
		return new ProfileSelectorSkin(this, new Behav(this));
	}

	static class Behav extends ComboBoxBaseBehavior<LaunchProfile>
	{
		Behav(ComboBoxBase<LaunchProfile> comboBox)
		{
			super(comboBox, COMBO_BOX_BASE_BINDINGS);
		}
	}
}
