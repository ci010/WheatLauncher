package net.wheatlauncher.control.settings;

import api.launcher.ARML;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTableView;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;


/**
 * @author ci010
 */
public class ControllerTasks
{
	public JFXTableView<Worker<?>> table;
	public TableColumn<Worker<?>, Node> state;
	public TableColumn<Worker<?>, String> title;
	public TableColumn<Worker<?>, String> message;
	public TableColumn<Worker<?>, String> progress;
	public TableColumn<Worker<?>, String> exception;

	public void initialize()
	{
		table.setItems(ARML.core().getTaskCenter().getAllWorkerHistory());
		state.setCellValueFactory(param -> Bindings.createObjectBinding(() ->
		{
			Worker.State state = param.getValue().getState();
			switch (state)
			{
				case READY:
					Icon track = new Icon("CLOCK2");
					track.setTooltip(new Tooltip("READY"));
					return track;
				case SCHEDULED:
				case RUNNING:
					return new JFXSpinner();
				case SUCCEEDED:
					return new Icon("CHECK_CIRCLE");
				case CANCELLED:
					return new Icon("MINUS_CIRCLE");
				case FAILED:
					return new Icon("WARNING");
			}
			return new Icon("WARNING");
		}, param.getValue().stateProperty()));
		title.setCellValueFactory(param -> param.getValue().titleProperty());
		message.setCellValueFactory(param -> param.getValue().messageProperty());
		progress.setCellValueFactory(param -> Bindings.createStringBinding(() ->
						param.getValue().getProgress() + "/" + param.getValue().getTotalWork(),
				param.getValue().progressProperty()));
		exception.setCellValueFactory(param -> Bindings.createStringBinding(() ->
				{
					return String.valueOf(param.getValue().getException());
				}
				, param.getValue().exceptionProperty()));
	}
}
