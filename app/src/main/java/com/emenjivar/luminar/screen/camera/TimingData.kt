package com.emenjivar.luminar.screen.camera

data class TimingData(
    val dit: Long
) {

    /**
     *  Milliseconds of duration for dah (`-`) character.
     */
    val dah = dit * 3

    /**
     * Indicates the milliseconds for decoding a new letter.
     */
    val spaceLetter = dit * 3

    /**
     * Indicates the milliseconds for decoding a new word.
     */
    val spaceWord = dit * 7

    /**
     * Margin of error in milliseconds for calculating a dit character.
     */
    val ditError = dit / 2

    /**
     * Margin of error in milliseconds for calculating a dah character.
     */
    val dahError = dit + dit / 2

    /**
     * Margin of error in milliseconds for calculating spaces between letters
     */
    val spaceLetterError = spaceLetter - (dit + ditError)

    /**
     * Margin of error in milliseconds for calculating spaces between words.
     */
    val spaceWordError = spaceWord - (spaceLetter + spaceLetterError)

    /**
     * Time needed in milliseconds for end the message and start a new one.
     */
    val endMessage = spaceWord + spaceWordError + 1

    fun getDitRange() = (dit - ditError)..(dit + ditError)
    fun getDashRange() = (dah - dahError)..(dah + dahError)
    fun getSpaceLetterRange() = (spaceLetter - spaceLetterError)..(spaceLetter + spaceLetterError)
    fun getSpaceWordRange() = (spaceWord - spaceWordError)..(spaceWord + spaceWordError)
}
