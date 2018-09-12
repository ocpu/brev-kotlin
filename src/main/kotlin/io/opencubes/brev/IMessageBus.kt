package io.opencubes.brev

import java.util.function.Consumer

interface IMessageBus {
  fun <T : IEvent> on(event: Class<out T>, listener: Consumer<T>) =
      on(event, listener::accept)
  fun <T : IEvent> on(event: Class<out T>, listener: (event: T) -> Unit): IMessageBus
  fun <T : IEvent> once(event: Class<out T>, listener: Consumer<T>) =
      once(event, listener::accept)
  fun <T : IEvent> once(event: Class<out T>, listener: (event: T) -> Unit): IMessageBus
  fun <T : IEvent> many(event: Class<out T>, limit: Int, listener: Consumer<T>) =
      many(event, limit, listener::accept)
  fun <T : IEvent> many(event: Class<out T>, limit: Int, listener: (event: T) -> Unit): IMessageBus
  fun <T : IEvent> off(event: Class<out T>, listener: Consumer<T>) =
      off(event, listener::accept)
  fun <T : IEvent> off(event: Class<out T>, listener: (event: T) -> Unit): IMessageBus
  fun <T : IEvent> off(event: Class<out T>, stream: IEventStream<T>): IMessageBus
  fun <T : IEvent> stream(event: Class<out T>): IEventStream<T>
  fun <T : IEvent> emit(event: T)
}
