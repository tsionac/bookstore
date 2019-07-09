package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeliveryEvent implements Event {
    private String address;
    private int distance;
    //private DeliveryVehicle car;

    public DeliveryEvent(String address, int distance) {
        this.address = address;
        this.distance = distance;
        //this.car = car;
    }

    public String getAddress() {
        return address;
    }

    public int getDistance() {
        return distance;
    }

    /*public DeliveryVehicle getCar() {
        return car;
    }*/
}
