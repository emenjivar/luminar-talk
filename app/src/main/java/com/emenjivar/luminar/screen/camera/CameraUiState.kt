package com.emenjivar.luminar.screen.camera

import android.util.Range
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class CameraUiState(
    val morseCharacter: StateFlow<MorseCharacter>,
    val lastDuration: StateFlow<Long>,
    val messages: StateFlow<List<String>>,
    val debugMorse: StateFlow<String>,
    val timingData: StateFlow<TimingData>,
    val circularityRange: StateFlow<Range<Float>>,
    val blobAreaRange: StateFlow<Range<Float>>,
    val lightBPM: StateFlow<Int>,
    val emission: Flow<Boolean>,
    val addFlashState : (isTurnOn: Boolean) -> Unit,
    val finishLetter: () -> Unit,
    val finishWord: () -> Unit,
    val finishMessage: () -> Unit,
    val clearText: () -> Unit,
    val onTranslateToMorse: (String) -> Unit,
    val onReset: () -> Unit
)
