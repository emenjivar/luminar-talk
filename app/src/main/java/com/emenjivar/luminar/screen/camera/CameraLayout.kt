package com.emenjivar.luminar.screen.camera

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme

/**
 * @param rawCameraPreview original image preview directly from the camera.
 *  This layout is rendered in the bottom of the layers.
 * @param filterCameraPreview camera image preview but with some filters applied.
 *  This layout is rendered in front of the cameraContent but behind cameraControls.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenLayout(
    modifier: Modifier = Modifier,
    rawCameraPreview: @Composable BoxScope.(Modifier) -> Unit,
    filteredCameraPreview: @Composable BoxScope.(Modifier) -> Unit
) {
    val enableDebug = remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        contentColor = Color.Transparent
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            rawCameraPreview(
                Modifier.fillMaxSize()
            )
            
            if (enableDebug.value) {
                Box(modifier = modifier.fillMaxSize().background(Color.Black))
            }

            if (enableDebug.value) {
                filteredCameraPreview(
                    Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .background(color = Color.Black, shape = CircleShape)
                    .padding(horizontal = 5.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Debug",
                    color = Color.White,
                    fontSize = 11.sp
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

@Composable
@Preview
private fun CameraScreenLayoutTorchOnPreview() {
    RealTimeCameraFilterTheme {
        CameraScreenLayout(
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
