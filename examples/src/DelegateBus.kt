import io.opencubes.brev.*

class DelegateBus : IMessageBus by Brev.createBus() {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val delegated = DelegateBus()
      delegated.on<MyEvent> {
        println("Hello ${it.value}!")
      }
      delegated.once<MyEvent> {
        println("Hello (once) ${it.value}!")
      }
      delegated.many<MyEvent>(2) {
        println("Hello (twice) ${it.value}!")
      }
      delegated.emit(MyEvent("World"))
      delegated.emit(MyEvent("Eva"))
      delegated.emit(MyEvent("Bob"))
    }
  }
  class MyEvent(val value: String) : IEvent
}