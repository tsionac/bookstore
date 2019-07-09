package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        BlockingQueue<Thread> microServicesThreads = new LinkedBlockingQueue<>();
        CountDownLatch latch ;
        Gson gson= new Gson();
        try {
            JsonReader reader1 = new JsonReader(new FileReader(args[0]));
            JsonObject rootObject = gson.fromJson(reader1, JsonObject.class);
            JsonArray booksInInv = rootObject.get("initialInventory").getAsJsonArray();
            JsonObject services = rootObject.get("services").getAsJsonObject();
            int selling = services.get("selling").getAsInt();
            int inventoryService = services.get("inventoryService").getAsInt();
            int logistics = services.get("logistics").getAsInt();
            Check c = Check.getInstance();
            c.totalLogistic = logistics;
            c.logisticTerm = 0;
            int resourcesService = services.get("resourcesService").getAsInt();
            JsonArray customers = services.get("customers").getAsJsonArray();
            int totalService = selling + inventoryService + logistics + resourcesService + customers.size();
            latch = new CountDownLatch(totalService);

            Inventory inventory = Inventory.getInstance();
            BookInventoryInfo[] inventoryStore = new BookInventoryInfo[booksInInv.size()];
            for (int i = 0; i < booksInInv.size(); i++) {
                JsonObject book = (JsonObject) booksInInv.get(i);
                String name = book.get("bookTitle").getAsString();
                int amount = book.get("amount").getAsInt();
                int price = book.get("price").getAsInt();
                inventoryStore[i] = new BookInventoryInfo(name, amount, price);
            }
            inventory.load(inventoryStore);

            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            JsonArray resources = rootObject.get("initialResources").getAsJsonArray();
            JsonObject resource = (JsonObject) resources.get(0);
            JsonArray vehicles = resource.get("vehicles").getAsJsonArray();
            DeliveryVehicle[] vehiclesToStore = new DeliveryVehicle[vehicles.size()];
            for (int j = 0; j < vehicles.size(); j++) {
                JsonObject vehicle = (JsonObject) vehicles.get(j);
                int licence = vehicle.get("license").getAsInt();
                int speed = vehicle.get("speed").getAsInt();
                vehiclesToStore[j] = new DeliveryVehicle(licence, speed);
            }
            resourcesHolder.load(vehiclesToStore);


            for (int i = 1; i <= selling; i++) {
                Thread sellingThread = new Thread(new SellingService(i, latch));
                sellingThread.start();
                microServicesThreads.add(sellingThread);
            }


            for (int i = 1; i <= inventoryService; i++) {
                Thread inventoryThread = new Thread(new InventoryService(i, latch));
                inventoryThread.start();
                microServicesThreads.add(inventoryThread);
            }

            for (int i = 1; i <= logistics; i++) {
                Thread logisticsThread = new Thread(new LogisticsService(i, latch));
                logisticsThread.start();
                microServicesThreads.add(logisticsThread);
            }

            for (int i = 1; i <= resourcesService; i++) {
                Thread resourcesThread = new Thread(new ResourceService(i, latch));
                resourcesThread.start();
                microServicesThreads.add(resourcesThread);
            }

            int orderId = 0;
            Customer[] customersArr = new Customer[customers.size()];
            for (int i = 0; i < customers.size(); i++) {
                JsonObject customer = (JsonObject) customers.get(i);
                int id = customer.get("id").getAsInt();
                String name = customer.get("name").getAsString();
                String address = customer.get("address").getAsString();
                int distance = customer.get("distance").getAsInt();
                JsonObject creditCard = customer.get("creditCard").getAsJsonObject();
                int creditNum = creditCard.get("number").getAsInt();
                int amountRest = creditCard.get("amount").getAsInt();
                JsonArray orderSchedule = customer.get("orderSchedule").getAsJsonArray();
                Vector<Pair> bookOrders = new Vector();
                for (int j = 0; j < orderSchedule.size(); j++) {
                    JsonObject book = (JsonObject) orderSchedule.get(j);
                    String bookTitle = book.get("bookTitle").getAsString();
                    int tick = book.get("tick").getAsInt();
                    bookOrders.add(new Pair(bookTitle, tick));
                }
                customersArr[i] = new Customer(name, id, address, distance, creditNum, amountRest);
                Thread apiThread = new Thread(new APIService(customersArr[i], bookOrders, orderId, i, latch));
                apiThread.start();
                microServicesThreads.add(apiThread);
                orderId = orderId + bookOrders.size();
            }

            JsonObject time = (JsonObject) services.get("time");
            int speed = time.get("speed").getAsInt();
            int duration = time.get("duration").getAsInt();
            TimeService timeService = new TimeService(speed, duration, latch);
            Thread timeThread = new Thread(timeService);
            timeThread.start();
            microServicesThreads.add(timeThread);

            for(int i = 0; i<microServicesThreads.size(); i++){
                try {
                    microServicesThreads.poll().join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            HashMap<Integer, Customer> customersToPrint = new HashMap<>();
            for(int i = 0; i<customersArr.length; i++){
                customersToPrint.put(customersArr[i].getId(),customersArr[i]);
            }

            try {
                FileOutputStream fileOut = new FileOutputStream(args[1]);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(customersToPrint);
                out.close();
                fileOut.close();
            } catch (IOException i) {
                System.out.println("the file was not found");
            }

            inventory.printInventoryToFile(args[2]);

            MoneyRegister moneyRegister = MoneyRegister.getInstance();
            moneyRegister.printOrderReceipts(args[3]);

            try {
                FileOutputStream fileOut = new FileOutputStream(args[4]);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(moneyRegister);
                out.close();
                fileOut.close();
            } catch (IOException i) {
                System.out.println("the file was not found");
            }

        } catch (FileNotFoundException e) { }
    } //end of main

}
