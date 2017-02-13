package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import net.launcher.ArrayUtils;
import net.launcher.utils.EnvironmentUtils;
import net.launcher.utils.Tasks;

/**
 * @author ci010
 */
public class MinecraftOptionMemory extends MinecraftOptionWidget
{
	private IntegerProperty memory;
	private JFXTextField memo;

	private void snap(boolean left)
	{
		int[] availableMemory = EnvironmentUtils.getAvailableMemory();
		memory.set(availableMemory[ArrayUtils.snap(availableMemory, memory.get(), left)]);
	}

	@Override
	protected boolean shouldHide()
	{
		return !memo.isFocused() && super.shouldHide();
	}

	protected Node createContent()
	{
		memory = new SimpleIntegerProperty();
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
		memo = new JFXTextField();
		memo.setAlignment(Pos.CENTER);
		ChangeListener<String> txtLis = (observable, oldValue, newValue) ->
		{
			if (!newValue.matches("\\d*"))
				((StringProperty) observable).set(newValue.replaceAll("[^\\d]", ""));
		};
		memo.textProperty().addListener(txtLis);
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(left);
		borderPane.setRight(right);
		borderPane.setCenter(memo);

		left.setOnAction(event -> snap(true));
		right.setOnAction(event -> snap(false));
		memo.textProperty().addListener(observable -> Tasks.optional(() -> Integer.valueOf(memo.getText())).ifPresent
				(memory::set));
		memory.addListener(observable -> memo.setText(String.valueOf(memory.get())));
		memo.focusedProperty().addListener(observable ->
		{
			if (shouldHide()) hide();
		});
		this.value.bind(Bindings.createStringBinding(() -> String.valueOf(memory.getValue()), memory));
		memo.setText("512");
		return borderPane;
	}

	public int getMemory()
	{
		return memory.get();
	}

	public IntegerProperty memoryProperty()
	{
		return memory;
	}

	public void setMemory(int memory)
	{
		this.memory.set(memory);
	}
}
