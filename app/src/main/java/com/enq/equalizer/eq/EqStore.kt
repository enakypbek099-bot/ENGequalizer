package com.enq.equalizer.eq

import kotlinx.coroutines.flow.MutableStateFlow

object EqStore {
    val state = MutableStateFlow(EqState())
    val sessions = MutableStateFlow<Set<Int>>(emptySet())
}
