package com.emenjivar.luminar.screen.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.zIndex
import com.emenjivar.luminar.ui.theme.AppTheme

/**
 * This composable render the camera preview along with an option debug overlay,
 * with is useful for displayed openCV debug data.
 *
 * @param isDebugEnabled Whether to display the debug overlay. Set to true to overlap
 *  the [debugPreview] over the [rawPreview]. Set to false to hide the debug overlay.
 * @param rawPreview The preview for the camera.
 * @param debugPreview The Preview used for debugging the openCV output images.
 */
@Composable
fun DualCameraPreview(
    isDebugEnabled: Boolean,
    modifier: Modifier = Modifier,
    rawPreview: @Composable BoxScope.(Modifier) -> Unit,
    debugPreview: @Composable BoxScope.(Modifier) -> Unit
) {
    Box(modifier = modifier) {
        rawPreview(
            Modifier
                .fillMaxSize()
                .zIndex(Z_INDEX_RAW))

        if (isDebugEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .zIndex(Z_INDEX_DEBUG)
            )
            debugPreview(
                Modifier
                    .fillMaxSize()
                    .zIndex(Z_INDEX_DEBUG))
        }
    }
}

private const val Z_INDEX_RAW = 1f
private const val Z_INDEX_DEBUG = 2f

private class BooleanParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}

@Preview
@Composable
private fun CameraPreviewLayoutPreview(
    @PreviewParameter(BooleanParameterProvider::class) isDebugEnabled: Boolean
) {
    AppTheme {
        DualCameraPreview(
            isDebugEnabled = isDebugEnabled,
            rawPreview = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Raw preview",
                        color = Color.White
                    )
                }
            },
            debugPreview = {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Debug preview",
                    color = Color.White
                )
            }
        )
    }
}
