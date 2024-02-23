package com.emenjivar.luminar.screen.camera

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
    rawCameraPreview: @Composable (Modifier) -> Unit,
) {
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
            }
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
            }
        )
    }
}
