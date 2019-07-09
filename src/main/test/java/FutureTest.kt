import bgu.spl.mics.Future
import bgu.spl.mics.MessageBusImpl
import bgu.spl.mics.example.messages.ExampleEvent
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.util.concurrent.TimeUnit

import java.util.concurrent.TimeUnit.MILLISECONDS
import org.junit.Assert.*

class FutureTest {

    private var future: Future? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        future = Future()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }

    @Test
    fun testGet() {
        val result = "done"
        future!!.resolve(result)
        assertTrue(future!!.get() != null)
    }

    @Test
    fun testResolve() {
        val result = "done"
        future!!.resolve(result)
        assertTrue(future!!.get() != null)
    }

    @Test
    fun testIsDone() {
        val mBus = MessageBusImpl.getInstance()
        val exe = ExampleEvent("sender")
        val result = "done"
        mBus.complete(exe, result)
        assertTrue(future!!.isDone() === true)
    }

    @Test
    fun testGet1() {
        val result = "done"
        future!!.resolve(result)
        val unit = MILLISECONDS
        assertTrue(future!!.get(1000, unit) != null)
    }
}