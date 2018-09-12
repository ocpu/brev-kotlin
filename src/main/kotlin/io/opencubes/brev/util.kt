@file:JvmName("BusUtil")

package io.opencubes.brev


inline fun <reified T : IEvent> IMessageBus.on(noinline listener: (event: T) -> Unit) = on(T::class.java, listener)
inline fun <reified T : IEvent> IMessageBus.once(noinline listener: (event: T) -> Unit) = once(T::class.java, listener)
inline fun <reified T : IEvent> IMessageBus.many(limit: Int, noinline listener: (event: T) -> Unit) = many(T::class.java, limit, listener)
inline fun <reified T : IEvent> IMessageBus.off(noinline listener: (event: T) -> Unit) = off(T::class.java, listener)
inline fun <reified T : IEvent> IMessageBus.off(stream: IEventStream<T>) = off(T::class.java, stream)
inline fun <reified T : IEvent> IMessageBus.stream() = stream(T::class.java)