package com.emenjivar.luminar.screen.camera

import kotlinx.coroutines.flow.StateFlow

data class CameraUiState(
    val morseCharacter: StateFlow<MorseCharacter>,
    val lastDuration: StateFlow<Long>,
    val word: StateFlow<String?>,
    val debugMorse: StateFlow<String>,
    val addFlashState : (isTurnOn: Boolean) -> Unit,
    val finishLetter: () -> Unit,
    val finishWord: () -> Unit,
    val finishMessage: () -> Unit,
    val clearText: () -> Unit
)
