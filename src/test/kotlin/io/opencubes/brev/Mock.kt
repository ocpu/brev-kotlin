package io.opencubes.brev

class Mock {
  private val _calls = mutableListOf<Array<out Any>>()
  private var returns: Any = Unit

  val calls: List<Array<out Any>> get() = _calls

  operator fun invoke(vararg args: Any): Any {
    _calls += args
    return returns
  }

  fun clear() {
    _calls.clear()
  }

  inline fun <reified T: Any> fn(): T = this::invoke as T
}
