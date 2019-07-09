package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.

 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<Event<?>, Future> eventF;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> messages;
	private ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>> subMicroToEvent;
	private ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> subMicroToBroad;


	private static class Holder {
		private static final MessageBusImpl INSTANCE = new MessageBusImpl();
	}

	private MessageBusImpl() {
		eventF = new ConcurrentHashMap<>();
		messages = new ConcurrentHashMap<>();
		subMicroToEvent = new ConcurrentHashMap<>();
		subMicroToBroad = new ConcurrentHashMap<>();
	}


	public static MessageBusImpl getInstance() {

		return Holder.INSTANCE;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (type) { //cheap synchronized
			BlockingQueue<MicroService> q = subMicroToEvent.get(type);
			if (q == null) {

				BlockingQueue<MicroService> temp = new LinkedBlockingQueue<>();//because there is a danger in the new operator
				q = temp;
				subMicroToEvent.put(type, q);
			}
			subMicroToEvent.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (type) {
			BlockingQueue<MicroService> q = subMicroToBroad.get(type);
			if (q == null) {
				//cheap synchronized

				BlockingQueue<MicroService> temp = new LinkedBlockingQueue<>();//because there is a danger in the new operator
				q = temp;
				subMicroToBroad.put(type, q);
			}

			subMicroToBroad.get(type).add(m);
		}
	}


	@Override
	public <T> void complete(Event<T> e, T result) {
		eventF.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (subMicroToBroad.size() == 0 || !subMicroToBroad.containsKey(b.getClass()) || subMicroToBroad.get(b.getClass()).isEmpty()) {
			return;
		}
		if (subMicroToBroad.containsKey(b.getClass())) {
			for (MicroService m : subMicroToBroad.get(b.getClass())) {
				if (messages.get(m) != null) {
					messages.get(m).add(b);
				}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		try {
			Future<T> future = new Future<>();
			if (subMicroToEvent.size() == 0 || !subMicroToEvent.containsKey(e.getClass()) || subMicroToEvent.get(e.getClass()).isEmpty()) {
				future.resolve(null);
				return future;
			}
			eventF.put(e, future);
			synchronized (subMicroToEvent.get(e.getClass())) {
				MicroService m = subMicroToEvent.get(e.getClass()).take();
				messages.get(m).add(e);
				subMicroToEvent.get(e.getClass()).add(m);
				return future;
			}
		}catch (InterruptedException e1) {
			}
			return null;
	}

	@Override
	public void register(MicroService m)
	{
		messages.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		messages.get(m).forEach(mess-> {
			if (!eventF.get(mess).isDone()){
				eventF.get(mess).resolve(null);
			}
		});
		messages.remove(m);
		for (ConcurrentHashMap.Entry<Class<? extends Event<?>>, BlockingQueue<MicroService>> e: subMicroToEvent.entrySet()) {
			e.getValue().remove(m);
		}
		for (ConcurrentHashMap.Entry<Class<? extends Broadcast>, BlockingQueue<MicroService>> b: subMicroToBroad.entrySet()) {
			b.getValue().remove(m);
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!messages.containsKey(m))
			throw new IllegalStateException();
		Message ms = messages.get(m).take();

		return ms;
	}

}
