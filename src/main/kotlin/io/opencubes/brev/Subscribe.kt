package io.opencubes.brev

/**
 * @param value The amount of times the method can the executed
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Subscribe(val value: Int = 0)
