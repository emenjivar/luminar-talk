package com.emenjivar.luminar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme

@Composable
fun MessageBubble(
    message: String,
    modifier: Modifier = Modifier,
    isSentByMe: Boolean = false
) {
    val tipOrientation = remember(isSentByMe) {
        if (isSentByMe) {
            TipOrientation.RIGHT
        } else {
            TipOrientation.LEFT
        }
    }

    val extraStartPadding = if (!isSentByMe) {
        HORIZONTAL_PADDING
    } else {
        0.dp
    }

    val extraEndPadding = if (isSentByMe) {
        HORIZONTAL_PADDING
    } else {
        0.dp
    }

    Row(
        modifier = modifier
            .graphicsLayer {
                this.shadowElevation = 5.dp.toPx()
                this.shape = MessageShape(tipOrientation)
                this.clip = true
            }
            .background(color = Color.DarkGray)
            .padding(
                horizontal = HORIZONTAL_PADDING,
                vertical = VERTICAL_PADDING
            )
            .padding(
                start = extraStartPadding,
                end = extraEndPadding
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = message,
            color = Color.White
        )
    }
}

private val HORIZONTAL_PADDING = 15.dp
private val VERTICAL_PADDING = 5.dp

@Preview
@Composable
private fun MessageBubbleText() {
    RealTimeCameraFilterTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            MessageBubble(
                message = "Lorem ipsum dolor.",
            )
            MessageBubble(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                isSentByMe = true
            )
            MessageBubble(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                isSentByMe = false
            )

        }
    }
}
