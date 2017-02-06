package net.launcher.control.versions;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import net.launcher.control.ComboBoxSkinSimple;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;

/**
 * @author ci010
 */
public class MinecraftVersionPickerSkin extends ComboBoxSkinSimple<RemoteVersion>
{
	protected MinecraftVersionPicker parent;
	private MinecraftVersionDisplayContent content;

	public MinecraftVersionPickerSkin(MinecraftVersionPicker parent)
	{
		super(parent, new MinecraftVersionPicker.Behavior(parent));
		this.parent = parent;
	}

	@Override
	protected Node getPopupContent()
	{
		if (content == null) content = defaultContent();
		return content;
	}

	protected MinecraftVersionDisplayContent defaultContent()
	{
		return new MinecraftVersionDisplayContent(this.parent);
	}

	@Override
	public void show()
	{
		super.show();
		TableView.TableViewSelectionModel<?> model = this.content.versionTable.getSelectionModel();
		if (!this.content.versionTable.getItems().isEmpty() && model.isEmpty())
			model.select(0);
	}

	@Override
	protected StringConverter<RemoteVersion> getConverter()
	{
		return new StringConverter<RemoteVersion>()
		{
			@Override
			public String toString(RemoteVersion object)
			{
				if (object != null)
					return object.getVersion();
				return "Unknown";
			}

			@Override
			public RemoteVersion fromString(String string)
			{
				return parent.dataListProperty().get().getVersions().get(string);
			}
		};
	}
}
