@file:JvmName("BusUtil")

package io.opencubes.brev

/**A shorthand function forregistering on events. */
inline fun <reified T : IEvent> IMessageBus.on(noinline listener: (event: T) -> Unit) = on(T::class.java, listener)
/**A shorthand function forregistering once on events. */
inline fun <reified T : IEvent> IMessageBus.once(noinline listener: (event: T) -> Unit) = once(T::class.java, listener)
/**A shorthand function forregistering on events. */
inline fun <reified T : IEvent> IMessageBus.many(limit: Int, noinline listener: (event: T) -> Unit) = many(T::class.java, limit, listener)
/**A shorthand function unregistering off events. */
inline fun <reified T : IEvent> IMessageBus.off(noinline listener: (event: T) -> Unit) = off(T::class.java, listener)
/**A shorthand function unregistering off events. */
inline fun <reified T : IEvent> IMessageBus.off(stream: IEventStream<T>) = off(T::class.java, stream)
/**A shorthand function for registering on a event stream. */
inline fun <reified T : IEvent> IMessageBus.stream() = stream(T::class.java)
