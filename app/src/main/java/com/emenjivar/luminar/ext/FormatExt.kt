package com.emenjivar.luminar.ext

fun Float.twoDecimals() = String.format(
    locale = null,
    format = "%.2f",
    this
)
