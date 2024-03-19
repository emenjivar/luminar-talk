package com.emenjivar.luminar.translator.dictionary

enum class Morse {
    DOT,
    DASH
}

data class Node(
    val value: Morse,
    val char: Char? = null,
    var left: Node? = null,
    var right: Node? = null
)
