package com.emenjivar.luminar.ui.components

import android.util.Range
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.util.toRange
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    fieldName: String,
    value: () -> Range<Float>,
    valueRange: Range<Float>,
    modifier: Modifier = Modifier,
    onValueChange: (Range<Float>) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = fieldName, color = Color.White)
        RangeSlider(
            value = value().lower..value().upper,
            valueRange = valueRange.lower..valueRange.upper,
            onValueChange = { onValueChange(it.toRange()) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = roundOffDecimals(value().lower),
                color = Color.White
            )
            Text(
                text = roundOffDecimals(value().upper),
                color = Color.White
            )
        }
    }
}

private fun roundOffDecimals(value: Float): String {
    val format = DecimalFormat("#.##")
    format.roundingMode = RoundingMode.CEILING
    return format.format(value)
}
