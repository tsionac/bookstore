package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireEvent;
import bgu.spl.mics.application.messages.ReleaseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder;
	private CountDownLatch latch;

	public ResourceService(int i, CountDownLatch latch) {
		super("Resource Service " + i);
		this.resourcesHolder = ResourcesHolder.getInstance();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, tb->{
			if(tb.getCurrentTick() == 0){
				terminate();
			}
		});

		subscribeEvent(AcquireEvent.class, Aev->{
			Future<DeliveryVehicle> future = resourcesHolder.acquireVehicle();
			complete(Aev,future);
		});

		subscribeEvent(ReleaseEvent.class, rel->{
			resourcesHolder.releaseVehicle(rel.getCar());
			complete(rel, "successRelease");
		});
		latch.countDown();
	}

}
