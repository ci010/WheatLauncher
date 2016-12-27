package net.launcher.control.versions;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.launcher.utils.CallbacksOption;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

/**
 * @author ci010
 */
public abstract class JFXVersionPicker<VersionData, VersionDataList> extends ComboBoxBase<VersionData>
{
	public JFXVersionPicker()
	{
		init();
	}

	protected abstract void onUpdate(Callback<VersionDataList> callback);

	private ObjectProperty<VersionDataList> dataList = new SimpleObjectProperty<>();
	private ListProperty<VersionData> dataObservableList = new SimpleListProperty<>();

	protected void init()
	{
		getStyleClass().add("jfx-date-picker");
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		this.onUpdate(CallbacksOption.empty());
	}

	public VersionDataList getDataList()
	{
		return dataList.get();
	}

	public ObjectProperty<VersionDataList> dataListProperty()
	{
		return dataList;
	}

	@Override
	protected abstract javafx.scene.control.Skin<?> createDefaultSkin();

	public static abstract class VersionDisplayContent<T, L> extends Region
	{
		protected JFXVersionPicker<T, L> picker;

		protected StackPane root;
		protected VBox container;
		protected VBox header;
		protected VBox content;

		private JFXSpinner spinner;

		//contents
		protected JFXTableView<?> versionTable;
		protected JFXRippler confirm, refresh;

		public VersionDisplayContent(JFXVersionPicker<T, L> picker)
		{
			this.picker = picker;

			this.root = new StackPane();
			this.root.setOnMouseClicked(Event::consume);
			this.root.setAlignment(Pos.CENTER);
			this.getChildren().add(root);

			this.container = new VBox();
			this.header = new VBox();
			this.content = new VBox();
			this.content.setAlignment(Pos.CENTER);

			this.container.getChildren().add(header);
			this.container.getChildren().add(content);
			this.container.setAlignment(Pos.CENTER);
			this.container.setStyle("-fx-padding:5; -fx-background-color:#009688");

			this.spinner = new JFXSpinner();

			this.root.getChildren().add(container);

			header.getStyleClass().add("jfx-layout-heading");
			header.getStyleClass().add("title");
			header.setStyle("-fx-padding:10");
//			header.setBackground(new Background(new BackgroundFill(Color.valueOf("#009688"), CornerRadii.EMPTY, Insets.EMPTY)));

			JFXDepthManager.setDepth(header, 3);
			setupHeader();
			setupContent();
			bindData();

			refresh.setOnMouseClicked(event -> refresh());
			confirm.setOnMouseClicked(event -> onConfirm());

//			refresh();
		}

		private void refresh()
		{
			if (!root.getChildren().contains(spinner))
				root.getChildren().add(spinner);
			this.container.setDisable(true);
			this.picker.onUpdate(CallbacksOption.whateverCallback(
					() ->
					{
						root.getChildren().remove(spinner);
						container.setDisable(false);
					}));
		}

		protected void setupContent()
		{
			this.versionTable = buildTable();
			//setup refresh
			this.refresh = new JFXRippler(new Icon("REFRESH", "2em", ";", "icon"));
			//setup onConfirm
			this.confirm = new JFXRippler(new Icon("CHECK", "2em", ";", "icon"));

			BorderPane btnContainer = new BorderPane();
			btnContainer.setLeft(refresh);
			btnContainer.setRight(confirm);
//			btnContainer.setBackground(new Background(new BackgroundFill(Color.valueOf("#009688"), CornerRadii.EMPTY, Insets.EMPTY)));

			this.content.getChildren().setAll(this.versionTable, btnContainer);
		}

		protected abstract JFXTableView<?> buildTable();

		protected abstract void setupHeader();

		protected abstract void bindData();

		protected abstract void onConfirm();
	}

	public static abstract class Skin<T, L> extends ComboBoxPopupControl<T>
	{
		protected JFXVersionPicker<T, L> parent;

		private Node displayNode;
		private VersionDisplayContent<T, L> content;
		private JFXTextField textField = null;

		public Skin(JFXVersionPicker<T, L> parent)
		{
			super(parent, new Behavior<>(parent));
			this.parent = parent;
			this.textField = new JFXTextField();
			this.textField.setEditable(false);
			this.textField.setText("Unknown");

			registerChangeListener(parent.valueProperty(), "VALUE");
		}


		@Override
		protected Node getPopupContent()
		{
			if (content == null) content = defaultContent();
			return content;
		}

		protected abstract Node defaultLabel();

		protected abstract VersionDisplayContent<T, L> defaultContent();

		@Override
		protected TextField getEditor()
		{
			StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		/*
		 *  added to fix android issue as the stack trace on android is
		 *  not the same as desktop
		 */
			if (caller.getClassName().equals(this.getClass().getName()))
				caller = Thread.currentThread().getStackTrace()[3];
			boolean parentListenerCall = caller.getMethodName().contains("lambda") && caller.getClassName().equals("com.sun.javafx.scene.control.skin.ComboBoxPopupControl");
			if (parentListenerCall) return null;
			return textField;
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
		protected void handleControlPropertyChanged(String p)
		{
			if ("VALUE".equals(p))
			{
				updateDisplayNode();
				if (content != null && this.popup.isShowing())
					this.popup.hide();
			}
			else
				super.handleControlPropertyChanged(p);
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
//			displayNode.setDisable(parent.isEditable());
			return displayNode;
		}


	}

	static class Behavior<T, L> extends ComboBoxBaseBehavior<T>
	{
		public Behavior(JFXVersionPicker<T, L> comboBoxBase) {super(comboBoxBase, COMBO_BOX_BASE_BINDINGS);}
	}

}
