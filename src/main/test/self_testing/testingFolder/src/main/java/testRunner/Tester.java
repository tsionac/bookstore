package testRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import testerObjects.TesterCustomer;
import testerObjects.TesterMoneyRegister;
import testerObjects.TesterReceipt;

public class Tester {
	
    private static HashMap<Integer,Customer> customers;
    private static HashMap<String,Integer> inventory;
    private static List<OrderReceipt> receipts;
    private static MoneyRegister moneyRegister;
    private static HashMap<Integer,TesterCustomer> testerCustomers;
    private static HashMap<String,Integer> testerInventory;
    private static TesterMoneyRegister testerMoneyRegister;
    private static String testResults;


	public static void main(String [] args) throws IOException {
		testResults="";
		ExtractProgramOutput(args); //extract the student's serializable program output
		prepareTesterObjects();
		if (testResults.isEmpty())//if we didn't get exceptions regarding the student's serializable output file. 
			testResults = runTests().toString();//send the test number.
		outputTestResults(testResults);
	}


	private static StringBuilder runTests() {
		BasicTests generalTests = new BasicTests(customers, inventory, receipts, moneyRegister, testerCustomers, testerInventory, testerMoneyRegister);
		StringBuilder testResults = generalTests.runTests();
		return testResults;
	}

	private static void outputTestResults(String testResults) throws IOException {
		Files.write(Paths.get("test_result.txt"), testResults.getBytes());
	}
	
	
	private static void ExtractProgramOutput(String[] srializedObjects) {
		  int testNum = Integer.parseInt(srializedObjects[5]);
		  try {
			 getCustomers(srializedObjects[1]);
			 getInventory(srializedObjects[2]);
			 getReceipts(srializedObjects[3]);
			 getMoneyRegister(srializedObjects[4]);
		  } catch (Exception e) {
			  testResults="################  Could not open the serialized output file for test "+testNum+"!!";
		     e.printStackTrace();
		  }
	}

	private static void getMoneyRegister(String moneyRegisterObj)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fileIn = new FileInputStream(moneyRegisterObj);
		 ObjectInputStream in = new ObjectInputStream(fileIn);
		 moneyRegister = (MoneyRegister) in.readObject();
		 in.close();
		 fileIn.close();
	}

	private static void getReceipts(String receiptsObj)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fileIn = new FileInputStream(receiptsObj);
		 ObjectInputStream in = new ObjectInputStream(fileIn);
		 receipts = (List<OrderReceipt>) in.readObject();
		 in.close();
		 fileIn.close();
	}

	private static void getInventory(String inventoryObj)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fileIn = new FileInputStream(inventoryObj);
		 ObjectInputStream in = new ObjectInputStream(fileIn);
		 inventory = (HashMap<String,Integer>) in.readObject();
		 in.close();
		 fileIn.close();
	}
	

	private static void prepareTesterObjects() {
		prepareTestCustomers();
		prepareTestInventory();
		prepareTestMoneyReg();
		
	}


	private static void prepareTestMoneyReg() {
		testerMoneyRegister = new TesterMoneyRegister();
		testerMoneyRegister.addReceiptsPerStudent(new TesterReceipt(234567891, "The Hunger Games", 102, 12));
		testerMoneyRegister.setTotalIncome(102);
	}


	private static void prepareTestInventory() {
		testerInventory = new HashMap<String,Integer>();
		testerInventory.put("Harry Poter", 10);
		testerInventory.put("The Hunger Games", 89);
	}


	private static void prepareTestCustomers() {
		testerCustomers = new HashMap<Integer,TesterCustomer>();
		TesterCustomer bruria = new TesterCustomer(123456789, "NewYork 123", 33, 67890, 88);
		testerCustomers.put(123456789, bruria);
		TesterCustomer shraga = new TesterCustomer(234567891, "BeerSheva 3333", 12, 453536, 220-102);
		testerCustomers.put(234567891, shraga);
	}

	private static void getCustomers(String customersObj)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		 FileInputStream fileIn = new FileInputStream(customersObj);
		 ObjectInputStream in = new ObjectInputStream(fileIn);
		 customers = (HashMap<Integer,Customer>) in.readObject();
		 in.close();
		 fileIn.close();
	}

}
