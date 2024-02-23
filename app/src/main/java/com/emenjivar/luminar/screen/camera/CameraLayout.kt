package com.emenjivar.luminar.screen.camera

import android.annotation.SuppressLint
import androidx.camera.core.TorchState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme
import com.emenjivar.luminar.ui.widget.RoundedButton

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
    torchState: Int?,
    modifier: Modifier = Modifier,
    onToggleTorch: (Boolean) -> Unit,
    onFlipCamera: () -> Unit,
    rawCameraPreview: @Composable (Modifier) -> Unit,
    filterCameraPreview: @Composable (Modifier) -> Unit,
    bottomControllers: @Composable BoxScope.(Modifier) -> Unit
) {
    var isTorchEnabled by remember(torchState) {
        mutableStateOf(torchState == TorchState.ON)
    }
    val torchIcon = remember(isTorchEnabled) {
        if (isTorchEnabled) {
            R.drawable.ic_flash_on
        } else {
            R.drawable.ic_flash_off
        }
    }

    Scaffold(
        modifier = modifier,
        contentColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    RoundedButton(
                        iconRes = R.drawable.ic_flip_camera,
                        onClick = onFlipCamera
                    )
                },
                actions = {
                    RoundedButton(
                        iconRes = torchIcon,
                        onClick = {
                            onToggleTorch(!isTorchEnabled)
                            isTorchEnabled = !isTorchEnabled
                        }
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            rawCameraPreview(
                Modifier.fillMaxSize()
            )
            filterCameraPreview(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
            )

            bottomControllers(
                Modifier
                    .align(Alignment.BottomCenter)
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
            },
            torchState = TorchState.ON,
            onToggleTorch = {},
            onFlipCamera = {},
            filterCameraPreview = {},
            bottomControllers = {}
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
            torchState = TorchState.OFF,
            onToggleTorch = {},
            onFlipCamera = {},
            filterCameraPreview = {},
            bottomControllers = {}
        )
    }
}
