package net.launcher.control.profile.base;

import api.launcher.LaunchProfile;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.List;

/**
 * @author ci010
 */
public class ProfileSelectContent extends StackPane
{
	private ProfileSelector selector;
	private ProfileSelectorSimpleSkin selectorSkin;

	private VBox profiles;

	public ProfileSelectContent(ProfileSelector selector, ProfileSelectorSimpleSkin skin)
	{
		this.selectorSkin = skin;
		this.selector = selector;
		this.profiles = new VBox();
		this.setStyle("-fx-background-color:WHITE; -fx-padding:5");
		this.prefWidth(100);
		this.prefHeight(100);
		for (LaunchProfile launchProfile : this.selector.getProfiles())
			profiles.getChildren().add(new ProfilePane(launchProfile));
		profiles.getChildren().add(new ProfilePane());
		this.setOnMouseClicked(Event::consume);
		this.selector.profilesProperty().addListener((ListChangeListener<LaunchProfile>) c ->
		{
			while (c.next())
			{
				List<? extends LaunchProfile> ls = c.getAddedSubList();
				for (LaunchProfile l : ls)
				{
					ProfilePane profilePane = new ProfilePane(l);
					profiles.getChildren().add(profilePane);
				}
				for (LaunchProfile profile : c.getRemoved())
				{
					ProfilePane removed = null;
					for (Node profilePane : profiles.getChildren())
						if (((ProfilePane) profilePane).getProfile().equals(profile))
						{
							removed = (ProfilePane) profilePane;
							break;
						}
					if (removed != null)
					{
						profiles.getChildren().remove(removed);
						Callback<LaunchProfile, Void> removeCallback = this.selector.getRemoveCallback();
						if (removeCallback != null)
							removeCallback.call(removed.getProfile());
					}
				}
			}
		});
		this.getChildren().add(profiles);
		selector.valueProperty().addListener(observable ->
		{

		});
//		profiles.prefWidthProperty().bind(selector.widthProperty());
	}

	class ProfilePane extends StackPane
	{
		private ObjectProperty<LaunchProfile> profile = new SimpleObjectProperty<>();
		private Node root;

		ProfilePane() {init();}

		ProfilePane(LaunchProfile profile) {this.setProfile(profile); init();}

		protected void init()
		{
			this.prefWidthProperty().bind(ProfileSelectContent.this.selector.widthProperty());
			this.setMaxWidth(StackPane.USE_PREF_SIZE);

			if (getProfile() == null)
				getChildren().setAll(root = new NewNode());
			else setupToContentPane();

			this.profile.addListener(observable ->
			{
				if (profile.get() != null) setupToContentPane();
			});
			this.setOnMouseClicked(Event::consume);
			this.setOnMousePressed(Event::consume);
			this.setOnMouseReleased(Event::consume);
		}

		void setupToContentPane()
		{
			if (root != null && getChildren().contains(root))
				this.getChildren().remove(root);
			root = new ContentNode();
			this.getChildren().add(root);
		}

		boolean isNewPane() {return root instanceof NewNode;}

		public LaunchProfile getProfile()
		{
			return profile.get();
		}

		public ObjectProperty<LaunchProfile> profileProperty()
		{
			return profile;
		}

		public void setProfile(LaunchProfile profile)
		{
			this.profile.set(profile);
		}

		class NewNode extends StackPane implements InvalidationListener
		{
			JFXRippler prepareInterface;
			BorderPane editingInterface;

			void toEditingInterface()
			{
				prepareInterface.setDisable(true);

				if (editingInterface == null)
				{
					editingInterface = new BorderPane();

					JFXButton cancel = new JFXButton();
					cancel.setGraphic(new Icon("CLOSE"));
					JFXButton ok = new JFXButton();
					ok.setGraphic(new Icon("CHECK"));

					cancel.setOnAction(event -> toPrepareInterface());
					ok.setOnAction(event -> finishEditing());
					editingInterface.addEventHandler(KeyEvent.KEY_RELEASED, event ->
					{
						if (event.getCode() == KeyCode.ENTER) finishEditing();
					});

					JFXTextField text = new JFXTextField();
					text.requestFocus();
					editingInterface.setLeft(cancel);
					editingInterface.setRight(ok);
					editingInterface.setCenter(text);
					this.getChildren().add(editingInterface);
				}
				editingInterface.setVisible(true);
			}

			void finishEditing()
			{
				JFXTextField field = (JFXTextField) editingInterface.getCenter();
				String name = field.getText();
				LaunchProfile profile;
				Callback<String, LaunchProfile> profileFactory = selector.getProfileFactory();
				if (profileFactory != null)
					profile = profileFactory.call(name);
				else
				{
					profile = new LaunchProfile();
					profile.setDisplayName(name);
				}

				selector.showingProperty().removeListener(this);//remove this show listener
				ProfilePane.this.setProfile(profile);//submit result
			}

			void toPrepareInterface()
			{
				System.out.println("to prepare interface");
				if (editingInterface != null)
				{
					editingInterface.setVisible(false);
					prepareInterface.setDisable(false);
				}
			}

			NewNode()
			{
				HBox box = new HBox();
				box.setAlignment(Pos.CENTER);
				box.setSpacing(10);

				prepareInterface = new JFXRippler(box);

				Icon plus = new Icon("PLUS");
				box.getChildren().addAll(new Label("NEW PROFILE"), plus);
				prepareInterface.setStyle("-fx-padding:10 0");
				prepareInterface.setOnMouseReleased(event -> toEditingInterface());
				this.getChildren().add(prepareInterface);
			}

			@Override
			public void invalidated(Observable observable)
			{
				if (!selector.isShowing()) toPrepareInterface();
			}
		}

		class ContentNode extends BorderPane implements InvalidationListener
		{
			private boolean editing = false;

			ContentNode()
			{
				JFXButton name = new JFXButton();
				name.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (getProfile() != null)
						return getProfile().getDisplayName();
					return "";
				}, profileProperty()));
				name.setStyle("-fx-font-weight:bold;");
				name.setAlignment(Pos.CENTER);
				name.setMaxWidth(Double.MAX_VALUE);
				name.setMaxHeight(Double.MAX_VALUE);
				this.setCenter(name);

				JFXButton edit = new JFXButton();
				edit.setStyle("-fx-background-color:transparent;");
				edit.setGraphic(new Icon("PENCIL_SQUARE"));

				edit.setOnAction(event ->
				{
					if (!editing)
					{
						editing = true;
						((Icon) edit.getGraphic()).icon(AwesomeIcon.valueOf("CLOSE"));
					}
					else
					{
						//delete
					}
				});

				JFXButton ok = new JFXButton();
				ok.setStyle("-fx-background-color:transparent;");
				ok.setGraphic(new Icon("CHECK"));

				ok.setOnAction(event ->
				{
					if (editing)
					{
						editing = false;
						((Icon) edit.getGraphic()).icon(AwesomeIcon.valueOf("PENCIL_SQUARE"));
					}
					else
					{
						//select and hide
					}
				});
				this.setRight(ok);
				this.setLeft(edit);

				selector.showingProperty().addListener(this);
			}

			void onRemoved()
			{
				selector.showingProperty().removeListener(this);
			}

			@Override
			public void invalidated(Observable observable)
			{
				if (!selector.isShowing() && editing)
				{
					editing = false;
					Icon graphic = (Icon) ((JFXButton) this.getRight()).getGraphic();
					graphic.icon(AwesomeIcon.valueOf("PENCIL_SQUARE"));
				}
			}
		}
	}

}
