import io.opencubes.brev.*

class InstanceBus : Brev() {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val instance = InstanceBus()
      instance.on<MyEvent> {
        println("Hello ${it.value}!")
      }
      instance.once<MyEvent> {
        println("Hello (once) ${it.value}!")
      }
      instance.many<MyEvent>(2) {
        println("Hello (twice) ${it.value}!")
      }
      instance.emit(MyEvent("World"))
      instance.emit(MyEvent("Eva"))
      instance.emit(MyEvent("Bob"))
    }
  }
  class MyEvent(val value: String) : IEvent
}