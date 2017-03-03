package net.launcher.control.modview;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.launcher.services.curseforge.CurseForgeProject;

import java.text.DateFormat;

/**
 * @author ci010
 */
public class CurseForgeProjectCard extends StackPane
{
	private ObjectProperty<CurseForgeProject> project = new SimpleObjectProperty<>();

	public CurseForgeProjectCard() {}

	public CurseForgeProject getProject()
	{
		return project.get();
	}

	public ObjectProperty<CurseForgeProject> projectProperty()
	{
		return project;
	}

	public void setProject(CurseForgeProject project)
	{
		this.project.set(project);
	}

	private BorderPane header;

	private static int i = 0;

	public CurseForgeProjectCard(CurseForgeProject project)
	{
		this.project.set(project);
		this.setMaxWidth(300);
		VBox content = new VBox();
		this.header = new BorderPane();

		String headerColor = getDefaultColor(i++ % 12);
		header.setStyle("-fx-background-radius: 5 5 0 0;-fx-background-color: " +
				headerColor);
		Region placeHolder = new Region();
		placeHolder.setMinHeight(50);
		header.setCenter(placeHolder);
		VBox.setVgrow(header, Priority.ALWAYS);
		VBox body = new VBox();
		body.setMinHeight(40 + 50);
		content.getChildren().addAll(header, body);
		body.setStyle("-fx-background-radius: 0 0 5 5; -fx-background-color: rgb(255,255,255,0.87);");

		setupHeader(header);
		setupBody(body);
		body.maxWidthProperty().bind(header.maxWidthProperty());
		body.prefWidthProperty().bind(header.prefWidthProperty());
		body.minWidthProperty().bind(header.minWidthProperty());

		// create button
		JFXButton button = new JFXButton("");
		button.setButtonType(JFXButton.ButtonType.RAISED);
		button.setStyle("-fx-background-radius: 40;-fx-background-color: " + getDefaultColor((int) ((Math.random() * 12) % 12)));
		button.setPrefSize(40, 40);
		button.setRipplerFill(Color.valueOf(headerColor));
//		button.setScaleX(0);
//		button.setScaleY(0);
		Icon glyph = new Icon("DOWNLOAD");
		glyph.setTextFill(Color.WHITE);
		glyph.setPrefSize(40, 40);
		glyph.setAlignment(Pos.CENTER);
//		SVGGlyph glyph= new SVGGlyph(-1, "test", "M1008 6.286q18.857 13.714 15.429 36.571l-146.286 877.714q-2.857 16.571-18.286 25.714-8 4.571-17.714 4.571-6.286 0-13.714-2.857l-258.857-105.714-138.286 168.571q-10.286 13.143-28 13.143-7.429 0-12.571-2.286-10.857-4-17.429-13.429t-6.571-20.857v-199.429l493.714-605.143-610.857 528.571-225.714-92.571q-21.143-8-22.857-31.429-1.143-22.857 18.286-33.714l950.857-548.571q8.571-5.143 18.286-5.143 11.429 0 20.571 6.286z", Color.WHITE);
//		glyph.setSize(20, 20);
		button.setGraphic(glyph);

		ImageView imageView = new ImageView(new Image(project.getImageUrl()));
		StackPane imageViewContainer = new StackPane(imageView);
		imageViewContainer.setMaxHeight(64);
		imageViewContainer.setMaxWidth(64);
		imageViewContainer.setPrefWidth(64);
		imageViewContainer.setPrefHeight(64);
		imageViewContainer.setStyle("-fx-background-color:WHITE");

		JFXDepthManager.setDepth(imageViewContainer, 2);
		BorderPane mics = new BorderPane();
		mics.setPadding(new Insets(5, 10, 5, 10));
		mics.setLeft(imageViewContainer);
		mics.setRight(button);


		mics.translateYProperty().bind(Bindings.createDoubleBinding(() ->
				header.getBoundsInParent().getHeight() - button.getHeight() / 2, header.boundsInParentProperty(), mics
				.heightProperty()));
		StackPane.setAlignment(mics, Pos.CENTER);

		JFXDepthManager.setDepth(this, 1);
		this.getChildren().setAll(content, mics);
	}

	protected void setupBody(VBox body)
	{
		CurseForgeProject project = getProject();
		if (project == null) return;
		Label label = new Label(project.getDescription());
		label.setWrapText(true);
		label.setMaxWidth(200);
		VBox container = new VBox(label);
		container.setAlignment(Pos.CENTER);
		container.setStyle("-fx-padding:60 30 30 30");
		label.setAlignment(Pos.CENTER);
		body.setAlignment(Pos.CENTER);
		body.getChildren().add(container);
	}

	protected void setupHeader(BorderPane header)
	{
		CurseForgeProject curseForgeProject = this.project.get();
		if (curseForgeProject == null) return;
		Label name = new Label(curseForgeProject.getName()),
				author = new Label(curseForgeProject.getAuthor());
		name.getStyleClass().add("header-major-small");
		author.getStyleClass().add("header-minor-small");
		VBox top = new VBox();
		name.setWrapText(true);
		top.setSpacing(10);
		top.getChildren().add(name);
		top.getChildren().add(author);
		BorderPane mics = new BorderPane();

		Label count = new Label(curseForgeProject.getDownloadCount());
		count.getStyleClass().add("header-minor");
		Icon down = new Icon("DOWNLOAD");
		down.setTextFill(Color.WHITE);
		HBox left = new HBox(down, count);
		left.setSpacing(5);

		Label date = new Label(DateFormat.getInstance().format(curseForgeProject.getLastTime()));
		date.getStyleClass().add("header-minor");
		Icon clock = new Icon("CLOCK_ALT");
		clock.setTextFill(Color.WHITE);
		HBox right = new HBox(clock, date);
		right.setSpacing(5);

		mics.setLeft(left);
		mics.setRight(right);
		mics.setStyle("-fx-padding:0 0 10 0");

		header.setPadding(new Insets(20));
		header.setTop(top);
		header.setBottom(mics);
	}

	private String getDefaultColor(int i)
	{
		String color = "#FFFFFF";
		switch (i)
		{
			case 0:
				color = "#8F3F7E";
				break;
			case 1:
				color = "#B5305F";
				break;
			case 2:
				color = "#CE584A";
				break;
			case 3:
				color = "#DB8D5C";
				break;
			case 4:
				color = "#DA854E";
				break;
			case 5:
				color = "#E9AB44";
				break;
			case 6:
				color = "#FEE435";
				break;
			case 7:
				color = "#99C286";
				break;
			case 8:
				color = "#01A05E";
				break;
			case 9:
				color = "#4A8895";
				break;
			case 10:
				color = "#16669B";
				break;
			case 11:
				color = "#2F65A5";
				break;
			case 12:
				color = "#4E6A9C";
				break;
			default:
				break;
		}
		return color;
	}
}
