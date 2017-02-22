package net.launcher.control;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.Icon;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.wheatlauncher.MainApplication;

/**
 * @author ci010
 */
public class CustomComboBox extends Application
{
	public static void main(String[] a)
	{
		launch(a);
	}

	class ComboBxTest<T> extends ComboBoxBase<T>
	{
		ObservableList<T> items = FXCollections.observableArrayList();

		@Override
		public ObservableList<Node> getChildren()
		{
			return super.getChildren();
		}

		class Ski extends SkinBase<ComboBxTest<T>>
		{
			HBox box = new HBox();
			JFXTextField textField = new JFXTextField();
			JFXRippler arrow;

			private PopupControl control;

			/**
			 * Constructor for all SkinBase instances.
			 *
			 * @param control The control for which this Skin should attach to.
			 */
			protected Ski(ComboBxTest<T> control)
			{
				super(control);
			}


			{
				Icon folder = new Icon("ANGLE_DOWN");
				StackPane pane = new StackPane(folder);
				pane.setPadding(new Insets(5));
				folder.setMaxWidth(Region.USE_PREF_SIZE);
				folder.setMaxHeight(Region.USE_PREF_SIZE);
				arrow = new JFXRippler(pane);
				arrow.setFocusTraversable(false);
				arrow.getStyleClass().setAll("arrow");
				arrow.setId("arrow");
				arrow.setMaxWidth(Region.USE_PREF_SIZE);
				arrow.setMaxHeight(Region.USE_PREF_SIZE);
				arrow.setMaskType(JFXRippler.RipplerMask.CIRCLE);
				arrow.setRipplerRecenter(true);
//					arrow.setMouseTransparent(true);
				arrow.setOnMouseReleased(e -> toggle());
				getSkinnable().sceneProperty().addListener(o ->
				{
					if (((ObservableValue) o).getValue() == null)
						hide();
				});
				box.getChildren().addAll(textField, arrow);
				box.setSpacing(2);


				ComboBxTest.this.getChildren().addAll(box);
			}

			private Node getPopupContent()
			{
				StackPane pane = new StackPane();
				Label test = new Label("test");
				pane.getChildren().addAll(test);
				pane.setPrefSize(200, 200);
				pane.setMinSize(200, 200);
				pane.setStyle("-fx-background-color:RED;");
				return pane;
			}

			private void toggle()
			{
				if (control == null)
				{
					this.control = new PopupControl()
					{
						@Override
						public Styleable getStyleableParent()
						{
							return Ski.this.getSkinnable();
						}

						{
							setSkin(new Skin<Skinnable>()
							{
								@Override
								public Skinnable getSkinnable() { return Ski.this.getSkinnable(); }

								@Override
								public Node getNode() { return getPopupContent(); }

								@Override
								public void dispose() { }
							});
						}
					};
//					control.setConsumeAutoHidingEvents(false);
//					control.setAutoHide(true);
//					control.setAutoFix(true);
					control.setHideOnEscape(true);
				}
				if (!control.isShowing())
				{
					ComboBxTest<T> skinnable = getSkinnable();
					Point2D point2D = arrow.localToScreen(arrow.getTranslateX(), arrow.getTranslateY());
					control.show(skinnable.getScene().getWindow(),
							point2D.getX(), point2D.getY() + arrow.getHeight());
				}
				else
				{
					control.hide();
				}
			}
		}

		@Override
		protected Ski createDefaultSkin()
		{
			return new Ski(this);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		StackPane pane = new StackPane();
		ComboBxTest<String> test = new ComboBxTest<>();
		test.items.addAll("test");
		pane.getChildren().addAll(test);
		Scene scene = new Scene(pane, 800, 500);//old 512 380  542, 380
		scene.getStylesheets().add(MainApplication.class.getResource("/assets/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/assets/css/common.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
