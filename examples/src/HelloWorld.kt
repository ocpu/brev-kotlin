import io.opencubes.brev.Brev
import io.opencubes.brev.IEvent

object HelloWorld {
  @JvmStatic fun main(args: Array<String>) {
    Brev.on(MyEvent::class.java) {
      println("Greetings ${it.value}!")
    }
    Brev.emit(MyEvent("World"))
    Brev.emit(MyEvent("Eva"))
    Brev.emit(MyEvent("Bob"))
  }

  class MyEvent(val value: String) : IEvent
}
