package io.opencubes.brev

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Suppress("MemberVisibilityCanBePrivate")
internal class EventStreamTest {

  lateinit var bus: Brev
  val mock1 = Mock()
  val mock2 = Mock()

  @BeforeEach
  fun setUpEach() {
    bus = Brev.createBus()
    mock1.clear()
    mock2.clear()
  }

  val event = MyEvent::class.java
  val e = MyEvent("value")

  fun collector(any: Any) {
    mock1.memorize(any)
  }

  @Test
  fun `The event stream gets events`() {
    val stream = bus.stream(event)
    stream.forEach(::collector)
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
  }

  @Test
  fun `The stream can transform the event with the map function`() {
    val stream = bus.stream(event)
    stream
      .map(MyEvent::value)
      .forEach(::collector)
    bus.emit(e)
    assert(mock1.calls.size == 1) {
      if (mock1.calls.isEmpty()) "The mock function was not called"
      else "The mock function was not called once but ${mock1.calls.size} times"
    }
    assert(mock1.calls[0].size == 1) {
      if (mock1.calls[0].isEmpty()) "The mock function was called with no parameters"
      else "The mock function was called with ${mock1.calls[0].size} parameters expected 1"
    }
    assert(mock1.calls[0][0] == e.value) { "The emit function transformed the event limit" }
  }

  fun aFilter(event: MyEvent) = "a" in event.value

  @Test
  fun `The stream can filter away events it does not care about`() {
    val stream = bus.stream(event)
    stream
      .filter(::aFilter)
      .forEach(::collector)
    bus.emit(e)
    bus.emit(MyEvent("there is no x"))
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

  @Test
  fun `The stream can take a look at the values and pass them on`() {
    val stream = bus.stream(event)
    var peeked = false
    stream
      .peek { peeked = true }
      .forEach(::collector)
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
    assert(peeked) { "The stream could not peek at the limit" }
  }

  @Test
  fun `The stream can skip the first n values in the stream`() {
    val stream = bus.stream(event)
    val n = 5
    stream
      .skip(n)
      .forEach(::collector)
    for (i in 0 until n + 1)
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
  }

  fun mapToValue(myOtherEvent: MyOtherEvent) = myOtherEvent.value
  fun mapToValue(myEvent: MyEvent) = myEvent.value

  @Test
  fun `The stream can combine a different stream with itself to create a super stream`() {
    val stream1 = bus.stream(MyEvent::class.java)
    val stream2 = bus.stream(MyOtherEvent::class.java)

    stream1
      .map(::mapToValue)
      .combine(
        stream2
          .map(::mapToValue)
      )
      .forEach(::collector)
    bus.emit(MyEvent("super"))
    bus.emit(MyOtherEvent("event"))
    assert(mock1.calls.size == 2) {
      if (mock1.calls.isEmpty()) "The mock function was not called"
      else "The mock function was not called twice but ${mock1.calls.size} times"
    }
    assert(mock1.calls[0].size == 1 && mock1.calls[1].size == 1) {
      "The mock function was called with ${mock1.calls[0].size} and " +
        "${mock1.calls[1].size} parameters expected 1 and 1"
    }
    assert(mock1.calls[0][0] == "super" && mock1.calls[1][0] == "event") {
      "The emit function transformed the event limit"
    }
  }

  @Test
  fun `The stream can cancel the receiving of events by calling off`() {
    val stream = bus.stream(event)
    stream.forEach(::collector)
    bus.emit(e)
    bus.off(event, stream)
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

  class MyEvent(val value: String) : IEvent
  class MyOtherEvent(val value: String) : IEvent
}
