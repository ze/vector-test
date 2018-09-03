package com.zelkatani

interface Function<T> {
    fun eval(fields: IntArray): T
}