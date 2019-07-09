package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.terminatebroadcastlog;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;
	private CountDownLatch latch;

	public TimeService(int speed, int duration, CountDownLatch latch) {
		super("Time Service");
		this.speed = speed;
		this.duration = duration;
		this.latch = latch;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, tb->{
			if(tb.getCurrentTick() == 0){
				terminate();
			}
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
		}

		for (int i = 1; i <= duration; i++){
			TickBroadcast t = new TickBroadcast(i);
			sendBroadcast(t);

			try{
				sleep(speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		terminatebroadcastlog tend = new terminatebroadcastlog();
		sendBroadcast(tend);
	}

}
