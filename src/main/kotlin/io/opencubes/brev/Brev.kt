package io.opencubes.brev

import java.util.function.*
import java.util.function.Function


@Suppress("UNCHECKED_CAST", "unused")
open class Brev private constructor(val global: Boolean) : IMessageBus {
  constructor() : this(false)

  private val listeners = mutableMapOf<Class<IEvent>, MutableSet<Entry>>()
  private val streams = mutableMapOf<IEventStream<*>, (IEvent) -> Unit>()

  private fun ensureEventExists(clazz: Class<IEvent>): MutableSet<Entry> =
      if (clazz in listeners) listeners[clazz]!! else {
        listeners[clazz] = mutableSetOf(); listeners[clazz]!!
      }

  override fun <T : IEvent> on(event: Class<out T>, listener: (T) -> Unit): IMessageBus =
      many(event, 0, listener)

  override fun <T : IEvent> once(event: Class<out T>, listener: (T) -> Unit): IMessageBus =
      many(event, 1, listener)

  override fun <T : IEvent> many(event: Class<out T>, limit: Int, listener: (T) -> Unit): IMessageBus {
    ensureEventExists(event as Class<IEvent>).add(Entry(limit, listener as (event: IEvent) -> Unit))
    return this
  }

  override fun <T : IEvent> off(event: Class<out T>, listener: (T) -> Unit): IMessageBus {
    val entry = ensureEventExists(event as Class<IEvent>).find { it.hashCode() == listener.hashCode() }
    listeners[event]!!.remove(entry)
    return this
  }

  override fun <T : IEvent> off(event: Class<out T>, stream: IEventStream<T>): IMessageBus {
    val listener = if (streams.contains(stream)) streams[stream] else null
    return if (listener == null) this else off(event, listener)
  }

  override fun <T : IEvent> emit(event: T) {
    for (entry in ensureEventExists(event.javaClass)) {
      entry.listener(event)
      if (entry.amount == 0)
        continue
      if (entry.amount == 1) {
        ensureEventExists(event.javaClass).remove(entry)
        continue
      }
      entry.amount--
    }
  }

  override fun <T : IEvent> stream(event: Class<out T>): IEventStream<T> {
    val stream = EventStream<T>()
    val func: (T) -> Unit = { stream(it) }
    this.on(event, func)
    streams[stream] = func as (IEvent) -> Unit
    return stream
  }

  private class Entry(var amount: Int, val listener: (event: IEvent) -> Unit) {
    override fun hashCode() = listener.hashCode()
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Entry

      if (listener != other.listener) return false

      return true
    }
  }

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

    operator fun invoke(value: T) {
      func?.invoke(value)
    }
  }

  companion object : IMessageBus {

    override fun <T : IEvent> on(event: Class<out T>, listener: (T) -> Unit) = global.on(event, listener)
    override fun <T : IEvent> once(event: Class<out T>, listener: (T) -> Unit) = global.once(event, listener)
    override fun <T : IEvent> many(event: Class<out T>, limit: Int, listener: (T) -> Unit) = global.many(event, limit, listener)
    override fun <T : IEvent> off(event: Class<out T>, listener: (T) -> Unit): IMessageBus = global.off(event, listener)
    override fun <T : IEvent> off(event: Class<out T>, stream: IEventStream<T>): IMessageBus = global.off(event, stream)
    override fun <T : IEvent> stream(event: Class<out T>) = global.stream(event)
    override fun <T : IEvent> emit(event: T) = global.emit(event)

    private val global = Brev(true)

    @JvmStatic
    fun createBus() = Brev(false)
  }
}
