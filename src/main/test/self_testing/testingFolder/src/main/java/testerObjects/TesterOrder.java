package testerObjects;

public class TesterOrder {
	
	private String bookName;
	private int orderTime;
	private int orderPrice;

	public TesterOrder(String book, int time, int price) {
		bookName = book;
		orderTime = time;
		orderPrice = price;
	}

	/**
	 * @return the bookName
	 */
	public String getBookName() {
		return bookName;
	}

	/**
	 * @return the orderTime
	 */
	public int getOrderTime() {
		return orderTime;
	}

	/**
	 * @return the orderPrice
	 */
	public int getOrderPrice() {
		return orderPrice;
	}

}
