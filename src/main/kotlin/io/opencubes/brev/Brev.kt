package io.opencubes.brev

import java.util.*
import java.util.function.*
import java.util.function.Function


/**A simple implementation of [IMessageBus] and as a whole a event bus implementation. */
@Suppress("UNCHECKED_CAST")
open class Brev private constructor(
    /**A boolean to see if the bus is the global one. */
    val globalBus: Boolean
) : IMessageBus<IEvent> {
  /**Create a new event bus. */
  constructor() : this(false)

  internal val listeners = mutableMapOf<Class<IEvent>, MutableList<Entry>>()
  internal val streams = mutableMapOf<IEventStream<*>, (IEvent) -> Unit>()

  override fun <T : IEvent> on(event: Class<out T>, listener: (T) -> Unit): IMessageBus<IEvent> =
      many(event, 0, listener)

  override fun <T : IEvent> once(event: Class<out T>, listener: (T) -> Unit): IMessageBus<IEvent> =
      many(event, 1, listener)

  override fun <T : IEvent> many(event: Class<out T>, limit: Int, listener: (T) -> Unit): IMessageBus<IEvent> {
    if (limit < 0)
      throw Exception("limit can only be a value >= 0")
    event as Class<IEvent>
    listener as (event: IEvent) -> Unit
    if (event !in listeners) listeners[event] = mutableListOf(Entry(limit, listener))
    else {
      val entries = listeners[event]!!
      if (entries.none { it.listener == listener })
        entries.add(Entry(limit, listener))
    }
    return this
  }

  override fun <T : IEvent> off(event: Class<out T>, listener: (T) -> Unit): IMessageBus<IEvent> {
    if (event as Class<IEvent> !in listeners) return this
    val entries = listeners[event]!!
    val e = entries.find { it.listener == listener }
    if (e != null)
      entries.remove(e)
    return this
  }

  override fun <T : IEvent> off(event: Class<out T>, stream: IEventStream<T>): IMessageBus<IEvent> {
    val listener = if (streams.contains(stream)) streams[stream] else null
    return if (listener == null) this else off(event, listener)
  }

  override fun <T : IEvent> emit(event: T) {
    val c = event.javaClass as Class<IEvent>
    if (c !in listeners) return

    val toRemove = LinkedList<Entry>()
    for (entry in listeners[c]!!) {
      entry.listener(event)
      if (entry.left == 0)
        continue
      if (entry.left == 1) {
        toRemove.add(entry)
        continue
      }
      entry.left--
    }
    listeners[c]!!.removeAll(toRemove)
  }

  override fun <T : IEvent> stream(event: Class<out T>): IEventStream<T> {
    val stream = EventStream<T>()
    val func: (T) -> Unit = { stream(it) }
    this.on(event, func)
    streams[stream] = func as (IEvent) -> Unit
    return stream
  }

  internal class Entry(var left: Int, val listener: (event: IEvent) -> Unit) {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Entry

      if (listener != other.listener) return false

      return true
    }

    override fun hashCode(): Int {
      var result = left
      result = 31 * result + listener.hashCode()
      return result
    }
  }

  /**A simple implementation of [IEventStream]. */
  class EventStream<T> : IEventStream<T> {

    private var func: ((T) -> Unit)? = null

    private fun <E> execute(): EventStream<E> {
      if (func != null)
        throw IllegalStateException("stream has already been operated upon")
      return EventStream()
    }

    override fun <R> map(mapper: Function<in T, out R>): IEventStream<R> {
      val stream = execute<R>()
      func = { stream(mapper.apply(it)) }
      return stream
    }

    override fun filter(predicate: Predicate<in T>): IEventStream<T> {
      val stream = execute<T>()
      func = { if (predicate.test(it)) stream(it) }
      return stream
    }

    override fun forEach(action: Consumer<in T>) {
      execute<T>()
      func = { action.accept(it) }
    }

    override fun peek(action: Consumer<in T>): IEventStream<T> {
      val stream = execute<T>()
      func = { action.accept(it); stream(it) }
      return stream
    }

    override fun combine(other: IEventStream<T>): IEventStream<T> {
      val stream = execute<T>()
      forEach { stream(it) }
      other.forEach { stream(it) }
      return stream
    }

    override fun skip(n: Number): IEventStream<T> {
      var times = n.toLong()
      val stream = execute<T>()
      func = { if (times == 0L) stream(it) else times-- }
      return stream
    }

    /**Starts executing the stream. */
    operator fun invoke(value: T) {
      func?.invoke(value)
    }
  }

  /**The global bus. */
  companion object : Brev(true) {
    /**Creates a new event bus. */
    @JvmStatic fun createBus() = Brev(false)
    /**The global bus in a property. */
    @JvmField val global: Brev = this
  }
}
