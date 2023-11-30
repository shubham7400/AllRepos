package com.example.customcomponent.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private var _sweepAngle = MutableStateFlow(0)
    val uiState: StateFlow<Int> = _sweepAngle.asStateFlow()

    fun updateSweepAngle(sweepAngle: Int){
        _sweepAngle.value = sweepAngle
    }
}