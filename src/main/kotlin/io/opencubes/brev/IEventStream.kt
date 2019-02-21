package io.opencubes.brev

import java.util.function.*
import java.util.function.Function

/**
 * A interface to indicate how a event stream is.
 */
@Suppress("UNCHECKED_CAST")
interface IEventStream<T> {
  /**Map a event [T] to a new object [R]. */
  fun <R : Any?> map(mapper: Function<in T, out R>): IEventStream<R>
  /**Filter one/some events */
  fun filter(predicate: Predicate<in T>): IEventStream<T>
  /**Do something with the event that was fired. */
  fun forEach(action: Consumer<in T>)
  /**Like [forEach] but returns the stream. */
  fun peek(action: Consumer<in T>): IEventStream<T>
  /**Combines two event streams. */
  fun combine(other: IEventStream<T>): IEventStream<T>
  /**Skip [n] amount of events. */
  fun skip(n: Number): IEventStream<T>

  /**[map] macro.*/
  fun <R> map(mapper: (T) -> R) = map(Function<T, R> { mapper(it) })
  /**Cast [T] to [R]. */
  fun <R> mapTo() = map(Function<T, R> { it as R })

  /**[filter] macro.*/
  fun filter(predicate: (T) -> Boolean) = filter(Predicate { predicate(it) })
  /**Like [filter] but inverts the result. */
  fun filterNot(predicate: Predicate<in T>) = filter(predicate.negate())
  /**Like [filter] but inverts the result. */
  fun filterNot(predicate: (T) -> Boolean) = filter(Predicate { !predicate(it) })
  /**Filters out all objects that are null. */
  fun filterNotNull() = filter(Predicate { it != null })
  /**Filters out all objects that are not of [R] instance. */
  fun <R : Any> filterIsInstance(): IEventStream<R> =
      map { try { it as R } catch (e: Throwable) { Unit } }
          .filter { it != Unit }
          .map { it as R }

  /**[forEach] macro.*/
  fun forEach(action: (T) -> Unit) = forEach(Consumer { action(it) })
  /**[peek] macro.*/
  fun peek(action: (T) -> Unit) = peek(Consumer { action(it) })
}
