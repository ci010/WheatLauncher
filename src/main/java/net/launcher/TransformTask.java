package net.launcher;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author ci010
 */
public abstract class TransformTask<U, T> extends Task<U>
{
	private Task<T> task;

	public TransformTask(Task<T> task)
	{
		this.task = task;
		((StringProperty) this.messageProperty()).bind(task.messageProperty());
		((DoubleProperty) this.progressProperty()).bind(task.progressProperty());
		((DoubleProperty) this.totalWorkProperty()).bind(task.totalWorkProperty());
	}

	@Override
	protected U call() throws Exception
	{
		task.run();
		T u = task.get();
		return transform(u);
	}

	protected abstract U transform(T v);

	public static <T> TransformTask<Void, T> toVoid(String title, Task<T> task)
	{
		Objects.requireNonNull(title);
		Objects.requireNonNull(task);

		return new TransformTask<Void, T>(task)
		{
			{updateTitle(title);}

			@Override
			protected Void transform(T v) {return null;}
		};
	}

	public static <U, T> TransformTask<U, T> create(String title, Task<T> task, Function<T, U> function)
	{
		Objects.requireNonNull(title);
		Objects.requireNonNull(task);
		Objects.requireNonNull(function);

		return new TransformTask<U, T>(task)
		{
			{
				updateTitle(title);
			}

			@Override
			protected U transform(T v) {return function.apply(v);}
		};
	}
}
