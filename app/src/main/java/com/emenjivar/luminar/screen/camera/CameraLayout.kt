package com.emenjivar.luminar.screen.camera

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emenjivar.luminar.ui.components.MessageBubble
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme

/**
 * @param morse Current morse characters that will form a letter or number.
 * @param messages List of messages.
 * @param rawCameraPreview original image preview directly from the camera.
 *  This layout is rendered in the bottom of the layers.
 * @param filteredCameraPreview camera image preview but with some filters applied.
 *  This layout is rendered in front of the cameraContent but behind cameraControls.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Stable
fun CameraScreenLayout(
    modifier: Modifier = Modifier,
    morse: String,
    messages: List<String>,
    rawCameraPreview: @Composable BoxScope.(Modifier) -> Unit,
    filteredCameraPreview: @Composable BoxScope.(Modifier) -> Unit
) {
    val enableDebug = remember { mutableStateOf(false) }
    val verticalScroll = rememberScrollState()
    val verticalJumpPx = with(LocalDensity.current) { verticalJump.toPx() }

    LaunchedEffect(messages) {
        // Scroll to the bottom after receiving a new message
        verticalScroll.animateScrollBy(verticalJumpPx * messages.size)
    }

    Scaffold(
        modifier = modifier,
        contentColor = Color.Transparent
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            rawCameraPreview(Modifier.fillMaxSize())

            if (enableDebug.value) {
                // Use this box to cover the rawCamera composable
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
                filteredCameraPreview(
                    Modifier
                        .fillMaxSize()
                )
            }

            if (morse.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(roundedShape)
                        )
                        .padding(horizontal = horizontalPaddingMorseText),
                    text = morse,
                    fontSize = fontSizeMorseText,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightMessages)
                    .padding(paddingMessages)
                    .align(Alignment.BottomCenter)
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

            Row(
                modifier = Modifier
                    .padding(debugSwitchPadding)
                    .align(Alignment.BottomEnd)
                    .background(color = Color.Black, shape = CircleShape)
                    .padding(
                        horizontal = horizontalPaddingDebugSwitch,
                        vertical = verticalPaddingDebugSwitch
                    ),
                horizontalArrangement = Arrangement.spacedBy(debugSwitchPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Debug",
                    color = Color.White,
                    fontSize = fontSizeDebug
                )
                Switch(
                    checked = enableDebug.value,
                    onCheckedChange = {
                        enableDebug.value = it
                    }
                )
            }
        }
    }
}

private val roundedShape = 15.dp
private val horizontalPaddingMorseText = 15.dp
private val fontSizeMorseText = 25.sp

private val paddingMessages = 3.dp
private val heightMessages = 300.dp

private val debugSwitchPadding = 8.dp
private val horizontalPaddingDebugSwitch = 5.dp
private val verticalPaddingDebugSwitch = 2.dp
private val fontSizeDebug = 11.sp

private val verticalJump = 20.dp

@Composable
@Preview
private fun CameraScreenLayoutTorchOnPreview() {
    RealTimeCameraFilterTheme {
        CameraScreenLayout(
            morse = "",
            messages = emptyList(),
            rawCameraPreview = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Camera should be displayed here")
                }
            },
            filteredCameraPreview = {}
        )
    }
}

@Composable
@Preview
private fun CameraScreenLayoutTorchOffPreview() {
    RealTimeCameraFilterTheme {
        CameraScreenLayout(
            morse = "",
            messages = emptyList(),
            rawCameraPreview = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Camera should be displayed here")
                }
            },
            filteredCameraPreview = {}
        )
    }
}
