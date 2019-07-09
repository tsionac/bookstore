package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch latch;

	public LogisticsService(int i, CountDownLatch latch) {
		super("Logistics Service " + i);
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(terminatebroadcastlog.class, terb->{
			Check c =Check.getInstance();
			terminate();
			synchronized (c){
				c.logisticTerm = c.logisticTerm + 1;
				if(c.totalLogistic.intValue()==c.logisticTerm.intValue()){
					sendBroadcast(new TickBroadcast(0));
				}

			}

		});

		subscribeEvent(DeliveryEvent.class, dev->{
			AcquireEvent acquireEvent = new AcquireEvent();
			Future<Future> futureCar1 = sendEvent(acquireEvent);
			if (futureCar1 != null) {
				Future<DeliveryVehicle> futureCar2 = futureCar1.get();
				if (futureCar2 != null) {
					DeliveryVehicle car = futureCar2.get();
					car.deliver(dev.getAddress(), dev.getDistance());
					ReleaseEvent rel = new ReleaseEvent(car);
					Future<String> releaseFuture = sendEvent(rel);
					if (releaseFuture != null) {
						if (releaseFuture.get() != null)
							complete(dev, car);
						else
							complete(dev, null);
					} else
						complete(dev, null);
				} else
					complete(dev, null);
			}
			else
				complete(dev, null);
		});
		latch.countDown();
	}

}
