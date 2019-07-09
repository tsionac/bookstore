import bgu.spl.mics.application.passiveObjects.BookInventoryInfo
import bgu.spl.mics.application.passiveObjects.Inventory

import org.junit.Assert.*

import bgu.spl.mics.application.passiveObjects.OrderResult
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.util.HashMap


class InventoryTest {
    private var inv: Inventory? = null
    private var book: BookInventoryInfo? = null
    private var inventory: HashMap<String, BookInventoryInfo>? = null
    private var inventoryToAdd: Array<BookInventoryInfo>? = null

    @Before
    fun setUp() {
        inv = inv!!.getInstance()
        book = BookInventoryInfo("The Jungle Book", 20, 50)
        inventory = HashMap<String, BookInventoryInfo>()
        inventoryToAdd = arrayOfNulls<BookInventoryInfo>(2)
        inventoryToAdd[0] = BookInventoryInfo("Harry Potter", 15, 30)
        inventoryToAdd[1] = book
    }

    @After
    fun tearDown() {
        inv = null
        book = null
        inventory = null
    }

    @Test
    fun testGetInstance() {
        val inv1: Inventory
        inv1 = inv!!.getInstance()
        assertTrue(inv!!.getClass() === inv1.getClass())
    }

    @Test
    fun testLoad() {
        inv!!.load(inventoryToAdd)
        assertTrue(inventory!!.containsKey("Harry Potter"))
        assertTrue(inventory!!.containsKey("The Jungle Book"))
    }

    @Test
    fun testTake() {
        assertEquals(OrderResult.NOT_IN_STOCK, inv!!.take(book!!.getBookTitle()))
        inventory!!.put(book!!.getBookTitle(), book)
        assertEquals(20, book!!.getAmountInInventory())
        inv!!.take(book!!.getBookTitle())
        assertEquals(19, book!!.getAmountInInventory())
    }

    @Test
    fun testCheckAvailabiltyAndGetPrice() {
        assertEquals(-1, inv!!.checkAvailabiltyAndGetPrice(book!!.getBookTitle()))
        inventory!!.put(book!!.getBookTitle(), book)
        assertEquals(50, inv!!.checkAvailabiltyAndGetPrice(book!!.getBookTitle()))
    }

}
