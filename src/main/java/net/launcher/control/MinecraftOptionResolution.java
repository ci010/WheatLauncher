package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import net.launcher.ArrayUtils;
import net.launcher.utils.EnvironmentUtils;
import net.launcher.utils.Tasks;
import org.to2mbn.jmccc.option.WindowSize;

import java.util.Comparator;


/**
 * @author ci010
 */
public class MinecraftOptionResolution extends MinecraftOptionWidget
{
	private ObjectProperty<WindowSize> windowSize;
	private IntegerProperty screenWidth,
			screenHeight;
	private JFXTextField widthSrc, heightSrc;

	private static WindowSize[] commons = new WindowSize[5];

	static
	{
		commons[0] = new WindowSize(856, 482);
		commons[1] = new WindowSize(1024, 612);
		commons[2] = new WindowSize(1280, 766);
		commons[3] = new WindowSize(1712, 1024);
		commons[4] = new WindowSize(1980, 1184);
//		commons[5] = WindowSize.fullscreen();
	}

	private void trim(boolean left)
	{
		WindowSize windowSize = Tasks.optional(() -> new WindowSize(Integer.valueOf(widthSrc.getText()), Integer.valueOf(heightSrc.getText())))
				.orElse(getWindowSize());
		int snap = ArrayUtils.snap(commons, windowSize, EnvironmentUtils.getComparator(), left);
		windowSize = commons[snap];
		screenWidth.set(windowSize.getWidth());
		screenHeight.set(windowSize.getHeight());
	}

	@Override
	protected boolean shouldHide()
	{
		return !widthSrc.isFocused() && !heightSrc.isFocused() && super.shouldHide();
	}

	@Override
	protected Node createContent()
	{
		windowSize = new SimpleObjectProperty<>();
		screenWidth = new SimpleIntegerProperty(856);
		screenHeight = new SimpleIntegerProperty(482);

		ChangeListener<String> txtLis = (observable, oldValue, newValue) ->
		{
			if (!newValue.matches("\\d*"))
				((StringProperty) observable).set(newValue.replaceAll("[^\\d]", ""));
		};
		widthSrc = new JFXTextField();
		heightSrc = new JFXTextField();
		widthSrc.textProperty().addListener(txtLis);
		heightSrc.textProperty().addListener(txtLis);
		widthSrc.setMaxWidth(50);
		heightSrc.setMaxWidth(50);
		widthSrc.setText("856");
		heightSrc.setText("482");


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

		left.setOnAction(event -> trim(true));
		right.setOnAction(event -> trim(false));

		Label label = new Label("X");

		BorderPane borderPane = new BorderPane();
		HBox hBox = new HBox(widthSrc, label, heightSrc);
		hBox.setAlignment(Pos.CENTER);
		borderPane.setCenter(hBox);
		borderPane.setLeft(left);
		borderPane.setRight(right);

		InvalidationListener listener = observable ->
		{
			if (shouldHide())
				hide();
		};
		widthSrc.focusedProperty().addListener(listener);
		heightSrc.focusedProperty().addListener(listener);
		widthSrc.textProperty().addListener(observable ->
				Tasks.optional(() -> Integer.valueOf(widthSrc.getText())).ifPresent(screenWidth::set));
		heightSrc.textProperty().addListener(observable ->
				Tasks.optional(() -> Integer.valueOf(heightSrc.getText())).ifPresent(screenHeight::set));
		screenHeight.addListener(observable ->
				heightSrc.setText(String.valueOf(screenHeight.get())));
		screenWidth.addListener(observable ->
				widthSrc.setText(String.valueOf(screenWidth.get())));
		windowSize.bind(Bindings.createObjectBinding(() ->
		{
			WindowSize windowSize = new WindowSize(screenWidth.get(), screenHeight.get());
			Comparator<WindowSize> comparator = EnvironmentUtils.getComparator();
			int compare = comparator.compare(windowSize, EnvironmentUtils.getScreenSize());
			if (compare > 0) windowSize = WindowSize.fullscreen();
			return windowSize;
		}, screenWidth, screenHeight));
		value.bind(Bindings.createStringBinding(() -> windowSize.get().toString(), windowSize));

		return borderPane;
	}

	public int getScreenWidth()
	{
		return screenWidth.get();
	}

	public IntegerProperty screenWidthProperty()
	{
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth)
	{
		this.screenWidth.set(screenWidth);
	}

	public int getScreenHeight()
	{
		return screenHeight.get();
	}

	public IntegerProperty screenHeightProperty()
	{
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight)
	{
		this.screenHeight.set(screenHeight);
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
		return widthSrc;
	}

	public JFXTextField getHeightField()
	{
		return heightSrc;
	}
}
