import io.opencubes.brev.Brev
import io.opencubes.brev.IEvent

object LocalBusExample {
  @JvmStatic fun main(args: Array<String>) {
    val bus = Brev.createBus()

    bus.on(MyEvent::class.java) {
      println("Greetings ${it.value}!")
    }
    bus.emit(MyEvent("World"))
    bus.emit(MyEvent("Eva"))
    bus.emit(MyEvent("Bob"))
  }

  class MyEvent(val value: String) : IEvent
}
