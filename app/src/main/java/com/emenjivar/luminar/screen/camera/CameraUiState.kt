package com.emenjivar.luminar.screen.camera

import kotlinx.coroutines.flow.StateFlow

data class CameraUiState(
    val morseCharacter: StateFlow<MorseCharacter>,
    val lastDuration: StateFlow<Long>,
    val addFlashState : (isTurnOn: Boolean) -> Unit,
    val finishMessage: () -> Unit
)
