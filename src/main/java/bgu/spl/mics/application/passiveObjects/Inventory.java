package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {
	private ConcurrentHashMap<String,BookInventoryInfo> inventoryStore = new ConcurrentHashMap<>();

	private static class Holder {
		private static final Inventory INSTANCE = new Inventory();
	}


	private Inventory(){}
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {

		return Holder.INSTANCE;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for(int i = 0; i<inventory.length;i++){

			if (inventoryStore.get(inventory[i].getBookTitle())==null){
				inventoryStore.put(inventory[i].getBookTitle(),inventory[i]);
			}
			else
				inventoryStore.get(inventory[i].getBookTitle()).increaseAmountInInventory();
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		synchronized (inventoryStore.get(book)) {
			if (inventoryStore.get(book).getAmountInInventory() == 0 || inventoryStore.get(book) == null) {
				return OrderResult.NOT_IN_STOCK;
			} else {
				if (inventoryStore.get(book).getAmountInInventory() != 0 && inventoryStore.get(book) != null) {
					inventoryStore.get(book).decreaseAmountInInventory();
					return OrderResult.SUCCESSFULLY_TAKEN;
				} else
					return OrderResult.NOT_IN_STOCK;
			}
		}
	}



	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		if(inventoryStore.get(book)==null|| inventoryStore.get(book).getAmountInInventory()==0)
		return -1;
		else
			return inventoryStore.get(book).getPrice();
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		HashMap<String,Integer> books = new HashMap<>();
		for (ConcurrentHashMap.Entry<String, BookInventoryInfo> e: inventoryStore.entrySet()) {
			books.put(e.getKey(), e.getValue().getAmountInInventory());
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(books);
			out.close();
			fileOut.close();
		} catch (IOException i) {
		    System.out.println("the file was not found");
        }
	}
}


