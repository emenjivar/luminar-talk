package com.emenjivar.luminar.screen.camera

import android.util.Range
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emenjivar.luminar.data.SettingPreferences
import com.emenjivar.luminar.translator.TranslatorRepository
import com.emenjivar.luminar.translator.dictionary.Morse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val translatorRepository: TranslatorRepository,
    private val settings: SettingPreferences
) : ViewModel() {
    private val morseCharacter = MutableStateFlow(MorseCharacter.NONE)
    private val lightFlickers = ArrayDeque<LightFlicker>()
    private val lastDuration = MutableStateFlow(0L)

    // Indicates a list of morse that make up a single letter o number
    private val listMorse = mutableListOf<Morse>()
    private val debugMorse = MutableStateFlow("")

    // Store the list of messages
    private val messages = morseCharacter
        .scan(initial = "") { accumulator, morse ->
            when (morse) {
                MorseCharacter.DIT -> {
                    listMorse.add(Morse.DOT)
                    debugMorse.update { value -> "$value." }
                    accumulator
                }

                MorseCharacter.DAH -> {
                    listMorse.add(Morse.DASH)
                    debugMorse.update { value -> "$value-" }
                    accumulator
                }

                MorseCharacter.LETTER_SPACE -> {
                    val value = translatorRepository.find(listMorse)
                    listMorse.clear()
                    lightFlickers.clear()
                    debugMorse.update { "" }
                    if (value != null) {
                        accumulator + value
                    } else {
                        accumulator
                    }
                }

                MorseCharacter.WORD_SPACE -> {
                    listMorse.clear()
                    lightFlickers.clear()
                    debugMorse.update { "" }
                    // Add and space to divide indicate a new word starts
                    "$accumulator "
                }

                MorseCharacter.END_SENTENCE -> {
                    listMorse.clear()
                    lightFlickers.clear()
                    debugMorse.update { "" }
                    "$accumulator\n"
                }
                // TODO: add here some condition to clear the message list
                else -> accumulator
            }
        }.map { text ->
            text.split('\n')
                .filter { it.isNotBlank() }
                .asReversed()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val minAllowedCircularity = 0.01f
    private val circularityRange = settings.getCircularity()
        .map { range ->
            val min = range.lower.coerceAtLeast(minAllowedCircularity)
            Range(min, range.upper)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Range(minAllowedCircularity, 1f)
        )

    private val minAllowedArea = 0.01f
    private val blobAreaRange = settings.getBlobRadius()
        .map { range ->
            val minArea = (Math.PI * range.lower * range.lower).toFloat().coerceAtLeast(minAllowedArea)
            val maxArea = (Math.PI * range.upper * range.upper).toFloat()
            Range(minArea, maxArea)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Range(minAllowedArea, 200f)
        )

    private val lightBPM = settings.getLightBPM()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = 60
        )

    private val timingData = lightBPM.map { bpm ->
        bpm / SECOND_PER_MINUTE * MILLISECOND_PER_SECOND
    }.map { dit ->
        TimingData(dit)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = TimingData(DEFAULT_DIT_TIME)
    )

    private fun onReset() {
        viewModelScope.launch {
            settings.setBlobRadius(Range(0f, 200f))
            settings.setCircularity(Range(0f, 1f))
            settings.setLightBPM(DEFAULT_BPM)
        }
    }

    private fun addFlashState(isTurnOn: Boolean) {
        // Ensure the same elements in not saved twice consecutively
        if (lightFlickers.lastOrNull()?.isTurnOn == isTurnOn) {
            return
        }
        val timing = this.timingData.value

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
                when (diffMilliseconds) {
                    in timing.getDitRange() -> {
                        morseCharacter.update { MorseCharacter.DIT }
                    }

                    in timing.getDashRange() -> {
                        morseCharacter.update { MorseCharacter.DAH }
                    }
                }
            } else {
                when {
                    diffMilliseconds in timing.getDitRange() -> {
                        morseCharacter.update { MorseCharacter.SPACE }
                    }

                    diffMilliseconds in timing.getSpaceLetterRange() -> {
                        morseCharacter.update { MorseCharacter.LETTER_SPACE }
                    }

                    diffMilliseconds in timing.getSpaceWordRange() -> {
                        morseCharacter.update { MorseCharacter.WORD_SPACE }
                    }

                    diffMilliseconds >= timing.endMessage -> finishMessage()
                }
            }
        }

        lightFlickers.add(current)
    }

    private fun finishMessage() {
        morseCharacter.update { MorseCharacter.END_SENTENCE }
    }

    private fun finishWord() {
        morseCharacter.update { MorseCharacter.WORD_SPACE }
    }

    private fun finishLetter() {
        morseCharacter.update { MorseCharacter.LETTER_SPACE }
    }

    private fun clearText() {
        // TODO: send here a signal to clear the text
//        sentence.update { "" }
    }

    val state = CameraUiState(
        morseCharacter = morseCharacter,
        lastDuration = lastDuration,
        messages = messages,
        debugMorse = debugMorse,
        timingData = timingData,
        circularityRange = circularityRange,
        blobAreaRange = blobAreaRange,
        lightBPM = lightBPM,
        addFlashState = ::addFlashState,
        finishLetter = ::finishLetter,
        finishWord = ::finishWord,
        finishMessage = ::finishMessage,
        clearText = ::clearText,
        onReset = ::onReset
    )

    companion object {
        private const val SECOND_PER_MINUTE = 60
        private const val DEFAULT_DIT_TIME = 1000L
        private const val MILLISECOND_PER_SECOND = 1000L
        private const val DEFAULT_BPM = 60
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
