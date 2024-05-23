package com.emenjivar.luminar.screen.settings

import android.util.Range
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emenjivar.luminar.data.SettingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    settings: SettingPreferences
) : ViewModel() {

    private val circularityRange = settings.getCircularity()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Range(0f, 1f)
        )

    private val blobRadiusRange = settings.getBlobRadius()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Range(0f, 200f)
        )

    private val lightBPM = settings.getLightBPM()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = 60
        )

    val uiState = SettingsUiState(
        circularityRange = circularityRange,
        blobRadiusRange = blobRadiusRange,
        lightBPM = lightBPM,
        onReset = {},
    )
}
