package net.wheatlauncher.control;

import api.launcher.ARML;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import net.launcher.FXServerInfo;
import net.launcher.TextComponentConverter;
import net.launcher.control.ImageCellBase;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerInfoBase;
import net.launcher.game.ServerStatus;

import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.function.Predicate;

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
		refresh();
	}

	public void add()
	{
		ServerInfo localhost = new FXServerInfo(new ServerInfoBase("Minecraft Server", "localhost"));
		ARML.core().getServerManager().getAllServers().add(localhost);
	}

	public void remove()
	{
		ServerInfo item = serverList.getSelectionModel().getSelectedItem();
		ARML.core().getServerManager().getAllServers().remove(item);
	}

	private Task<ServerStatus> pingServerTask(ServerInfo serverInfo)
	{
		Task<ServerStatus> serverStatusTask = ARML.core().getServerManager().fetchInfoAndWaitPing(serverInfo);

		serverStatusTask.setOnScheduled(event ->
		{
			FXServerInfo serverInfo1 = (FXServerInfo) serverInfo;
			serverInfo1.setStatus(ServerStatus.pinging());
		});
		serverStatusTask.setOnSucceeded(event ->
		{
			Worker<ServerStatus> source = event.getSource();
			FXServerInfo serverInfo1 = (FXServerInfo) serverInfo;
			serverInfo1.setStatus(source.getValue());
		});
		serverStatusTask.setOnFailed(event ->
		{
			Throwable exception = event.getSource().getException();
			if (exception instanceof UnknownHostException)
			{
				FXServerInfo serverInfo1 = (FXServerInfo) serverInfo;
				serverInfo1.setStatus(ServerStatus.unknownHost());
			}
			else
			{
				FXServerInfo serverInfo1 = (FXServerInfo) serverInfo;
				serverInfo1.setStatus(ServerStatus.error());
			}
		});
		return serverStatusTask;
	}

	public void refresh()
	{
		for (ServerInfo serverInfo : serverList.getItems())
			ARML.core().getTaskCenter().runTask(pingServerTask(serverInfo));
	}

	public void edit()
	{
		if (serverList.getEditingIndex() == -1)
			serverList.edit(serverList.getSelectionModel().getSelectedIndex());
		else
			serverList.edit(-1);
	}

	private class ServerCells extends ListCell<ServerInfo>
	{
		private JFXTextField ip, name;

		private ImageCellBase<ServerInfo> graphic = new ImageCellBase<>();

		private Node commonContent;
		private Node editContent;
		private Label nameLabel, capaLabel, pingLabel;
		private TextFlow motd;

		ServerCells()
		{
			this.getStyleClass().add("base-cell");
			motd = new TextFlow();
			nameLabel = new Label();
			capaLabel = new Label();
			pingLabel = new Label();
			BorderPane borderPane = new BorderPane();
			VBox borderLeft = new VBox();
			BorderPane borderRight = new BorderPane();
			borderPane.setLeft(borderLeft);
			borderPane.setRight(borderRight);
			borderLeft.getChildren().addAll(nameLabel, motd);
			borderRight.setTop(capaLabel);
			borderRight.setBottom(pingLabel);
			BorderPane.setAlignment(borderRight, Pos.TOP_RIGHT);

			graphic.setRight(commonContent = borderPane);

			VBox editContent = new VBox();

			Label ipLabel = new Label(resources.getString("server.ip"), ip = new JFXTextField());
			ipLabel.setContentDisplay(ContentDisplay.RIGHT);

			Label nameLabel = new Label(resources.getString("server.name"), name = new JFXTextField());
			nameLabel.setContentDisplay(ContentDisplay.RIGHT);

			editContent.setSpacing(5);
			editContent.getChildren().addAll(nameLabel, ipLabel);
			this.editContent = editContent;

			EventHandler<KeyEvent> keyEventsHandler = t ->
			{
				if (t.getCode() == KeyCode.ENTER) commitEdit(getItem());
				else if (t.getCode() == KeyCode.ESCAPE) cancelEdit();
			};

			ChangeListener<Boolean> focusChangeListener = (observable, oldValue, newValue) ->
			{
				if (!newValue) cancelEdit();
			};
			this.addEventHandler(KeyEvent.KEY_RELEASED, keyEventsHandler);
			this.focusedProperty().addListener(focusChangeListener);
		}

		@Override
		public void startEdit()
		{
			super.startEdit();
			graphic.setRight(editContent);
			if (getItem() != null)
			{
				ip.setText(getItem().getHostName());
				name.setText(getItem().getName());
			}
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
			if (newValue != null)
				if (!ip.getText().equals(newValue.getHostName()) || !name.getText().equals(newValue.getName()))
				{
					newValue.setHostName(ip.getText());
					newValue.setName(name.getText());
					((FXServerInfo) newValue).invalidated(null);
					ARML.core().getTaskCenter().runTask(pingServerTask(newValue));
				}
			graphic.setRight(commonContent);
		}

		@Override
		protected void updateItem(ServerInfo item, boolean empty)
		{
			super.updateItem(item, empty);
			if (item != null && !empty && getItem() != null)
			{
				nameLabel.textProperty().bind(((FXServerInfo) getItem()).nameProperty());
				capaLabel.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (getItem() == null) return "?/?";
					ServerStatus status = ((FXServerInfo) getItem()).getStatus();
					if (status != null) return status.getOnlinePlayers() + "/" + status.getCapability();
					return "?/?";
				}, ((FXServerInfo) getItem()).statusProperty()));

				pingLabel.textProperty().bind(Bindings.createStringBinding(() ->
				{
					if (getItem() == null) return "NaN ms";
					ServerStatus status = ((FXServerInfo) getItem()).getStatus();
					if (status == null) return "NaN ms";
					return status.getPingToServer() + " ms";
				}, ((FXServerInfo) getItem()).statusProperty()));
				ServerStatus status = ((FXServerInfo) getItem()).getStatus();
				if (status != null) TextComponentConverter.convert(status.getServerMOTD(), motd);

				((FXServerInfo) getItem()).statusProperty().addListener(observable ->
				{
					if (getItem() == null) return;
					ServerStatus status0 = ((FXServerInfo) getItem()).getStatus();
					if (status0 != null) TextComponentConverter.convert(status0.getServerMOTD(), motd);
				});
				graphic.imageProperty().bind(Bindings.createObjectBinding(() -> ServerInfo.createServerIcon(item), ((FXServerInfo) getItem()).serverIconProperty()));
				setGraphic(graphic);
			}
			else setGraphic(null);
		}

	}

}
