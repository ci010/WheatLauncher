package net.wheatlauncher.control;

import api.launcher.ARML;
import api.launcher.event.ServerEvent;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.launcher.control.ImageCellBase;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;

import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author ci010
 */
public class ControllerServersView
{
	public JFXListView<ServerInfo> serverList;
	public JFXTextField search;
	public Node removed;
	public JFXRippler edited;

	private Predicate<ServerInfo> predicate = info -> info.getHostName().contains(search.getText()) || info.getName()
			.contains(search.getText());

	public ResourceBundle resources;

	public void initialize()
	{
//		FilteredList<ServerInfo> serverInfos = new FilteredList<>(ARML.core().getServerManager().getAllServers());
//		serverInfos.predicateProperty().bind(Bindings.createObjectBinding(() -> predicate, search.textProperty()));
		serverList.setCellFactory(param -> new ServerCells());
		serverList.setItems(ARML.core().getServerManager().getAllServers());
		serverList.setOnEditCommit(event ->
		{
			Task<ServerStatus> serverStatusTask = ARML.core().getServerManager().fetchInfoAndWaitPing(event.getNewValue());
			ARML.core().getTaskCenter().runTask(serverStatusTask);

		});
		BooleanBinding booleanBinding = Bindings.createBooleanBinding(() -> serverList.getSelectionModel().isEmpty(),
				serverList.getSelectionModel().selectedIndexProperty());
		edited.disableProperty().bind(booleanBinding);
		removed.disableProperty().bind(booleanBinding);
	}

	public void add()
	{
		ServerInfo localhost = new ServerInfo("Minecraft Server", "localhost");
		ARML.core().getServerManager().getAllServers().add(localhost);
	}

	public void remove()
	{
		ServerInfo item = serverList.getSelectionModel().getSelectedItem();
		ARML.core().getServerManager().getAllServers().remove(item);
	}

	public void refresh()
	{
		for (ServerInfo serverInfo : serverList.getItems())
		{
			Task<ServerStatus> serverStatusTask = ARML.core().getServerManager().fetchInfoAndWaitPing(serverInfo);
			serverStatusTask.setOnScheduled(event ->
			{

			});
			ARML.core().getTaskCenter().runTask(serverStatusTask);
		}
	}

	public void edit()
	{
		if (serverList.getEditingIndex() == -1)
			serverList.edit(serverList.getSelectionModel().getSelectedIndex());
		else
			serverList.edit(-1);
	}

	private final Pattern PATTERN = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private class ServerCells extends ListCell<ServerInfo>
	{
		private JFXTextField ip, name;
		private ServerStatus status;

		private ImageCellBase<ServerInfo> graphic = new ImageCellBase<>();

		private HBox commonContent;
		private Node editContent;
		private Label nameLabel = new Label(), motd = new Label();

		{
			this.getStyleClass().add("base-cell");
			commonContent = new HBox(nameLabel, motd);
			commonContent.setSpacing(5);
			graphic.setRight(commonContent);
			VBox editContent = new VBox();
			ip = new JFXTextField();
			name = new JFXTextField();
			editContent.setSpacing(5);
			editContent.getChildren().addAll(name, ip);
			this.editContent = editContent;

			EventHandler<KeyEvent> keyEventsHandler = t ->
			{
				if (t.getCode() == KeyCode.ENTER)
					commitEdit(getItem());
				else if (t.getCode() == KeyCode.ESCAPE)
					cancelEdit();
			};

			ChangeListener<Boolean> focusChangeListener = (observable, oldValue, newValue) ->
			{
				if (!newValue) commitEdit(getItem());
			};
			this.addEventHandler(KeyEvent.KEY_RELEASED, keyEventsHandler);
			this.focusedProperty().addListener(focusChangeListener);
		}

		private void updateDisplay()
		{
			nameLabel.setText(getItem().getName());
			Image serverIcon = ServerInfo.createServerIcon(getItem());
			if (serverIcon != null)
				graphic.setImage(serverIcon);
			if (status != null)
				motd.setText(status.getServerMOTD());
		}

		@Override
		public void startEdit()
		{
			super.startEdit();
			graphic.setRight(editContent);
			ip.setText(getItem().getHostName());
			name.setText(getItem().getName());
		}

		@Override
		public void cancelEdit()
		{
			super.cancelEdit();
			graphic.setRight(commonContent);
		}

		@Override
		public void commitEdit(ServerInfo newValue)
		{
			super.commitEdit(newValue);
			graphic.setRight(commonContent);
			ARML.bus().postEvent(new ServerEvent(ServerEvent.MODIFY, newValue));
		}

		@Override
		protected void updateItem(ServerInfo item, boolean empty)
		{
			super.updateItem(item, empty);
			if (item != null && !empty)
			{
				nameLabel.setText(item.getName());
				setGraphic(graphic);
			}
		}

	}

}
