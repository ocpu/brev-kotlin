package io.opencubes.brev

class Mock {
  val calls = mutableListOf<Array<out Any>>()

  fun memorize(vararg args: Any) {
    calls.add(args)
  }

  fun clear() {
    calls.clear()
  }
}
