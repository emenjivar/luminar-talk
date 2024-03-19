package com.emenjivar.luminar.translator.dictionary

/**
 * https://en.wikipedia.org/wiki/Morse_code#/media/File:International_Morse_Code.svg
 */
enum class MorseDictionary(val char: Char, vararg val morse: Morse) {
    A(char = 'a', Morse.DOT, Morse.DASH),
    B(char = 'b', Morse.DASH, Morse.DOT, Morse.DOT, Morse.DOT),
    C(char = 'c', Morse.DASH, Morse.DOT, Morse.DASH, Morse.DOT),
    D(char = 'd', Morse.DASH, Morse.DOT, Morse.DOT),
    E(char = 'e', Morse.DOT),
    F(char = 'f', Morse.DOT, Morse.DOT, Morse.DASH, Morse.DOT),
    G(char = 'g', Morse.DASH, Morse.DASH, Morse.DOT),
    H(char = 'h', Morse.DOT, Morse.DOT, Morse.DOT, Morse.DOT),
    I(char = 'i', Morse.DOT, Morse.DOT),
    J(char = 'j', Morse.DOT, Morse.DASH, Morse.DASH, Morse.DASH),
    K(char = 'k', Morse.DASH, Morse.DOT, Morse.DASH),
    L(char = 'l', Morse.DOT, Morse.DASH, Morse.DOT, Morse.DOT),
    M(char = 'm', Morse.DASH, Morse.DASH),
    N(char = 'n', Morse.DASH, Morse.DOT),
    O(char = 'o', Morse.DASH, Morse.DASH, Morse.DASH),
    P(char = 'p', Morse.DOT, Morse.DASH, Morse.DASH, Morse.DOT),
    Q(char = 'q', Morse.DASH, Morse.DASH, Morse.DOT, Morse.DASH),
    R(char = 'r', Morse.DOT, Morse.DASH, Morse.DOT),
    S(char = 's', Morse.DOT, Morse.DOT, Morse.DOT),
    T(char = 't', Morse.DASH),
    U(char = 'u', Morse.DOT, Morse.DOT, Morse.DASH),
    V(char = 'v', Morse.DOT, Morse.DOT, Morse.DOT, Morse.DASH),
    W(char = 'w', Morse.DOT, Morse.DASH, Morse.DASH),
    X(char = 'x', Morse.DASH, Morse.DOT, Morse.DOT, Morse.DASH),
    Y(char = 'y', Morse.DASH, Morse.DOT, Morse.DASH, Morse.DASH),
    Z(char = 'z', Morse.DASH, Morse.DASH, Morse.DOT, Morse.DOT),
    ONE(char = '1', Morse.DOT, Morse.DASH, Morse.DASH, Morse.DASH, Morse.DASH),
    TWO(char = '2', Morse.DOT, Morse.DOT, Morse.DASH, Morse.DASH, Morse.DASH),
    THREE(char = '3', Morse.DOT, Morse.DOT, Morse.DOT, Morse.DASH, Morse.DASH),
    FOUR(char = '4', Morse.DOT, Morse.DOT, Morse.DOT, Morse.DOT, Morse.DASH),
    FIVE(char = '5', Morse.DOT, Morse.DOT, Morse.DOT, Morse.DOT, Morse.DOT),
    SIX(char = '6', Morse.DASH, Morse.DOT, Morse.DOT, Morse.DOT, Morse.DOT),
    SEVEN(char = '7', Morse.DASH, Morse.DASH, Morse.DOT, Morse.DOT, Morse.DOT),
    EIGHT(char = '8', Morse.DASH, Morse.DASH, Morse.DASH, Morse.DOT, Morse.DOT),
    NINE(char = '9', Morse.DASH, Morse.DASH, Morse.DASH, Morse.DASH, Morse.DOT),
    ZERO(char = '0', Morse.DASH, Morse.DASH, Morse.DASH, Morse.DASH, Morse.DASH);
    fun morseCharacters() = this.morse.asList()
}
