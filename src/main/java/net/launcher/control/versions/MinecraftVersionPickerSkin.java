package net.launcher.control.versions;

import javafx.scene.Node;
import javafx.util.StringConverter;
import net.launcher.control.ComboBoxSkinSimple;
import net.launcher.version.MinecraftVersion;

/**
 * @author ci010
 */
public class MinecraftVersionPickerSkin extends ComboBoxSkinSimple<MinecraftVersion>
{
	protected MinecraftVersionPicker parent;
	private MinecraftVersionDisplayContent content;

	public MinecraftVersionPickerSkin(MinecraftVersionPicker parent)
	{
		super(parent, new MinecraftVersionPicker.Behavior(parent));
		this.parent = parent;
	}

	protected void createEditor()
	{
		super.createEditor();
		textField.setPromptText("Version");
	}

	@Override
	protected Node getPopupContent()
	{
		if (content == null) content = new MinecraftVersionDisplayContent(this.parent);
		return content;
	}

	@Override
	public void show()
	{
		super.show();
		content.onShow();
	}

	@Override
	protected StringConverter<MinecraftVersion> getConverter()
	{
		return new StringConverter<MinecraftVersion>()
		{
			@Override
			public String toString(MinecraftVersion object)
			{
				if (object != null)
					return object.getVersionID();
				return "Unknown";
			}

			@Override
			public MinecraftVersion fromString(String string)
			{
				//this should not happen
				return parent.dataListProperty().get().get(0);
			}
		};
	}
}
