package com.zelkatani

interface Builder<out T> {
    fun build(): T
}