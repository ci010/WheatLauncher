package net.launcher.control.versions;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import net.launcher.assets.MinecraftVersion;

import java.util.function.Consumer;

/**
 * @author ci010
 */
public class MinecraftVersionPicker extends ComboBoxBase<MinecraftVersion>
{
	public MinecraftVersionPicker()
	{
		init();
	}

	private ListProperty<MinecraftVersion> dataList = new SimpleListProperty<>(FXCollections.observableArrayList());

	protected void init()
	{
		getStyleClass().add("version-picker");
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		Runnable updateFunction = getRequestUpdate();
		if (updateFunction != null) updateFunction.run();
		this.requestUpdate.addListener(observable ->
		{
			Runnable f = getRequestUpdate();
			if (f != null) f.run();
		});
	}

	public void setDataList(ObservableList<MinecraftVersion> dataList) {this.dataList.set(dataList);}

	public ObservableList<MinecraftVersion> getDataList() {return dataList.get();}

	public ListProperty<MinecraftVersion> dataListProperty() {return dataList;}

	private ObjectProperty<Runnable> requestUpdate = new SimpleObjectProperty<>();

	public Runnable getRequestUpdate()
	{
		return requestUpdate.get();
	}

	public ObjectProperty<Runnable> requestUpdateProperty()
	{
		return requestUpdate;
	}

	public void setRequestUpdate(Runnable requestUpdate)
	{
		this.requestUpdate.set(requestUpdate);
	}

	private ObjectProperty<Consumer<MinecraftVersion>> downloadRequest = new SimpleObjectProperty<>();

	public Consumer<MinecraftVersion> getDownloadRequest()
	{
		return downloadRequest.get();
	}

	public ObjectProperty<Consumer<MinecraftVersion>> downloadRequestProperty()
	{
		return downloadRequest;
	}

	public void setDownloadRequest(Consumer<MinecraftVersion> downloadRequest)
	{
		this.downloadRequest.set(downloadRequest);
	}

	@Override
	protected javafx.scene.control.Skin<?> createDefaultSkin()
	{
		return new MinecraftVersionPickerSkin(this);
	}

	static class Behavior extends ComboBoxBaseBehavior<MinecraftVersion>
	{
		public Behavior(MinecraftVersionPicker comboBoxBase) {super(comboBoxBase, COMBO_BOX_BASE_BINDINGS);}
	}
}
