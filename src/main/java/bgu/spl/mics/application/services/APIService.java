package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private ConcurrentHashMap<Integer, BlockingQueue<String>> booksOrders;
	private Customer c;
	private int currentTick;
	private int orderId;
	private CountDownLatch latch;

	public APIService(Customer c, Vector<Pair> bookOrders,int orderId, int i, CountDownLatch latch) {
		super("API Service " + i);
		this.c = c;
		this.booksOrders = convertToMap(bookOrders);
		this.currentTick = 0;
		this.orderId = orderId;
		this.latch = latch;
	}

	private ConcurrentHashMap<Integer, BlockingQueue<String>> convertToMap(Vector<Pair> bookOrders){
		ConcurrentHashMap<Integer, BlockingQueue<String>> booksByTick = new ConcurrentHashMap<>();
		for(int j = 0; j < bookOrders.size(); j++){
			if(!booksByTick.containsKey(bookOrders.get(j).getSecond()))
				booksByTick.put(bookOrders.get(j).getSecond(), new LinkedBlockingQueue<>());
			booksByTick.get(bookOrders.get(j).getSecond()).add(bookOrders.get(j).getFirst());
		}

		return booksByTick;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tb->{
			currentTick = tb.getCurrentTick();
			if(tb.getCurrentTick() == 0) {
				terminate();
			}
			else {
				BookOrderEvent[] events;
				if (booksOrders.get(currentTick) != null)
					events = new BookOrderEvent[booksOrders.get(currentTick).size()];
				else
					events = new BookOrderEvent[0];
				Future<OrderReceipt> futures[] = new Future[events.length];
				if (booksOrders.containsKey(currentTick)) {
					for (int i = 0; i < events.length; i++) {
						String bookTitle = booksOrders.get(currentTick).poll();
						events[i] = new BookOrderEvent(orderId, bookTitle, c, currentTick);
						this.orderId++;
						futures[i] = sendEvent(events[i]);
						if (futures[i].get() != null) {
							c.getCustomerReceiptList().add(futures[i].get());
						}
					}
				}
			}
		});
		latch.countDown();

		
	}

}
