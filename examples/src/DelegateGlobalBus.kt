import io.opencubes.brev.*

class DelegateGlobalBus : IMessageBus by Brev.global {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val delegatedGlobal = DelegateGlobalBus()
      delegatedGlobal.on<MyEvent> {
        println("Hello ${it.value}!")
      }
      delegatedGlobal.once<MyEvent> {
        println("Hello (once) ${it.value}!")
      }
      delegatedGlobal.many<MyEvent>(2) {
        println("Hello (twice) ${it.value}!")
      }
      Brev.emit(MyEvent("World"))
      Brev.emit(MyEvent("Eva"))
      Brev.emit(MyEvent("Bob"))
    }
  }
  class MyEvent(val value: String) : IEvent
}