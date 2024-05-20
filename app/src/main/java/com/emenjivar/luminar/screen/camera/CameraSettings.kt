package com.emenjivar.luminar.screen.camera

import android.util.Range
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ui.components.CustomSlider
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraSettings(
    modifier: Modifier = Modifier,
    circularityRange: () -> Range<Float>,
    blobRadiusRange: () -> Range<Float>,
    lightBPM: () -> Int,
    onSetCircularity: (Range<Float>) -> Unit,
    onSetBlobRadius: (Range<Float>) -> Unit,
    onSetLightBPM: (Int) -> Unit,
    onResetClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState(isPersistent = true)

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
            .padding(
                horizontal = 32.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomSlider(
            fieldName = stringResource(id = R.string.settings_circularity),
            instructions = stringResource(id = R.string.settings_circularity_help),
            value = circularityRange,
            valueRange = Range(0f, CIRCULARITY_MAX),
            onValueChange = onSetCircularity
        )

        CustomSlider(
            fieldName = stringResource(id = R.string.settings_radius),
            instructions = stringResource(id = R.string.settings_radius_help),
            value = blobRadiusRange,
            valueRange = Range(0f, RADIUS_MAX),
            onValueChange = onSetBlobRadius
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${stringResource(id = R.string.settings_bpm)} (${lightBPM()})",
                color = Color.White
            )
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(
                            text = stringResource(id = R.string.settings_bpm_help),
                            color = Color.White
                        )
                    }
                },
                state = tooltipState
            ) {
                Icon(
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    },
                    painter = painterResource(R.drawable.ic_question_circle),
                    tint = Color.White,
                    contentDescription = stringResource(id = R.string.settings_field_content_description, "bpm")
                )
            }
        }
        Row {
            Slider(
                value = lightBPM().toFloat(),
                valueRange = 40f..100f,
                onValueChange = { value ->
                    onSetLightBPM(value.toInt())
                }
            )
        }

        Button(
            onClick = onResetClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = BorderStroke(width = 1.dp, color = Color.White)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Reset configuration",
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
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
            lightBPM = { 0 },
            onSetCircularity = {},
            onSetBlobRadius = {},
            onSetLightBPM = {},
            onResetClick = {}
        )
    }
}
