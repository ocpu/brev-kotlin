import io.opencubes.brev.*

object LocalBusExample {
  @JvmStatic fun main(args: Array<String>) {
    val bus = Brev.createBus()

    bus.on<MyEvent> {
      println("Greetings ${it.value}!")
    }
    bus.once<MyEvent> {
      println("Hello (once) ${it.value}!")
    }
    bus.many<MyEvent>(2) {
      println("Hello (twice) ${it.value}!")
    }
    bus.emit(MyEvent("World"))
    bus.emit(MyEvent("Eva"))
    bus.emit(MyEvent("Bob"))
  }

  class MyEvent(val value: String) : IEvent
}
