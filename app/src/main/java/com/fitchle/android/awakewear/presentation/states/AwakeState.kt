package com.fitchle.android.awakewear.presentation.states

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object AwakeState {
    var enabled: Boolean = false;

    private val _awakeStateFlow = MutableStateFlow<Boolean?>(null)
    val awakeStateFlow = _awakeStateFlow.asStateFlow()

    fun setEnabledd(enabled: Boolean) {
        this.enabled = enabled;
        _awakeStateFlow.update { enabled }
    }
}