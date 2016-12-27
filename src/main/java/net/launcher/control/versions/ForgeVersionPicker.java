package net.launcher.control.versions;

import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.provider.forge.ForgeVersion;
import org.to2mbn.jmccc.mcdownloader.provider.forge.ForgeVersionList;

/**
 * @author ci010
 */
public class ForgeVersionPicker extends JFXVersionPicker<ForgeVersion, ForgeVersionList>
{
	@Override
	protected void onUpdate(Callback<ForgeVersionList> callback)
	{

	}

	@Override
	protected javafx.scene.control.Skin<?> createDefaultSkin()
	{
		return new ForgeSkin(this);
	}

	private static class ForgeSkin extends Skin<ForgeVersion, ForgeVersionList>
	{
		public ForgeSkin(JFXVersionPicker<ForgeVersion, ForgeVersionList> parent)
		{
			super(parent);
		}

		@Override
		protected Node defaultLabel() {return null;}

		@Override
		protected VersionDisplayContent<ForgeVersion, ForgeVersionList> defaultContent()
		{
			return null;
		}

		@Override
		protected StringConverter<ForgeVersion> getConverter()
		{
			return new StringConverter<ForgeVersion>()
			{
				@Override
				public String toString(ForgeVersion object) {return object.getForgeVersion();}

				@Override
				public ForgeVersion fromString(String string) {return parent.getDataList().get(string);}
			};
		}
	}

	private static class ForgeVersionObj extends RecursiveTreeObject<ForgeVersionObj>
	{

	}

	private static class ForgeVersionDisplayContent extends VersionDisplayContent<ForgeVersion, ForgeVersionList>
	{
		public ForgeVersionDisplayContent(JFXVersionPicker<ForgeVersion, ForgeVersionList> picker)
		{
			super(picker);
		}

		@Override
		protected JFXTableView<?> buildTable()
		{
			return null;
		}


		@Override
		protected void setupHeader()
		{
			Label fgVersion = new Label();
			Label mcVersion = new Label();

			BorderPane versionContainer = new BorderPane();
			versionContainer.setLeft(fgVersion);
			versionContainer.setRight(mcVersion);


		}

		@Override
		protected void bindData()
		{

		}

		@Override
		protected void onConfirm()
		{

		}
	}
}
