package privatization;

import java.util.HashSet;
import java.util.IdentityHashMap;

@SuppressWarnings({ "unchecked", "rawtypes" })
class PrivatizationDataStructure {
	private static IdentityHashMap[] data = new IdentityHashMap[10000];

	private static IdentityHashMap privateObjectsContainer() {
		final int threadId = (int) Thread.currentThread().getId();
		IdentityHashMap identityHashMap = data[threadId];
		if (identityHashMap == null) {
			identityHashMap = new IdentityHashMap();
			data[threadId] = identityHashMap;
		}
		return identityHashMap;
	}

	static Object get(final Object o) {
		if (o == null)
			return null;

		final IdentityHashMap objectContainer = privateObjectsContainer();
		final Object obj = objectContainer.get(o);
		if (obj != null)
			return obj;

		Object p = null;

		if (o instanceof Privatizable)
			p = privatizePrivatizable(objectContainer, (Privatizable) o);

		if (o instanceof HashSet)
			p = privatizeHashSet((HashSet) o);

		if (p == null)
			throw new RuntimeException("Couldn't recognize object type " + o.getClass());

		objectContainer.put(o, p);
		return p;
	}

	// Methods that knows how to privatize objects of different classes
	private static <T extends Privatizable<T>> T privatizePrivatizable(final IdentityHashMap objectContainer,
			final Privatizable<T> o) {
		final T p = o.createPrivate();
		objectContainer.put(o, p);
		o.populatePrivate(p);
		return p;
	}

	private static Object privatizeHashSet(final HashSet o) {
		final HashSet x = new HashSet();
		for (final Object object : o)
			x.add(get(object));
		return x;
	}
}
