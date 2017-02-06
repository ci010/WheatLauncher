package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import net.launcher.utils.ResolutionUtils;
import org.to2mbn.jmccc.option.WindowSize;

import java.util.Comparator;


/**
 * @author ci010
 */
public class MinecraftOptionResolution extends MinecraftOptionWidget
{
	private ObjectProperty<WindowSize> windowSize;
	private JFXTextField width, height;

	private static WindowSize[] commons = new WindowSize[6];

	static
	{
		commons[0] = new WindowSize(856, 482);
		commons[1] = new WindowSize(1024, 612);
		commons[2] = new WindowSize(1280, 766);
		commons[3] = new WindowSize(1712, 1024);
		commons[4] = new WindowSize(1980, 1184);
		commons[5] = WindowSize.fullscreen();
	}

	private WindowSize updateWindow(String width, String height)
	{
		try
		{
			return new WindowSize(Integer.valueOf(width), Integer.valueOf(height));
		}
		catch (Exception e)
		{
			return new WindowSize(856, 482);
		}
	}

	private void trim(JFXTextField width, JFXTextField height, boolean left)
	{
		WindowSize windowSize;
		try
		{
			windowSize = new WindowSize(Integer.valueOf(width.getText()), Integer.valueOf(height.getText()));
		}
		catch (Exception e) {windowSize = getWindowSize();}
		Comparator<WindowSize> comparator = ResolutionUtils.getComparator();
		int min = Integer.MAX_VALUE;
		int minIdx = -1;
		for (int i = 0; i < commons.length; i++)
		{
			int compare = comparator.compare(windowSize, commons[i]);
			if (Math.abs(min) > Math.abs(compare))
			{
				min = compare;
				minIdx = i;
			}
		}

		int leftIdx, rightIdx;

		if (min > 0)
		{
			leftIdx = minIdx;
			rightIdx = minIdx + 1;
		}
		else
		{
			leftIdx = minIdx - 1;
			rightIdx = minIdx;
		}

		if (leftIdx < 0)
			leftIdx = 0;
		if (rightIdx >= commons.length)
			rightIdx = commons.length - 1;

		if (left)
		{

		}
	}

	@Override
	protected Node createContent()
	{
		this.windowSize = new SimpleObjectProperty<>();
		ChangeListener<String> txtLis = (observable, oldValue, newValue) ->
		{
			if (!newValue.matches("\\d*"))
				((StringProperty) observable).set(newValue.replaceAll("[^\\d]", ""));
		};
		width = new JFXTextField();
		height = new JFXTextField();
		width.textProperty().addListener(txtLis);
		height.textProperty().addListener(txtLis);
		width.setMaxWidth(50);
		height.setMaxWidth(50);
		width.setText("856");
		height.setText("482");
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

		left.setOnAction(event -> trim(width, height, true));
		right.setOnAction(event -> trim(width, height, false));
		windowSize.bind(Bindings.createObjectBinding(() ->
						updateWindow(width.getText(), height.getText()),
				width.textProperty(), height.textProperty()));
		this.value.bind(Bindings.createStringBinding(() -> windowSize.get().toString(), windowSize));

		Label label = new Label("X");

		BorderPane borderPane = new BorderPane();
		HBox hBox = new HBox(width, label, height);
		hBox.setAlignment(Pos.CENTER);
		borderPane.setCenter(hBox);
		borderPane.setLeft(left);
		borderPane.setRight(right);
		return borderPane;
	}

	public WindowSize getWindowSize()
	{
		return windowSize.get();
	}

	public ReadOnlyObjectProperty<WindowSize> windowSizeProperty()
	{
		return windowSize;
	}

	public JFXTextField getWidthField()
	{
		return width;
	}

	public JFXTextField getHeightField()
	{
		return height;
	}
}
