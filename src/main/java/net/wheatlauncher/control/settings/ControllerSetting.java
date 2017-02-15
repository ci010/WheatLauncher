package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXListView;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.StackPane;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class ControllerSetting
{
	public JFXListView<Node> options;
//	public JFXListView<Node> resourcePackSubList;

	public StackPane container;

	private Map<String, Node> content = new TreeMap<>();

	public void initialize()
	{
		for (Node node : container.getChildren()) content.put(node.getId(), node);
		container.getChildren().clear();

		options.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		options.getSelectionModel().selectedItemProperty().addListener(observable ->
				switchTo(options.getSelectionModel().getSelectedItems().get(0)));
		options.getSelectionModel().select(0);
//		resourcePackSubList.getSelectionModel().selectedIndexProperty().addListener(observable ->
//				switchTo(resourcePackSubList.getSelectionModel().getSelectedItems().get(0)));
	}

	private void switchTo(Node from)
	{
		if (from == null) return;
		if (from instanceof JFXListView) return;
		Node node = content.get(from.getId());
		if (node != null) container.getChildren().setAll(node);
	}
}
