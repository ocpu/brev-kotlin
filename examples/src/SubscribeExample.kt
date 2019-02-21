import io.opencubes.brev.*

object SubscribeExample {
  @JvmStatic fun main(args: Array<String>) {
//    val subscriptionBus = Brev.createBus()
    val subscriptionBus = Brev
    SubscribeExample::class.java.methods
        .filter { it.isAnnotationPresent(Subscribe::class.java) }
        .forEach { method ->
          subscriptionBus.many(
              method.parameterTypes[0] as Class<IEvent>, // event
              method.getAnnotation(Subscribe::class.java).limit // limit (0 = never remove)
          ) { event -> method.invoke(SubscribeExample, event) } // listener
        }
    Brev.emit(MyEvent("World"))
    Brev.emit(MyEvent("Eva"))
    Brev.emit(MyEvent("Bob"))
  }

  @Subscribe
  fun greetings(e: MyEvent) {
    println("Greetings ${e.value}!")
  }

  class MyEvent(val value: String) : IEvent
}
