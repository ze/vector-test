package com.zelkatani

/**
 * Function interface to create functions that return some number of [T]'s
 */
interface Function<T> {
    /**
     * Evaluate the function to get a result.
     */
    fun eval(fields: IntArray): Array<T>
}