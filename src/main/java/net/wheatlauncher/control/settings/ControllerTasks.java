package net.wheatlauncher.control.settings;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.fontawesome.Icon;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Worker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


/**
 * @author ci010
 */
public class ControllerTasks
{
	public JFXListView<Worker<?>> list;

	public void initialize()
	{
		list.setItems(ARML.taskCenter().getAllWorkerHistory());
		list.setCellFactory(param -> new JFXListCell<Worker<?>>()
		{
			@Override
			public void updateItem(Worker<?> item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item != null && !empty)
				{
					BorderPane borderPane = new BorderPane();
					Label title = new Label();
					title.textProperty().bind(Bindings.createStringBinding(() ->
					{
						String title1 = item.getTitle();
						if (title1 == null || title1.equals(""))
							return "Unknown";
						return title1;
					}));
					borderPane.setLeft(title);
					borderPane.centerProperty().bind(Bindings.createObjectBinding(() ->
							{
								Throwable exception = item.getException();
								if (exception != null)
								{
									Label label = new Label(exception.toString());
									label.setTextFill(Color.RED);
									return label;
								}
								String message = item.getMessage();
								if (message != null) return new Label(message);
								return null;
							},
							item.messageProperty(), item.exceptionProperty()));
					borderPane.rightProperty().bind(Bindings.createObjectBinding(() ->
					{
						Worker.State state = item.getState();
						switch (state)
						{
							case READY:
								Icon track = new Icon("CLOCK");
								track.setTooltip(new Tooltip("READY"));
								return track;
							case SCHEDULED:
							case RUNNING:
								return new JFXSpinner();
							case SUCCEEDED:
								Icon icon = new Icon("CHECK_CIRCLE");
								icon.setTextFill(Color.GREEN);
								return icon;
							case CANCELLED:
								return new Icon("MINUS_CIRCLE");
							case FAILED:
								return new Icon("WARNING");
						}
						return new Icon("WARNING");
					}, item.stateProperty()));
					setGraphic(borderPane);
				}
				else setGraphic(null);
			}
		});
//		table.setItems(ARML.taskCenter().getAllWorkerHistory());
//		state.setCellValueFactory(param -> Bindings.createObjectBinding(() ->
//		{
//			Worker.State state = param.getValue().getState();
//			switch (state)
//			{
//				case READY:
//					Icon track = new Icon("CLOCK2");
//					track.setTooltip(new Tooltip("READY"));
//					return track;
//				case SCHEDULED:
//				case RUNNING:
//					return new JFXSpinner();
//				case SUCCEEDED:
//					return new Icon("CHECK_CIRCLE");
//				case CANCELLED:
//					return new Icon("MINUS_CIRCLE");
//				case FAILED:
//					return new Icon("WARNING");
//			}
//			return new Icon("WARNING");
//		}, param.getValue().stateProperty()));
//		title.setCellValueFactory(param -> param.getValue().titleProperty());
//		message.setCellValueFactory(param -> param.getValue().messageProperty());
//		progress.setCellValueFactory(param -> Bindings.createStringBinding(() ->
//						param.getValue().getProgress() + "/" + param.getValue().getTotalWork(),
//				param.getValue().progressProperty()));
//		exception.setCellValueFactory(param -> Bindings.createStringBinding(() ->
//				{
//					return String.valueOf(param.getValue().getException());
//				}
//				, param.getValue().exceptionProperty()));
	}
}
