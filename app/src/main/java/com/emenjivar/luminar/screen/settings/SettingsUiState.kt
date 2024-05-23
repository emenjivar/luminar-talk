package com.emenjivar.luminar.screen.settings

import android.util.Range
import kotlinx.coroutines.flow.StateFlow

data class SettingsUiState(
    val circularityRange: StateFlow<Range<Float>>,
    val blobRadiusRange: StateFlow<Range<Float>>,
    val lightBPM: StateFlow<Int>,
    val onReset: () -> Unit
)
