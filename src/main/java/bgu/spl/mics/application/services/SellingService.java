package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister mr;
	private int currentTick;
	private CountDownLatch latch;

	public SellingService(int i, CountDownLatch latch) {
		super("Selling Service " + i);
		this.mr = MoneyRegister.getInstance();
		this.currentTick = 0;
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tb->{
			if(tb.getCurrentTick() == 0){
				terminate();
			}
			else
				currentTick = tb.getCurrentTick();
		});

		subscribeEvent(BookOrderEvent.class, ev->{
			int processTick = currentTick;
			CheckAvailabilityEvent cae = new CheckAvailabilityEvent(ev.getBookTitle());
			Future<Integer> future = sendEvent(cae);
			if (future != null) {
				Integer price = future.get();
				if (future.get() != null) {
					if (price.intValue() != -1) {
						synchronized (ev.getCustomer()) {
							if (ev.getCustomer().getAvailableCreditAmount() >= price.intValue()) {
								TakeBookEvent tev = new TakeBookEvent(ev.getBookTitle());
								Future<OrderResult> future2 = sendEvent(tev);
								if (future2 != null) {
									OrderResult or = future2.get();
									if (or == OrderResult.SUCCESSFULLY_TAKEN) {
										mr.chargeCreditCard(ev.getCustomer(), price.intValue());
										OrderReceipt receipt = new OrderReceipt(ev.getCustomer().getName(), ev.getCustomer().getId(), ev.getBookTitle(), price.intValue(), currentTick, ev.getOrderTick(), processTick, ev.getOrderId());
										mr.file(receipt);
										DeliveryEvent dev = new DeliveryEvent(ev.getCustomer().getAddress(), ev.getCustomer().getDistance());
										sendEvent(dev);
										complete(ev, receipt);
									} else
										complete(ev, null);
								} else
									complete(ev, null);
							} else
								complete(ev, null);
						}
					} else
						complete(ev, null);
				}
				else
					complete(ev, null);
			}
			else
				complete(ev,null);
		});
		latch.countDown();
	}

}
