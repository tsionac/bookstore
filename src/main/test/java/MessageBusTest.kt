import bgu.spl.mics.*
import bgu.spl.mics.example.messages.ExampleBroadcast
import bgu.spl.mics.example.messages.ExampleEvent
import bgu.spl.mics.example.services.ExampleMessageSenderService
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class MessageBusTest {

    private var msbi: MessageBusImpl? = null
    private var exe: ExampleEvent? = null
    private var m: MicroService? = null
    private var args: Array<String>? = null
    private var arg: Array<String>? = null
    private var exb: ExampleBroadcast? = null
    private var ms: MicroService? = null

    @Before
    fun setUp() {
        msbi = MessageBusImpl.getInstance()
        exe = ExampleEvent("sender")
        args = arrayOfNulls(1)
        args[0] = "event"
        m = ExampleMessageSenderService("event handler", args)
        arg = arrayOfNulls(1)
        arg[0] = "broadcast"
        exb = ExampleBroadcast("sender")
        ms = ExampleMessageSenderService("broadcast handler", arg)
    }

    @After
    fun tearDown() {
        exe = null
        args = null
        m = null
    }

    @Test
    fun <T> testSubscribeEvent() {
        msbi!!.register(m)
        msbi!!.subscribeEvent(exe!!.getClass(), m)
        assertTrue(msbi!!.sendEvent(exe) != null)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testSubscribeBroadcast() {
        msbi!!.register(ms)
        msbi!!.subscribeBroadcast(exb!!.getClass(), ms)
        msbi!!.sendBroadcast(exb)
        try {

            assertTrue(msbi!!.awaitMessage(ms) != null)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    @Test
    fun <T> testComplete() {
        msbi!!.register(m)
        msbi!!.subscribeEvent(exe!!.getClass(), m)
        val future = msbi!!.sendEvent(exe)
        val result = "done"
        msbi!!.complete(exe, result)
        assertTrue(future.isDone() === true)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testSendBroadcast() {
        msbi!!.register(ms)
        msbi!!.subscribeBroadcast(exb!!.getClass(), ms)
        msbi!!.sendBroadcast(exb)
        assertTrue(msbi!!.awaitMessage(ms) != null)
    }

    @Test
    fun <T> testSendEvent() {
        msbi!!.register(m)
        msbi!!.subscribeEvent(exe!!.getClass(), m)
        val future = msbi!!.sendEvent(exe)
        assertTrue(future != null)
    }

    @Test
    fun testRegister() {
        msbi!!.register(m)
        msbi!!.subscribeEvent(exe!!.getClass(), m)
        assertTrue(msbi!!.sendEvent(exe) != null)
    }

    @Test
    fun testUnregister() {
        msbi!!.register(m)
        msbi!!.unregister(m)
        assertTrue(msbi!!.sendEvent(exe) == null)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testAwaitMessage() {
        msbi!!.register(m)
        msbi!!.subscribeEvent(exe!!.getClass(), m)
        assertTrue(msbi!!.awaitMessage(m) != null)
    }
}