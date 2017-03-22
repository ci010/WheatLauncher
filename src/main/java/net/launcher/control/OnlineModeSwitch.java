package net.launcher.control;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.skins.JFXComboBoxListViewSkin;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ci010
 */
public class OnlineModeSwitch extends JFXComboBox<String>
{
	private BooleanProperty isOffline = new SimpleBooleanProperty();

	{
		getItems().addAll(ARML.core().getAuthManager().getAuthorizeMap().keySet());
		getSelectionModel().select(0);
		getSelectionModel().selectedItemProperty().addListener(observable ->
		{
			isOffline.set(false);
			ARML.core().getAuthManager().setAuthorize(getValue());
		});
		isOffline.addListener(observable ->
		{
			boolean offline = isOffline();
			if (offline) ARML.core().getAuthManager().setNoAuthorize();
			else ARML.core().getAuthManager().setAuthorize(this.getValue());
		});
	}

	@Override
	protected Skin<?> createDefaultSkin()
	{
		return new SkinImpl();
	}

	public boolean isOffline()
	{
		return isOffline.get();
	}

	public BooleanProperty isOfflineProperty()
	{
		return isOffline;
	}

	public void setIsOffline(boolean isOffline)
	{
		this.isOffline.set(isOffline);
	}

	private class SkinImpl extends JFXComboBoxListViewSkin<String>
	{
		private Node displayNode;

		SkinImpl()
		{
			super(OnlineModeSwitch.this);
			List<Node> lst = new ArrayList<>();
			for (Node node : getChildren())
				if (node.getStyleClass().contains("input-line") || node.getStyleClass().contains("input-focused-line"))
					lst.add(node);
			getChildren().removeAll(lst);
		}

		@Override
		public Node getDisplayNode()
		{
			if (displayNode == null)
			{
				Icon globe = new Icon("GLOBE", "2em");
				globe.setTextFill(Color.TOMATO);
				JFXToggleNode node = new JFXToggleNode();
				node.setStyle("-fx-padding:10;");
				node.setSelectedColor(Color.WHEAT);
				node.setGraphic(globe);
				node.setPrefWidth(50);
				isOffline.bindBidirectional(node.selectedProperty());
				node.setSelected(ARML.core().getAuthManager().isOffline());
				displayNode = node;
				displayNode.getStyleClass().add("date-picker-display-node");
			}
			return displayNode;
		}
	}
}
