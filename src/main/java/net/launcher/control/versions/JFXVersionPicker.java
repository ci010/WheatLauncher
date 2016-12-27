package net.launcher.control.versions;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import net.launcher.utils.CallbacksOption;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
		protected PopupControl getPopup()
		{
			if (popup == null)
				createPopup();
			return popup;
		}

		@Override
		protected void handleControlPropertyChanged(String p)
		{
			if ("VALUE".equals(p))
			{
				updateDisplayNode();
				if (content != null && this.popup.isShowing())
				{
					this.popup.hide();
				}
			}
			else
				super.handleControlPropertyChanged(p);
		}

		private Field field;
		private Method method;

		private void createPopup()
		{
			popup = new PopupControl()
			{
				@Override
				public Styleable getStyleableParent()
				{
					return Skin.this.getSkinnable();
				}

				{
					setSkin(new javafx.scene.control.Skin<Skinnable>()
					{
						@Override
						public Skinnable getSkinnable() { return Skin.this.getSkinnable(); }

						@Override
						public Node getNode() { return getPopupContent(); }

						@Override
						public void dispose() { }
					});
				}

			};
			popup.getStyleClass().add(COMBO_BOX_STYLE_CLASS);
			popup.setConsumeAutoHidingEvents(false);
			popup.setAutoHide(true);
			popup.setAutoFix(true);
			popup.setHideOnEscape(true);
			popup.setOnAutoHide(e ->
			{
				getBehavior().onAutoHide();
			});
//			popup.addEventHandler(MouseEvent.MOUSE_CLICKED, t ->
//			{
//				// RT-18529: We listen to mouse input that is received by the popup
//				// but that is not consumed, and assume that this is due to the mouse
//				// clicking outside of the node, but in areas such as the
//				// dropshadow.
//				getBehavior().onAutoHide();
//			});
			popup.addEventHandler(WindowEvent.WINDOW_HIDDEN, t ->
			{
				// Make sure the accessibility focus returns to the combo box
				// after the window closes.
				getSkinnable().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_NODE);
			});

			// Fix for RT-21207
			InvalidationListener layoutPosListener = o ->
			{
				try
				{
					if (field == null) field = ComboBoxPopupControl.class.getField("popupNeedsReconfiguring");
					field.set(this, true);
					if (method == null) method = ComboBoxPopupControl.class.getMethod("reconfigurePopup");
					method.invoke(this);
				}
				catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e)
				{
					e.printStackTrace();
				}
			};
			getSkinnable().layoutXProperty().addListener(layoutPosListener);
			getSkinnable().layoutYProperty().addListener(layoutPosListener);
			getSkinnable().widthProperty().addListener(layoutPosListener);
			getSkinnable().heightProperty().addListener(layoutPosListener);

			// RT-36966 - if skinnable's scene becomes null, ensure popup is closed
			getSkinnable().sceneProperty().addListener(o ->
			{
				if (((ObservableValue) o).getValue() == null) hide();
			});

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
