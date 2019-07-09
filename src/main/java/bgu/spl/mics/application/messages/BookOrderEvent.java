package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {
    private int orderId;
    private  String bookTitle;
    private Customer customer;
    private int orderTick;

    public BookOrderEvent(int orderId, String bookTitle, Customer customer, int orderTick) {
        this.orderId = orderId;
        this.bookTitle = bookTitle;
        this.customer = customer;
        this.orderTick = orderTick;
    }

    public int getOrderId() {return orderId;}

    public String getBookTitle() {return bookTitle;}

    public Customer getCustomer() {return customer;}

    public int getOrderTick() {
        return orderTick;
    }
}
