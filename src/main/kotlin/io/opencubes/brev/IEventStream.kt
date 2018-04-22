package io.opencubes.brev

import java.util.function.*
import java.util.function.Function

@Suppress("UNCHECKED_CAST")
interface IEventStream<T> {
  fun <R : Any?> map(mapper: Function<in T, out R>): IEventStream<R>
  fun filter(predicate: Predicate<in T>): IEventStream<T>
  fun forEach(action: Consumer<in T>)
  fun peek(action: Consumer<in T>): IEventStream<T>
  fun combine(other: IEventStream<T>): IEventStream<T>
  fun skip(n: Long): IEventStream<T>

  fun <R> map(mapper: (T) -> R) = map(Function<T, R> { mapper(it) })
  fun <R> mapTo() = map(Function<T, R> { it as R })

  fun filter(predicate: (T) -> Boolean) = filter(Predicate { predicate(it) })
  fun filterNot(predicate: Predicate<in T>) = filter(Predicate { !predicate.test(it) })
  fun filterNot(predicate: (T) -> Boolean) = filter(Predicate { !predicate(it) })
  fun filterNotNull() = filter(Predicate { it != null })
  fun <R : Any> filterIsInstance(): IEventStream<R> =
      map { try { it as R } catch (e: Throwable) { Unit } }
          .filter { it != Unit }
          .map { it as R }

  fun forEach(action: (T) -> Unit) = forEach(Consumer { action(it) })
  fun peek(action: (T) -> Unit) = peek(Consumer { action(it) })
}