import io.opencubes.brev.*

object EventStreamExample {
  @JvmStatic
  fun main(args: Array<String>) {
    Brev.stream<MyEvent>()
        .map { it.value.toUpperCase() }
        .forEach {
          println("Greetings $it!")
        }
    Brev.emit(MyEvent("World"))
    Brev.emit(MyEvent("Eva"))
    Brev.emit(MyEvent("Bob"))
  }

  class MyEvent(val value: String) : IEvent
}