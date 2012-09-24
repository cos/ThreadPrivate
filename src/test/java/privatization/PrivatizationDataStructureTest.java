package privatization;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class PrivatizationDataStructureTest {

	@Before
	public void setUp() throws Exception {
	}

	static class PrivatizableObject implements Privatizable<PrivatizableObject> {

		public int x;
		public int y;

		public PrivatizableObject(final int i) {
			this.x = i;
			final Random random = new Random();
			this.y = random.nextInt();
		}

		@Override
		public PrivatizableObject createPrivate() {
			return new PrivatizableObject(this.x);
		}

		@Override
		public void populatePrivate(final PrivatizableObject t) {
			t.y = this.y;
		}

		@Override
		public boolean equals(final Object obj) {
			final PrivatizableObject o = (PrivatizableObject) obj;
			return ((o.x == this.x) && (o.y == this.y));
		}

	}

	static class RunnableForTest implements Runnable {

		private final Privatizable<?> fromMainThread;

		public RunnableForTest(final PrivatizableObject fromMainThread) {
			this.fromMainThread = fromMainThread;
		}

		@Override
		public void run() {
			final int threadId = (int) Thread.currentThread().getId();
			final PrivatizableObject fromThread = (PrivatizableObject) PrivatizationDataStructure.get(fromMainThread);
			System.out.println(threadId + "");
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
			}
			// different references;
			assertTrue(fromMainThread != fromThread);
			// equal semantically;
			assertEquals(fromMainThread, fromThread);
			fromThread.x = threadId;
			// now they definitely must not be equal semantically
			assertFalse(fromMainThread.equals(fromThread));
		}
	};

	@Test
	public void testSimple() throws Exception {
		final PrivatizableObject obj = new PrivatizableObject(42);
		final Thread thread = new Thread(new RunnableForTest(obj));
		thread.start();
	}

	@Test
	public void testConcurrentAccess() {
		final PrivatizableObject obj = new PrivatizableObject((int) Thread.currentThread().getId());
		final Thread[] threads = new Thread[42];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new RunnableForTest(obj));
		}

		for (final Thread t : threads)
			t.start();
	}

}
