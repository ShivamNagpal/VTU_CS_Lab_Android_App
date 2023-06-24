package com.nagpal.shivam.vtucslab.screens

interface EventEmitter<T> {
    fun onEvent(event: T)
}
