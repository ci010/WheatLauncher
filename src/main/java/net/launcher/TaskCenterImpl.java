package net.launcher;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author ci010
 */
public class TaskCenterImpl implements TaskCenter, InvalidationListener
{
	private ObservableList<Worker<?>> workers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
	private ObservableList<Service<?>> services = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	@Override
	public <T> Task<T> wrap(Callable<T> callable)
	{
		Objects.requireNonNull(callable);
		Task<T> task = new Task<T>()
		{
			@Override
			protected T call() throws Exception {return callable.call();}
		};
		task.setOnFailed(event -> event.getSource().getException());
		return task;
	}

	@Override
	public <T> Task<T> run(Callable<T> callable)
	{
		Objects.requireNonNull(callable);
		Task<T> task = new Task<T>()
		{
			@Override
			protected T call() throws Exception {return callable.call();}
		};
		task.setOnFailed(event -> event.getSource().getException());
		task.run();

		return task;
	}

	@Override
	public void reportError(Throwable throwable)
	{

	}

	@Override
	public void listen(Worker<?> worker)
	{
		Objects.requireNonNull(worker);
		workers.add(worker);
		worker.workDoneProperty().addListener(this);
		if (worker instanceof Service)
			services.add((Service<?>) worker);
	}

	@Override
	public ObservableList<Worker<?>> getAllRunningWorkers()
	{
		return workers;
	}

	@Override
	public ObservableList<Service<?>> getAllRunningServices()
	{
		return services;
	}

	@Override
	public void invalidated(Observable observable)
	{
		if (workers.remove(((Property) observable).getBean()))
			observable.removeListener(this);
	}
}
