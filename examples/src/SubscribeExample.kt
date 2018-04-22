import io.opencubes.brev.*

object SubscribeExample {
  @JvmStatic fun main(args: Array<String>) {
//    val subscriptionBus = Brev.createBus()
    val subscriptionBus = Brev
    SubscribeExample::class
        .java
        .methods
        .filter { it.isAnnotationPresent(Subscribe::class.java) }
        .forEach { method ->
          val event = method.parameterTypes[0] as Class<IEvent>
          val limit = method.getAnnotation(Subscribe::class.java).value
          val listener: (IEvent) -> Unit = { method(SubscribeExample, it) }

          subscriptionBus.many(event, limit, listener)
          Unit
        }
    Brev.emit(MyEvent("World"))
    Brev.emit(MyEvent("Eva"))
    Brev.emit(MyEvent("Bob"))
  }

  @Subscribe
  fun greatings(e: MyEvent) {
    println("Greetings ${e.value}!")
  }

  class MyEvent(val value: String) : IEvent
}