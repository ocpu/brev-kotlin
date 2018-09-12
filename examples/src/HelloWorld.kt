import io.opencubes.brev.*

object HelloWorld {
  @JvmStatic fun main(args: Array<String>) {
    Brev.on<MyEvent> {
      println("Hello ${it.value}!")
    }
    Brev.once<MyEvent> {
      println("Hello (once) ${it.value}!")
    }
    Brev.many<MyEvent>(2) {
      println("Hello (twice) ${it.value}!")
    }
    Brev.emit(MyEvent("World"))
    Brev.emit(MyEvent("Eva"))
    Brev.emit(MyEvent("Bob"))
  }

  class MyEvent(val value: String) : IEvent
}
