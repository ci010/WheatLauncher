package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author ci010
 */
public class MinecraftOptionMemory extends MinecraftOptionWidget
{
	protected Node createContent()
	{
		JFXButton left = new JFXButton();
		left.setStyle("-fx-padding:3,3,3,3; -fx-background-color:TRANSPARENT;");
		Icon angle_left = new Icon("ANGLE_LEFT");
		angle_left.setScaleX(0.8);
		angle_left.setScaleY(0.8);
		left.setGraphic(angle_left);

		JFXButton right = new JFXButton();
		right.setStyle("-fx-padding:3,3,3,3; -fx-background-color:TRANSPARENT;");
		Icon angle_right = new Icon("ANGLE_RIGHT");
		angle_right.setScaleX(0.8);
		angle_right.setScaleY(0.8);
		right.setGraphic(angle_right);
		JFXTextField min = new JFXTextField();
		JFXTextField max = new JFXTextField();

		ChangeListener<String> txtLis = (observable, oldValue, newValue) ->
		{
			if (!newValue.matches("\\d*"))
				((StringProperty) observable).set(newValue.replaceAll("[^\\d]", ""));
		};
		max.textProperty().addListener(txtLis);
		min.textProperty().addListener(txtLis);
		max.setMaxWidth(50);
		min.setMaxWidth(50);
		Label to = new Label("-");
		HBox bx = new HBox(min, to, max);
		bx.setAlignment(Pos.CENTER);
		return new HBox(left, bx, right);
	}
}
