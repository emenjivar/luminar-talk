package com.emenjivar.luminar.screen.camera

import android.util.Range
import kotlinx.coroutines.flow.StateFlow

data class CameraUiState(
    val morseCharacter: StateFlow<MorseCharacter>,
    val lastDuration: StateFlow<Long>,
    val messages: StateFlow<List<String>>,
    val debugMorse: StateFlow<String>,
    val circularityRange: StateFlow<Range<Float>>,
    val blobRadiusRange: StateFlow<Range<Float>>,
    val addFlashState : (isTurnOn: Boolean) -> Unit,
    val finishLetter: () -> Unit,
    val finishWord: () -> Unit,
    val finishMessage: () -> Unit,
    val clearText: () -> Unit,
    val onSetCircularity: (Range<Float>) -> Unit,
    val onSetBlobRadius: (Range<Float>) -> Unit,
)
