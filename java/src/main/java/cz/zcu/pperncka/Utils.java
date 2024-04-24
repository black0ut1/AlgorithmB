package cz.zcu.pperncka;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/** Class with some useful methods. */
public class Utils {
	
	/**
	 * Tries to call invokeAll() method of service, throwing RuntimeException
	 * if method is interrupted.
	 * @param service service which will execute tasks
	 * @param tasks collection of tasks executed on service
	 * @param <T> type of task
	 * @throws RuntimeException if invokeAll() throws InterruptedException
	 */
	public static <T> void tryInvokeAll(ExecutorService service, Collection<? extends Callable<T>> tasks) {
		try {
			service.invokeAll(tasks);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
