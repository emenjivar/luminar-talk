package com.emenjivar.luminar.screen.camera

import android.util.Range
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.ui.components.CustomSlider
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme

@Composable
fun CameraSettings(
    modifier: Modifier = Modifier,
    circularityRange: () -> Range<Float>,
    blobRadiusRange: () -> Range<Float>,
    onSetCircularity: (Range<Float>) -> Unit,
    onSetBlobRadius: (Range<Float>) -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Black
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomSlider(
            fieldName = "Circularity",
            value = circularityRange,
            valueRange = Range(0f, CIRCULARITY_MAX),
            onValueChange = onSetCircularity
        )

        CustomSlider(
            fieldName = "Circle radius",
            value = blobRadiusRange,
            valueRange = Range(0f, RADIUS_MAX),
            onValueChange = onSetBlobRadius
        )
    }
}

private const val CIRCULARITY_MAX = 1f
private const val RADIUS_MAX = 200f

// Preview constants
private const val UPPER_RANGE_CIRCULARITY = 1f
private const val UPPER_RANGE_BLOB = 100f

@Preview
@Composable
private fun CameraSettingsPreview() {
    RealTimeCameraFilterTheme {
        CameraSettings(
            circularityRange = { Range(0f, UPPER_RANGE_CIRCULARITY) },
            blobRadiusRange = { Range(0f, UPPER_RANGE_BLOB) },
            onSetCircularity = {},
            onSetBlobRadius = {}
        )
    }
}
