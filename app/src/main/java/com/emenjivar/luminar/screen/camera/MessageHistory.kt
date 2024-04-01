package com.emenjivar.luminar.screen.camera

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.ui.components.message.MessageBubble
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme

/**
 * Renders the list of messages decoded from morse.
 */
@Composable
@Stable
fun MessageHistory(
    messages: List<String>,
    modifier: Modifier = Modifier,
    verticalScroll: ScrollState = rememberScrollState()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(heightMessages)
            .padding(paddingMessages)
            .verticalScroll(verticalScroll),
        horizontalAlignment = Alignment.Start
    ) {
        for (message in messages) {
            MessageBubble(
                modifier = Modifier.padding(paddingMessages),
                message = message
            )
        }
    }
}

private val paddingMessages = 3.dp
private val heightMessages = 300.dp

@Preview
@Composable
private fun MessageHistoryPreview() {
    RealTimeCameraFilterTheme {
        MessageHistory(
            messages = listOf(
                "alpha",
                "bravo charlie",
                "delta echo foxtrot"
            )
        )
    }
}
