package com.enq.equalizer.eq

import kotlinx.coroutines.flow.MutableStateFlow

object EqStore {
    val state = MutableStateFlow(EqState())
    val sessions = MutableStateFlow<Set<Int>>(emptySet())
    val global = MutableStateFlow(false)
    val info = MutableStateFlow("")
    // 0 = динамический, 1 = классический, 2 = выключено
    val mode = MutableStateFlow(1)
}
