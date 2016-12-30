package net.launcher.control.versions;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import net.launcher.utils.CallbacksOption;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;

/**
 * @author ci010
 */
public class MinecraftVersionPicker extends ComboBoxBase<RemoteVersion>
{
	public MinecraftVersionPicker()
	{
		init();
	}

	private ObjectProperty<RemoteVersionList> dataList = new SimpleObjectProperty<>();

	protected void init()
	{
		getStyleClass().add("jfx-date-picker");
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		onUpdate(CallbacksOption.empty());
	}

	public RemoteVersionList getDataList()
	{
		return dataList.get();
	}

	public ObjectProperty<RemoteVersionList> dataListProperty()
	{
		return dataList;
	}

	public void onUpdate(Callback<RemoteVersionList> callback)
	{
		MinecraftDownloader downloader = MinecraftDownloaderBuilder.buildDefault();
		downloader.fetchRemoteVersionList(new CallbackAdapter<RemoteVersionList>()
		{
			@Override
			public void done(RemoteVersionList result)
			{
				Platform.runLater(() ->
				{
					dataListProperty().set(result);
					callback.done(result);
				});
			}

			@Override
			public void failed(Throwable e)
			{
				Platform.runLater(() -> callback.failed(e));
			}

			@Override
			public void cancelled()
			{
				Platform.runLater(callback::cancelled);
			}
		});
	}

	@Override
	protected javafx.scene.control.Skin<?> createDefaultSkin()
	{
		return new MinecraftVersionPickerSkin(this);
	}

	static class Behavior extends ComboBoxBaseBehavior<RemoteVersion>
	{
		public Behavior(MinecraftVersionPicker comboBoxBase) {super(comboBoxBase, COMBO_BOX_BASE_BINDINGS);}
	}

}
