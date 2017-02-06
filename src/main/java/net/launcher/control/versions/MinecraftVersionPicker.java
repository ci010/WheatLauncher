package net.launcher.control.versions;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import net.launcher.utils.Tasks;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;

import java.util.function.Function;

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
		getStyleClass().add("version-picker");
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		onUpdate(Tasks.empty());
		this.updateFunction.addListener(o -> onUpdate(Tasks.empty()));
	}

	public RemoteVersionList getDataList()
	{
		return dataList.get();
	}

	public ReadOnlyObjectProperty<RemoteVersionList> dataListProperty()
	{
		return dataList;
	}

	private ObjectProperty<Function<Callback<RemoteVersionList>, Void>> updateFunction = new SimpleObjectProperty<>();

	public Function<Callback<RemoteVersionList>, Void> getUpdateFunction()
	{
		return updateFunction.get();
	}

	public ObjectProperty<Function<Callback<RemoteVersionList>, Void>> updateFunctionProperty()
	{
		return updateFunction;
	}

	public void setUpdateFunction(Function<Callback<RemoteVersionList>, Void> updateFunction)
	{
		this.updateFunction.set(updateFunction);
	}

	public void onUpdate(Callback<RemoteVersionList> callback)
	{
		if (updateFunction.get() != null)
			updateFunction.get().apply(new CallbackAdapter<RemoteVersionList>()
			{
				@Override
				public void done(RemoteVersionList result)
				{
					Platform.runLater(() ->
					{
						dataList.set(result);
						callback.done(result);
					});
				}

				@Override
				public void failed(Throwable e) {Platform.runLater(() -> callback.failed(e));}

				@Override
				public void cancelled() {Platform.runLater(callback::cancelled);}
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
