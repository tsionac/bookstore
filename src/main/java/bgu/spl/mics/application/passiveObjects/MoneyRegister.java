package bgu.spl.mics.application.passiveObjects;



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	private LinkedList<OrderReceipt> receipts;
	private MoneyRegister() {
		receipts = new LinkedList<>();
	}
	private static class Holder {
		private static final MoneyRegister INSTANCE = new MoneyRegister();
	}


	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {

		return Holder.INSTANCE;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {

		receipts.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int sum =0;
		for(int i =0; i<receipts.size(); i++) {
			sum = sum + (receipts.get(i)).getPrice();
		}
		return sum;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.setAvailableCreditAmount(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(receipts);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			System.out.println("the file was not found");
		}
	}
}
