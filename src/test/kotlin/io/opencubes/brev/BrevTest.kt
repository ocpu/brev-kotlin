package io.opencubes.brev

import org.junit.jupiter.api.*

@Suppress("MemberVisibilityCanBePrivate")
internal class BrevTest {

  lateinit var bus: Brev
  val mock1 = Mock()
  val mock2 = Mock()

  @BeforeEach
  fun setUpEach() {
    bus = Brev.createBus()
    mock1.clear()
    mock2.clear()
  }

  val mock1fn = mock1.fn<(MyEvent) -> Unit>()
  val mock2fn = mock2.fn<(MyEvent) -> Unit>()
  val event = MyEvent::class.java as Class<out IEvent>
  val e = MyEvent("value")

  @Test
  fun `registers a function`() {
    assert(bus.listeners.isEmpty()) { "Listeners has a initial entries" }
    assert(bus.streams.isEmpty()) { "Streams has a initial entries" }
    bus.on(MyEvent::class.java, mock1fn)
    assert(bus.listeners.size == 1) {
      if (bus.listeners.size > 1) "More than one event was added"
      else "Nothing happened"
    }
    assert(event in bus.listeners) { "The right event was not added" }
    assert(bus.listeners[event]!!.size == 1) {
      if (bus.listeners[event]!!.size > 1) "More than one listener was added"
      else "Nothing happened"
    }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `unregisters a function`() {
    `registers a function`()
    bus.off(MyEvent::class.java, mock1fn)
    assert(bus.listeners[event]!!.isEmpty()) { "Failed to unregister function" }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `unable to register same function`() {
    `registers a function`()
    bus.on(MyEvent::class.java, mock1fn)
    assert(bus.listeners[event]!!.size == 1) {
      if (bus.listeners[event]!!.size > 1) "The listener was added twice"
      else "Something removed the listener"
    }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `removes nothing if the function is not registered`() {
    `registers a function`()
    bus.off(MyEvent::class.java, mock2fn)
    assert(bus.listeners.size == 1) {
      if (bus.listeners.size > 1) "One more event was added"
      else "mock 1 was removed"
    }
    assert(bus.listeners[event]!!.size == 1) {
      if (bus.listeners[event]!!.size > 1)
        "Listener was added"
      else "Listeners was removed"
    }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `removes nothing if the event is not registered`() {
    assert(bus.listeners.isEmpty()) { "Listeners has a initial entries" }
    assert(bus.streams.isEmpty()) { "Streams has a initial entries" }
    bus.off(MyEvent::class.java, mock1fn)
    assert(bus.listeners.isEmpty()) { "Something was added to listeners when it shouldn't" }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `triggers listener`() {
    bus.on(MyEvent::class.java, mock1fn)
    bus.emit(e)
    assert(mock1.calls.size == 1) { "mock 1 was not called" }
    assert(mock1.calls[0][0] as MyEvent == e) { "mock 1 called but not with the event" }
  }

  @Test
  fun `does nothing if the event does not exist`() {
    bus.emit(e)
  }

  @Test
  fun `once registers a function`() {
    assert(bus.listeners.isEmpty()) { "Listeners has a initial entries" }
    assert(bus.streams.isEmpty()) { "Streams has a initial entries" }
    bus.once(MyEvent::class.java, mock1fn)
    assert(bus.listeners.size == 1) {
      if (bus.listeners.size > 1) "More than one event was added"
      else "Nothing happened"
    }
    assert(event in bus.listeners) { "The right event was not added" }
    assert(bus.listeners[event]!!.size == 1) {
      if (bus.listeners[event]!!.size > 1) "More than one listener was added"
      else "Nothing happened"
    }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `once executes with function registered`() {
    bus.once(MyEvent::class.java, mock1fn)
    bus.emit(e)
    assert(mock1.calls.size == 1) { "mock 1 was not called" }
    assert(mock1.calls[0][0] as MyEvent == e) { "mock 1 called but not with the event" }
    assert(bus.listeners[event]!!.isEmpty()) { "Failed to unregister function" }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `many registers a function`() {
    assert(bus.listeners.isEmpty()) { "Listeners has a initial entries" }
    assert(bus.streams.isEmpty()) { "Streams has a initial entries" }
    bus.many(MyEvent::class.java, 0, mock1fn)
    assert(bus.listeners.size == 1) {
      if (bus.listeners.size > 1) "More than one event was added"
      else "Nothing happened"
    }
    assert(event in bus.listeners) { "The right event was not added" }
    assert(bus.listeners[event]!!.size == 1) {
      if (bus.listeners[event]!!.size > 1) "More than one listener was added"
      else "Nothing happened"
    }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `many executes with function registered`() {
    bus.many(MyEvent::class.java, 3, mock1fn)
    bus.emit(e)
    bus.emit(e)
    bus.emit(e)
    assert(mock1.calls.size == 3) { "mock 1 was not called 3 times" }
    assert(mock1.calls[0][0] as MyEvent == e) { "mock 1 called but not with the event" }
    assert(mock1.calls[1][0] as MyEvent == e) { "mock 1 called but not with the event" }
    assert(mock1.calls[2][0] as MyEvent == e) { "mock 1 called but not with the event" }
    assert(bus.listeners[event]!!.isEmpty()) { "Failed to unregister function" }
    assert(bus.streams.isEmpty()) { "Something was added to streams when it shouldn't" }
  }

  @Test
  fun `many only takes signed values`() {
    assertThrows<Exception> {
      bus.many(MyEvent::class.java, -1, mock1fn)
    }
  }

  @Test
  fun `global or local event bus`() {
    assert(Brev.globalBus) { "The supposed global bus is not?" }
    assert(Brev.global.globalBus) { "The supposed global bus is not?" }
    assert(!bus.globalBus) { "The local bus is global?" }
  }

  class Mock {
    private val _calls = mutableListOf<Array<out Any>>()
    private var returns: Any = Unit

    val calls: List<Array<out Any>> get() = _calls

    operator fun invoke(vararg args: Any): Any {
      _calls += args
      return returns
    }

    fun clear() {
      _calls.clear()
    }

    inline fun <reified T: Any> fn(): T = this::invoke as T
  }

  class MyEvent(val value: Any) : IEvent
}
