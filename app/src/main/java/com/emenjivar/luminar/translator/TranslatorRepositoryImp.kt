package com.emenjivar.luminar.translator

import com.emenjivar.luminar.translator.dictionary.Morse
import com.emenjivar.luminar.translator.dictionary.MorseDictionary
import com.emenjivar.luminar.translator.dictionary.Node
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @param initTree Used to fill load the alphabet and numbers to the tree.
 *  This should be false during unit testing, to test easily empty trees
 */
@Singleton
class TranslatorRepositoryImp @Inject constructor(
    private val initTree: Boolean
) : TranslatorRepository {

    private val leftRoot = Node(
        value = MorseDictionary.E.morse.first(),
        char = MorseDictionary.E.char
    )
    private val rightRoot = Node(
        value = MorseDictionary.T.morse.first(),
        char = MorseDictionary.T.char
    )

    private val mapCharacters = MorseDictionary.entries
        .toTypedArray()
        .associate { it.char to it.morseCharacters() }

    init {
        if (initTree) {
            MorseDictionary.entries.toTypedArray().onEach { morse ->
                add(morse)
            }
        }
    }

    override fun find(list: List<Morse>): Char? {
        if (list.isEmpty()) return null
        val root = when (list.first()) {
            Morse.DOT -> leftRoot
            Morse.DASH -> rightRoot
        }
        return if (list.size == 1) {
            root.char
        } else {
            find(list = ArrayDeque(list), node = root)
        }
    }

    override fun add(morse: MorseDictionary) {
        // Assuming the root nodes are already added
        if (morse.morseCharacters().size == 1) return

        val root = when (morse.morse.first()) {
            Morse.DOT -> leftRoot
            Morse.DASH -> rightRoot
        }
        add(morse.char, ArrayDeque(morse.morseCharacters()), root)
    }

    override fun charToMorse(char: Char) = mapCharacters[char].orEmpty()

    private fun find(list: ArrayDeque<Morse>, node: Node?, depth: Int = 1): Char? {
        if (node == null) return null
        if (list.isEmpty()) return node.char

        var current = list.removeFirst()

        if (depth == 1 && list.isNotEmpty()) {
            current = list.removeFirst()
        }

        return when(current) {
            Morse.DOT -> find(list, node.left, depth + 1)
            Morse.DASH -> find(list, node.right, depth + 1)
        }
    }

    /**
     * Add a morse character in the binary tree
     */
    private fun add(
        char: Char,
        list: ArrayDeque<Morse>,
        node: Node?,
        depth: Int = 1
    ) {
        if (node == null || list.isEmpty()) return

        var current = list.removeFirst()

        if (depth == 1 && list.isNotEmpty()) {
            // Assuming the list always have 2 or more items
            current = list.removeFirst()
        }

        if (list.isEmpty()) {
            val newNode = Node(current, char)
            when (current) {
                Morse.DOT -> {
                    val left = node.left
                    if (left == null) {
                        // Left node didn't exist, create a new one
                        node.left = newNode
                    } else {
                        // Left already exist, just override the letter
                        node.left = left.copy(char = char)
                    }
                }

                Morse.DASH -> {
                    val right = node.right
                    if (right == null) {
                        // Right didn't exist, create a new one
                        node.right = newNode
                    } else {
                        node.right = right.copy(char = char)
                    }
                }
            }
        } else {
            when (current) {
                Morse.DOT -> {
                    if (node.left == null) {
                        node.left = Node(current)
                    }
                    add(char, list, node.left, depth + 1)
                }

                Morse.DASH -> {
                    if (node.right == null) {
                        node.right = Node(current)
                    }
                    add(char, list, node.right, depth + 1)
                }
            }
        }
    }
}
