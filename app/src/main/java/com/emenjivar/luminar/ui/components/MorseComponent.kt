package com.emenjivar.luminar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme

/**
 * Used for displaying a floating text in a rounded background component.
 */
@Composable
fun MorseText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(roundedShape)
            )
            .padding(horizontal = horizontalPaddingMorseText),
        text = text,
        fontSize = fontSizeMorseText,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

private val roundedShape = 15.dp
private val horizontalPaddingMorseText = 15.dp
private val fontSizeMorseText = 25.sp

@Preview
@Composable
private fun MorseTextPreview() {
    RealTimeCameraFilterTheme {
        MorseText(text = "-.--")
    }
}
