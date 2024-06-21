package com.emenjivar.luminar.ext

/**
 * Verify if the value is inside of the exclusive range 0f..1f
 */
fun Float.isInProgress() = this > 0f && this < 1f
