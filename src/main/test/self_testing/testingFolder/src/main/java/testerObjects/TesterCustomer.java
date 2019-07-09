package testerObjects;

import java.util.HashMap;


public class TesterCustomer {
	
	private int id;
	private String address;
	private int distance;
	private int creditCard;
	private int availableAmountInCreditCard;
	HashMap<String, TesterOrder> orders;
	
	/**
	 * @param id
	 * @param address
	 * @param distance
	 * @param creditCard
	 * @param availableAmountInCreditCard
	 * @param orders
	 */
	public TesterCustomer(int id, String address, int distance, int creditCard, int availableAmountInCreditCard) {
		this.id = id;
		this.address = address;
		this.distance = distance;
		this.creditCard = creditCard;
		this.availableAmountInCreditCard = availableAmountInCreditCard;
		this.orders = new HashMap<String, TesterOrder>();
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @return the creditCard
	 */
	public int getCreditCard() {
		return creditCard;
	}

	/**
	 * @return the availableAmountInCreditCard
	 */
	public int getAvailableAmountInCreditCard() {
		return availableAmountInCreditCard;
	}

	/**
	 * @return the orders
	 */
	public HashMap<String, TesterOrder> getOrders() {
		return orders;
	}
	
	public void addOrder(String name, TesterOrder order) {
		orders.put(name, order);
	}

}
