package net.launcher.control.profile.base;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.util.StringConverter;
import net.launcher.Bootstrap;
import net.launcher.control.ComboBoxSkinSimple;
import net.launcher.profile.LaunchProfile;

/**
 * @author ci010
 */
public class ProfileSelectorSimpleSkin extends ComboBoxSkinSimple<LaunchProfile>
{
	private Node content;

	public ProfileSelectorSimpleSkin(ComboBoxBase<LaunchProfile> comboBoxBase, ComboBoxBaseBehavior<LaunchProfile> behavior)
	{
		super(comboBoxBase, behavior);
	}

	@Override
	protected Node getPopupContent()
	{
		if (content == null)
			content = new ProfileSelectContent(((ProfileSelector) getSkinnable()), this);
		return content;
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
				return Bootstrap.getCore().getProfileManager().selecting();
			}
		};
	}
}
