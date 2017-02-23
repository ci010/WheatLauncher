package net.launcher.control.profile.base;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.util.StringConverter;
import net.launcher.api.ARML;
import net.launcher.control.ComboBoxSkinSimple;
import net.launcher.profile.LaunchProfile;

/**
 * @author ci010
 */
public class ProfileSelectorComplexSkin extends ComboBoxSkinSimple<LaunchProfile>
{
	private ProfileSelectorTableContent content;
	private InvalidationListener listener = observable -> updateDisplayNode();

	public ProfileSelectorComplexSkin(ComboBoxBase<LaunchProfile> comboBoxBase, ComboBoxBaseBehavior<LaunchProfile> behavior)
	{
		super(comboBoxBase, behavior);
		LaunchProfile value = this.getSkinnable().getValue();
		if (value != null)
			value.displayNameProperty().addListener(listener);
		this.getSkinnable().valueProperty().addListener((observable, oldValue, newValue) ->
		{
			if (oldValue != null)
				oldValue.displayNameProperty().removeListener(listener);
			if (newValue != null)
				newValue.displayNameProperty().addListener(listener);
		});
	}

	protected void createEditor()
	{
		super.createEditor();
		textField.setPromptText("Profile");
	}

	@Override
	protected Node getPopupContent()
	{
		if (content == null)
			content = new ProfileSelectorTableContent((ProfileSelector) getSkinnable());
		return content;
	}

	@Override
	public void show()
	{
		super.show();
		content.onShow();
	}

	@Override
	protected StringConverter<LaunchProfile> getConverter()
	{
		return new StringConverter<LaunchProfile>()
		{
			@Override
			public String toString(LaunchProfile object)
			{
				if (object != null)
					return object.getDisplayName();
				return "";
			}

			@Override
			public LaunchProfile fromString(String string)
			{
				return ARML.core().getProfileManager().selecting();
			}
		};
	}
}
