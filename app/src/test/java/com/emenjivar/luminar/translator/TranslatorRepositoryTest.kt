package com.emenjivar.luminar.translator

import com.emenjivar.luminar.translator.dictionary.MorseDictionary
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TranslatorRepositoryTest {

    private lateinit var translatorRepository: TranslatorRepository

    @Before
    fun initRepository() {
        translatorRepository = TranslatorRepositoryImp(initTree = false)
    }

    @Test
    fun `repository should initialize left with E and rightNode with T`() {
        val e = MorseDictionary.E
        val t = MorseDictionary.T

        // Find the two root letter in an non initialized tree
        val outputLeftNode = translatorRepository.find(e.morseCharacters())
        val outputRightNode = translatorRepository.find(t.morseCharacters())

        // Verify the values are added by default
        assertEquals(e.char, outputLeftNode)
        assertEquals(t.char, outputRightNode)
    }

    @Test
    fun `add should omit repeated values`() {
        val character = MorseDictionary.A

        // When the same character is added multiple times
        translatorRepository.add(character)
        translatorRepository.add(character)
        translatorRepository.add(character)

        // The character should be stored just one time
        val output = translatorRepository.find(character.morseCharacters())
        assertEquals(character.char, output)
    }

    @Test
    fun `repository should initialize the tree with all the character when initTree parameter is true`() {
        // initTree is true by default but i passed explicit here to improve the reading
        val translatorRepository = TranslatorRepositoryImp(initTree = true)

        // Verify all the characters in the dictionary were loaded
        MorseDictionary.values().onEach { morse ->
            val output = translatorRepository.find(morse.morseCharacters())
            assertEquals(morse.char, output)
        }
    }
}
