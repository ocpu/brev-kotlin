package io.opencubes.brev

import java.util.function.Consumer

/**
 * A basic event bus
 */
interface IMessageBus {
  /**Register a [listener] on a [event]. */
  fun <T : IEvent> on(event: Class<out T>, listener: (event: T) -> Unit): IMessageBus
  /**Register a [listener] on a [event]. */
  fun <T : IEvent> on(event: Class<out T>, listener: Consumer<T>) =
      on(event, listener::accept)

  /**Register a [listener] on a [event] and only executes once. */
  fun <T : IEvent> once(event: Class<out T>, listener: (event: T) -> Unit): IMessageBus
  /**Register a [listener] on a [event] and only executes once. */
  fun <T : IEvent> once(event: Class<out T>, listener: Consumer<T>) =
      once(event, listener::accept)

  /**Register a [listener] on a [event] and only executes [limit]. */
  fun <T : IEvent> many(event: Class<out T>, limit: Int, listener: (event: T) -> Unit): IMessageBus
  /**Register a [listener] on a [event] and only executes [limit]. */
  fun <T : IEvent> many(event: Class<out T>, limit: Int, listener: Consumer<T>) =
      many(event, limit, listener::accept)

  /**Removes a [listener] from a [event]. */
  fun <T : IEvent> off(event: Class<out T>, listener: (event: T) -> Unit): IMessageBus
  /**Removes a [listener] from a [event]. */
  fun <T : IEvent> off(event: Class<out T>, listener: Consumer<T>) =
      off(event, listener::accept)

  /**Removes a [stream] from a [event]. */
  fun <T : IEvent> off(event: Class<out T>, stream: IEventStream<T>): IMessageBus

  /**
   * Registers a new [IEventStream] on the [event].
   * @return The event stream
   */
  fun <T : IEvent> stream(event: Class<out T>): IEventStream<T>
  /**Execute all listeners/streams for event [T] with value [event]. */
  fun <T : IEvent> emit(event: T)
}
