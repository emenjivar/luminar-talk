package com.emenjivar.luminar.translator

import com.emenjivar.luminar.translator.dictionary.Morse
import com.emenjivar.luminar.translator.dictionary.MorseDictionary

interface TranslatorRepository {
    /**
     * Finds the corresponding character for the given list of mode characters.
     * https://en.wikipedia.org/wiki/Morse_code#/media/File:International_Morse_Code.svg
     *
     * @param list of morse characters.
     */
    fun find(list: List<Morse>): Char?

    /**
     * Add a new morse code to the three in memory.
     */
    fun add(morse: MorseDictionary)
}
