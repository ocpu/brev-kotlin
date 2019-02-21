package io.opencubes.brev

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Suppress("MemberVisibilityCanBePrivate", "USELESS_CAST")
internal class ExtensionsTest {

  lateinit var bus: Brev
  val mock1 = Mock()

  @BeforeEach
  fun setUpEach() {
    bus = Brev.createBus()
    mock1.clear()
  }

  fun mock1fn(event: IEvent) = mock1.memorize(event)
  val e = MyEvent("limit")

  @Test
  fun `on works as usual`() {
    bus.on<MyEvent>(::mock1fn)
    bus.emit(e)
    assert(mock1.calls.size == 1) {
      if (mock1.calls.isEmpty()) "The mock function was not called"
      else "The mock function was not called once but ${mock1.calls.size} times"
    }
    assert(mock1.calls[0].size == 1) {
      if (mock1.calls[0].isEmpty()) "The mock function was called with no parameters"
      else "The mock function was called with ${mock1.calls[0].size} parameters expected 1"
    }
    assert(mock1.calls[0][0] == e) { "The emit function transformed the event limit" }
    assert(bus.listeners.containsKey(MyEvent::class.java as Class<*>)) {
      "The event has no functions"
    }
    assert(bus.listeners[MyEvent::class.java as Class<*>]!!.size == 1) {
      if (bus.listeners[MyEvent::class.java as Class<*>]!!.isEmpty()) "There are no listener entries"
      else "There are more listeners than expected (${bus.listeners[MyEvent::class.java as Class<*>]!!.size})"
    }
    assert(bus.listeners[MyEvent::class.java as Class<*>]!![0].left == 0) {
      "The listener is not listening endlessly"
    }
    assert(bus.listeners[MyEvent::class.java as Class<*>]!![0].listener == ::mock1fn) {
      "The listener is not the one specified?"
    }
  }

  @Test
  fun `once works as usual`() {
    bus.once<MyEvent>(::mock1fn)
    bus.emit(e)
    assert(mock1.calls.size == 1) {
      if (mock1.calls.isEmpty()) "The mock function was not called"
      else "The mock function was not called once but ${mock1.calls.size} times"
    }
    assert(mock1.calls[0].size == 1) {
      if (mock1.calls[0].isEmpty()) "The mock function was called with no parameters"
      else "The mock function was called with ${mock1.calls[0].size} parameters expected 1"
    }
    assert(mock1.calls[0][0] == e) { "The emit function transformed the event limit" }
    assert(bus.listeners.containsKey(MyEvent::class.java as Class<*>)) {
      "The event has no functions"
    }
    assert(bus.listeners[MyEvent::class.java as Class<*>]!!.size == 0) {
      "There are ${bus.listeners[MyEvent::class.java as Class<*>]!!.size} listener left expected 0"
    }
  }

  @Test
  fun `many works as usual`() {
    bus.many<MyEvent>(2, ::mock1fn)
    bus.emit(e)
    bus.emit(e)
    assert(mock1.calls.size == 2) {
      if (mock1.calls.isEmpty()) "The mock function was not called"
      else "The mock function was not called twice but ${mock1.calls.size}" +
        " ${if (mock1.calls.size == 1) "time" else "times"}"
    }
    assert(mock1.calls[0].size == 1 && mock1.calls[1].size == 1) {
      if (mock1.calls[0].isEmpty()) "The mock function was called with no parameters"
      else "The mock function was called with ${mock1.calls[0].size} and " +
        "${mock1.calls[1].size} parameters expected 1 and 1"
    }
    assert(mock1.calls[0][0] == e && mock1.calls[1][0] == e) { "The emit function transformed the event limit" }
    assert(bus.listeners.containsKey(MyEvent::class.java as Class<*>)) {
      "The event has no functions"
    }
    assert(bus.listeners[MyEvent::class.java as Class<*>]!!.size == 0) {
      "There are ${bus.listeners[MyEvent::class.java as Class<*>]!!.size} listener left expected 0"
    }
  }

  @Test
  fun `off with functions works as usual`() {
    bus.on<MyEvent>(::mock1fn)
    bus.emit(e)
    bus.off<MyEvent>(::mock1fn)
    assert(mock1.calls.size == 1) {
      if (mock1.calls.isEmpty()) "The mock function was not called"
      else "The mock function was not called once but ${mock1.calls.size} times"
    }
    assert(mock1.calls[0].size == 1) {
      if (mock1.calls[0].isEmpty()) "The mock function was called with no parameters"
      else "The mock function was called with ${mock1.calls[0].size} parameters expected 1"
    }
    assert(mock1.calls[0][0] == e) { "The emit function transformed the event limit" }
    assert(bus.listeners.containsKey(MyEvent::class.java as Class<*>)) {
      "The event has no functions"
    }
    assert(bus.listeners[MyEvent::class.java as Class<*>]!!.size == 0) {
      "There are ${bus.listeners[MyEvent::class.java as Class<*>]!!.size} listener left expected 0"
    }
  }

  @Test
  fun `off with stream works as usual`() {
    val stream = bus.stream<MyEvent>()
    stream.forEach(::mock1fn)
    bus.emit(e)
    bus.off(stream)
    assert(mock1.calls.size == 1) {
      if (mock1.calls.isEmpty()) "The mock function was not called"
      else "The mock function was not called once but ${mock1.calls.size} times"
    }
    assert(mock1.calls[0].size == 1) {
      if (mock1.calls[0].isEmpty()) "The mock function was called with no parameters"
      else "The mock function was called with ${mock1.calls[0].size} parameters expected 1"
    }
    assert(mock1.calls[0][0] == e) { "The emit function transformed the event limit" }
  }

  class MyEvent(val value: Any) : IEvent
}
