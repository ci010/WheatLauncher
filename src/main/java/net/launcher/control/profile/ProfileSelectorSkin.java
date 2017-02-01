package net.launcher.control.profile;

import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import net.launcher.Bootstrap;
import net.launcher.profile.LaunchProfile;

/**
 * @author ci010
 */
public class ProfileSelectorSkin extends ComboBoxPopupControl<LaunchProfile>
{
	private JFXTextField textField;
	private Node displayNode;

	public ProfileSelectorSkin(ComboBoxBase<LaunchProfile> comboBoxBase, ComboBoxBaseBehavior<LaunchProfile> behavior)
	{
		super(comboBoxBase, behavior);
	}

	@Override
	protected Node getPopupContent()
	{
		return null;
	}

	@Override
	protected TextField getEditor()
	{
		if (textField == null) textField = new JFXTextField();
		return textField;
	}

	@Override
	protected StringConverter<LaunchProfile> getConverter()
	{
		return new StringConverter<LaunchProfile>()
		{
			@Override
			public String toString(LaunchProfile object)
			{
				return object.getDisplayName();
			}

			@Override
			public LaunchProfile fromString(String string)
			{
				return Bootstrap.getCore().getProfileManager().selecting();
			}
		};
	}

	@Override
	public Node getDisplayNode()
	{
		if (displayNode == null)
		{
			displayNode = getEditableInputNode();
			displayNode.getStyleClass().add("date-picker-display-node");
			updateDisplayNode();
		}
		return displayNode;
	}
}
