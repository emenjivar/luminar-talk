package com.emenjivar.luminar.screen.camera

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val morseCharacter = MutableStateFlow(MorseCharacter.NONE)
    private val lightFlickers = ArrayDeque<LightFlicker>()
    private val lastDuration = MutableStateFlow(0L)
    private fun addFlashState(isTurnOn: Boolean) {
        // Ensure the same elements in not saved twice consecutively
        if (lightFlickers.lastOrNull()?.isTurnOn == isTurnOn) {
            return
        }

        val previous = lightFlickers.lastOrNull()
        val current = LightFlicker(
            isTurnOn = isTurnOn,
            milliseconds = System.currentTimeMillis()
        )

        if (previous != null) {
            val diffMilliseconds = current.milliseconds - previous.milliseconds
            val isLightEmission = previous.isTurnOn && !current.isTurnOn


            lastDuration.update { diffMilliseconds }
            if (isLightEmission) {
                when {
                    diffMilliseconds in (DIT - DIT_ERROR)..(DIT + DIT_ERROR) -> {
                        morseCharacter.update { MorseCharacter.DIT }
                    }
                    diffMilliseconds > (DAH - DAH_ERROR) && diffMilliseconds <= (DAH + DAH_ERROR) -> {
                        morseCharacter.update { MorseCharacter.DAH }
                    }
                }
            } else {
                when {
                    diffMilliseconds in (SPACE - SPACE_ERROR)..(SPACE + SPACE_ERROR) -> {
                        morseCharacter.update { MorseCharacter.SPACE }
                    }
                    diffMilliseconds in (SPACE_LETTER - SPACE_LETTER_ERROR)..(SPACE_LETTER + SPACE_LETTER_ERROR) -> {
                        morseCharacter.update { MorseCharacter.LETTER_SPACE }
                    }
                    diffMilliseconds in (SPACE_WORD - SPACE_WORD_ERROR)..(SPACE_WORD + SPACE_WORD_ERROR) -> {
                        morseCharacter.update { MorseCharacter.WORD_SPACE }
                    }
                    diffMilliseconds > (SPACE_WORD + SPACE_ERROR) -> {
                        morseCharacter.update { MorseCharacter.END_SENTENCE }
                        lightFlickers.clear()
                    }
                }
            }
        }

        lightFlickers.add(current)
    }

    val state = CameraUiState(
        morseCharacter = morseCharacter,
        lastDuration = lastDuration,
        addFlashState = ::addFlashState
    )

    companion object {
        // TODO: DIT is the UNIT that determines the duration of the other characters
        //  for now is good, but this should be a dynamic value.
        private const val DIT = 1000L
        private const val DIT_ERROR = DIT / 2

        private const val DAH = DIT * 3
        private const val DAH_ERROR = DIT + DIT_ERROR

        private const val SPACE = DIT
        private const val SPACE_ERROR = SPACE / 2

        private const val SPACE_LETTER = SPACE * 3
        private const val SPACE_LETTER_ERROR = SPACE_LETTER - (SPACE + SPACE_ERROR)

        private const val SPACE_WORD = SPACE * 7
        private const val SPACE_WORD_ERROR = SPACE_WORD - (SPACE_LETTER + SPACE_LETTER_ERROR)
    }
}

data class LightFlicker(val isTurnOn: Boolean, val milliseconds: Long)
enum class MorseCharacter {
    DIT,
    DAH,
    SPACE,
    LETTER_SPACE,
    WORD_SPACE,
    END_SENTENCE,
    NONE
}
