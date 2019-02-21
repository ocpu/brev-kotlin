package io.opencubes.brev

/**
 * @param limit The amount of times the method can the executed
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Subscribe(val limit: Int = 0)
