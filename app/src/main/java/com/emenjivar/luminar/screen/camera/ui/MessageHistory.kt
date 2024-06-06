package com.emenjivar.luminar.screen.camera.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.ui.components.message.MessageBubble
import com.emenjivar.luminar.ui.theme.AppTheme

/**
 * Renders the list of messages decoded from morse.
 * @param messages List of messages displayed on the chat history.
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
            .verticalScroll(verticalScroll),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(paddingMessages)
    ) {
        for (message in messages) {
            MessageBubble(message = message)
        }
    }
}

private val paddingMessages = 4.dp

@Preview
@Composable
private fun MessageHistoryPreview() {
    AppTheme {
        MessageHistory(
            messages = listOf(
                "alpha",
                "bravo charlie",
                "delta echo foxtrot"
            )
        )
    }
}
