package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseEvent implements Event {

    private DeliveryVehicle car;

    public ReleaseEvent(DeliveryVehicle car) {
        this.car = car;
    }

    public DeliveryVehicle getCar() {
        return car;
    }
}
