package privatization;

public class ThreadPrivate<T> extends ThreadLocal<T> {

	private static long mainThreadId = Thread.currentThread().getId();
	private T mainThreadObject;
	private boolean mainThreadObjectValueIsSet = false;

	@Override
	final protected T initialValue() {
		final long currentThreadId = Thread.currentThread().getId();
		if (currentThreadId != mainThreadId) {
			if (mainThreadObjectValueIsSet)
				return (T) PrivatizationDataStructure.get(mainThreadObject);
			else
				return initValue();
		} else {
			mainThreadObjectValueIsSet = true;
			return initValue();
		}
	}

	protected T initValue() {
		return null;
	}

	@Override
	final public void set(final T value) {
		final long currentThreadId = Thread.currentThread().getId();
		if (currentThreadId == mainThreadId) {
			mainThreadObjectValueIsSet = true;
			mainThreadObject = value;
		}
		super.set(value);
	};
}
