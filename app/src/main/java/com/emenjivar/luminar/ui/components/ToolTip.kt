package com.emenjivar.luminar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme

@Composable
private fun ToolTip(
    text: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(roundedCorner))
            .background(color = Color.Black, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = horizontalPadding, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp
        )
    }
}

enum class ToolTipDirection {
    START,
    TOP,
    END,
    BOTTOM
}

private val roundedCorner = 4.dp
private val horizontalPadding = 16.dp

@Preview
@Composable
private fun ToolTipPreview() {
    RealTimeCameraFilterTheme {
        ToolTip(
            text = "This is the value for circularity"
        )
    }
}