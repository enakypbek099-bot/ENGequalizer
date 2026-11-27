package com.enq.equalizer.eq

import kotlinx.coroutines.flow.MutableStateFlow

object EqStore {
    val state = MutableStateFlow(EqState())
    val sessions = MutableStateFlow<Set<Int>>(emptySet())
    val global = MutableStateFlow(false)
    val info = MutableStateFlow("")
    val mode = MutableStateFlow(0)
}
