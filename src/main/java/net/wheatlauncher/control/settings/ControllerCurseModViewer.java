package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import net.wheatlauncher.control.utils.ReloadableController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

/**
 * @author ci010
 */
public class ControllerCurseModViewer implements ReloadableController
{
	public StackPane root;
	public JFXMasonryPane masonryPane;
	public ScrollPane scrollPane;

	private Random random = new Random();
	private List<StackPane> modinfos;

	
	@PostConstruct
	public void init()
	{

		int index = 0;
		StackPane stackPane = buildContainer();
		// newService content
		VBox content = new VBox();
		StackPane header = new StackPane();
		String headerColor = getDefaultColor(index % 12);
		header.setStyle("-fx-background-radius: 5 5 0 0; -fx-background-color: " + headerColor);
		VBox.setVgrow(header, Priority.ALWAYS);
		StackPane body = new StackPane();
		body.setMinHeight(Math.random() * 20 + 50);
		content.getChildren().addAll(header, body);
		body.setStyle("-fx-background-radius: 0 0 5 5; -fx-background-color: rgb(255,255,255,0.87);");

		buildButton(header, headerColor, index);
	}

	//call on refresh
	private JFXButton buildButton(StackPane header, String baseColor, int index)
	{
		JFXButton button = new JFXButton("");
		button.setButtonType(JFXButton.ButtonType.RAISED);
		button.setStyle("-fx-background-radius: 40;-fx-background-color: " + getDefaultColor((int) ((Math.random() * 12) % 12)));
		button.setPrefSize(40, 40);
		button.setRipplerFill(Color.valueOf(baseColor));
		button.setScaleX(0);
		button.setScaleY(0);
		SVGGlyph glyph = new SVGGlyph(-1, "test", "M1008 6.286q18.857 13.714 15.429 36.571l-146.286 877.714q-2.857 16.571-18.286 25.714-8 4.571-17.714 4.571-6.286 0-13.714-2.857l-258.857-105.714-138.286 168.571q-10.286 13.143-28 13.143-7.429 0-12.571-2.286-10.857-4-17.429-13.429t-6.571-20.857v-199.429l493.714-605.143-610.857 528.571-225.714-92.571q-21.143-8-22.857-31.429-1.143-22.857 18.286-33.714l950.857-548.571q8.571-5.143 18.286-5.143 11.429 0 20.571 6.286z", Color.WHITE);
		glyph.setSize(20, 20);
		button.setGraphic(glyph);
		button.translateYProperty().bind(Bindings.createDoubleBinding(() ->
				header.getBoundsInParent().getHeight() - button.getHeight() / 2, header.boundsInParentProperty(), button.heightProperty()));
		StackPane.setMargin(button, new Insets(0, 12, 0, 0));
		StackPane.setAlignment(button, Pos.TOP_RIGHT);

		Timeline animation = new Timeline(new KeyFrame(Duration.millis(240), new KeyValue(button.scaleXProperty(), 1, Interpolator.EASE_BOTH),
				new KeyValue(button.scaleYProperty(), 1, Interpolator.EASE_BOTH)));
		animation.setDelay(Duration.millis(100 * index + (1000)));
		animation.play();
		return button;
	}

	private StackPane buildContainer()
	{
		StackPane child = new StackPane();
		double width = Math.random() * 100 + 100;
		child.setMinWidth(width);
		child.setMaxWidth(width);
		child.setPrefWidth(width);
		double height = Math.random() * 100 + 100;
		child.setMinHeight(height);
		child.setMaxHeight(height);
		child.setPrefHeight(height);
		JFXDepthManager.setDepth(child, 1);
		return child;
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

	@Override
	public void reload()
	{

	}

	@Override
	public void unload()
	{

	}
}
