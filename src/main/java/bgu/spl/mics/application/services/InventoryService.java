package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inv;
	private CountDownLatch latch;

	public InventoryService(int i, CountDownLatch latch) {
		super("Inventory Service " + i);
		this.inv = Inventory.getInstance();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tb->{
			if(tb.getCurrentTick() == 0){
				terminate();
			}
		});

		subscribeEvent(CheckAvailabilityEvent.class, ev->{
			int price = inv.checkAvailabiltyAndGetPrice(ev.getBookTitle());
			complete(ev, price);
		});

		subscribeEvent(TakeBookEvent.class, tev->{
			OrderResult or = inv.take(tev.getBookTitle());
			complete(tev, or);
		});
		latch.countDown();
	}

}
