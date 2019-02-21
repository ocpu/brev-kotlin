@file:JvmName("BusUtil")

package io.opencubes.brev

/**A shorthand function forregistering on events. */
inline fun <reified T : Any> IMessageBus<in T>.on(noinline listener: (event: T) -> Unit) = on(T::class.java, listener)
/**A shorthand function forregistering once on events. */
inline fun <reified T : Any> IMessageBus<in T>.once(noinline listener: (event: T) -> Unit) = once(T::class.java, listener)
/**A shorthand function forregistering on events. */
inline fun <reified T : Any> IMessageBus<in T>.many(limit: Int, noinline listener: (event: T) -> Unit) = many(T::class.java, limit, listener)
/**A shorthand function unregistering off events. */
inline fun <reified T : Any> IMessageBus<in T>.off(noinline listener: (event: T) -> Unit) = off(T::class.java, listener)
/**A shorthand function unregistering off events. */
inline fun <reified T : Any> IMessageBus<in T>.off(stream: IEventStream<T>) = off(T::class.java, stream)
/**A shorthand function for registering on a event stream. */
inline fun <reified T : Any> IMessageBus<in T>.stream() = stream(T::class.java)
