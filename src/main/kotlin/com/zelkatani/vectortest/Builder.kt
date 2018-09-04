package com.zelkatani.vectortest

/**
 * Builder interface to build components.
 */
interface Builder<out T> {
    /**
     * Create a version of [T].
     */
    fun build(): T
}