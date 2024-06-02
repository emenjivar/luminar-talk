package com.emenjivar.luminar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SwitchButton(
    text: String,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onEnable: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .background(color = Color.Black, shape = CircleShape)
            .padding(
                horizontal = horizontalPaddingDebugSwitch,
                vertical = verticalPaddingDebugSwitch
            ),
        horizontalArrangement = Arrangement.spacedBy(debugSwitchPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSizeDebug
        )
        Switch(
            checked = isEnabled,
            onCheckedChange = { onEnable(it) }
        )
    }
}

private val horizontalPaddingDebugSwitch = 5.dp
private val verticalPaddingDebugSwitch = 2.dp
private val debugSwitchPadding = 8.dp
private val fontSizeDebug = 11.sp
