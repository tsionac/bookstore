package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder implements Serializable {

	private BlockingQueue<DeliveryVehicle> cars = new LinkedBlockingQueue<>();

	private ResourcesHolder() {}
	private static class Holder {
		private static final ResourcesHolder INSTANCE = new ResourcesHolder();
	}


	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {

		return Holder.INSTANCE;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		DeliveryVehicle car = cars.peek();
		try {
			car = cars.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Future<DeliveryVehicle> future = new Future<>();
		future.resolve(car);
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		cars.add(vehicle);
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for(int i = 0; i<vehicles.length;i++){
			cars.add(vehicles[i]);
		}
	}

}
