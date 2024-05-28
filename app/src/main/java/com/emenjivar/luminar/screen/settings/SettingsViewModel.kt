package com.emenjivar.luminar.screen.settings

import android.util.Range
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emenjivar.luminar.data.SettingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingPreferences
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

    private fun onSetCircularity(range: Range<Float>) {
        viewModelScope.launch {
            settings.setCircularity(range)
        }
    }

    private fun onSetBlobRadius(range: Range<Float>) {
        viewModelScope.launch {
            settings.setBlobRadius(range)
        }
    }

    private fun onSetLightBPM(value: Int) {
        viewModelScope.launch {
            settings.setLightBPM(value)
        }
    }

    val uiState = SettingsUiState(
        circularityRange = circularityRange,
        blobRadiusRange = blobRadiusRange,
        lightBPM = lightBPM,
        onSetCircularity = ::onSetCircularity,
        onSetBlobRadius = ::onSetBlobRadius,
        onSetLightBPM = ::onSetLightBPM,
        onReset = {},
    )
}
