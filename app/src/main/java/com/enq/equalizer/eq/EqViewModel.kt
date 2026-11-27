package com.enq.equalizer.eq

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EqViewModel : ViewModel() {
    private val _state = MutableStateFlow(EqState())
    val state: StateFlow<EqState> = _state.asStateFlow()

    fun setGain(index: Int, value: Float) {
        _state.update { s ->
            val g = s.gains.toMutableList()
            g[index] = value
            s.copy(gains = g, activePresetId = null)
        }
    }

    fun setPreamp(value: Float) {
        _state.update { it.copy(preampDb = value) }
    }

    fun setPro(on: Boolean) {
        _state.update { s ->
            val p = presetById(s.activePresetId)
            when {
                p == null -> s.copy(pro = on)
                p.pro && !on -> s.copy(pro = on, gains = List(BAND_COUNT) { 0f }, activePresetId = null)
                else -> s.copy(pro = on, gains = p.toGains(on))
            }
        }
    }

    fun reset() {
        _state.update { it.copy(gains = List(BAND_COUNT) { 0f }, preampDb = 0f, activePresetId = null) }
    }

    fun applyPreset(p: Preset) {
        _state.update { it.copy(gains = p.toGains(it.pro), activePresetId = p.id) }
    }
}
