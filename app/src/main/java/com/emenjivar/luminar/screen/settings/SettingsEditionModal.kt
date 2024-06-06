package com.emenjivar.luminar.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emenjivar.luminar.ext.twoDecimals
import com.emenjivar.luminar.ui.components.buttons.ActionButton
import com.emenjivar.luminar.ui.theme.AppTheme
import com.emenjivar.luminar.ui.theme.AppTypography
import kotlin.math.roundToInt

@Composable
fun SettingsEditionModal(
    title: String,
    modifier: Modifier = Modifier,
    min: Float,
    max: Float,
    initialSelection: SettingsSliderSelection,
    onSaveClick: (SettingsSliderSelection) -> Unit,
    onCancelClick: () -> Unit
) {
    val sliderSelection = remember {
        // Put a default value to avoid the nullable warning
        mutableStateOf<SettingsSliderSelection>(SettingsSliderSelection.Single(0))
    }
    val textMeasure = rememberTextMeasurer()
    val sliderSelectorTextStyle = remember {
        AppTypography.captionCaption.copy(
            fontSize = tooltipFontSize,
            color = Color.White
        )
    }

    Column(
        modifier = modifier
            .background(Color.White)
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
    ) {
        Text(
            text = title,
            style = AppTypography.h1,
            textAlign = TextAlign.Center
        )

        when (initialSelection) {
            is SettingsSliderSelection.Single -> {
                val sliderState = remember(initialSelection.value) {
                    mutableFloatStateOf(initialSelection.value.toFloat())
                }
                Slider(
                    modifier = Modifier
                        .padding(top = sliderSelectorHeight)
                        .drawToolTip(
                            text = "${sliderState.floatValue.roundToInt()}",
                            value = { sliderState.floatValue },
                            min = min,
                            max = max,
                            textMeasurer = textMeasure,
                            textStyle = sliderSelectorTextStyle
                        ),
                    value = sliderState.floatValue,
                    valueRange = min..max,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Black,
                        activeTrackColor = Color.Black
                    ),
                    onValueChange = {
                        sliderState.floatValue = it
                    },
                    onValueChangeFinished = {
                        sliderSelection.value = SettingsSliderSelection.Single(
                            value = sliderState.floatValue.roundToInt()
                        )
                    }
                )
            }

            is SettingsSliderSelection.Range -> {
                val sliderState = remember(initialSelection.range) {
                    mutableStateOf(initialSelection.range)
                }
                RangeSlider(
                    modifier = Modifier
                        .padding(top = sliderSelectorHeight)
                        .drawToolTip(
                            text = sliderState.value.start.twoDecimals(),
                            value = { sliderState.value.start },
                            min = min,
                            max = max,
                            textMeasurer = textMeasure,
                            textStyle = sliderSelectorTextStyle
                        )
                        .drawToolTip(
                            text = sliderState.value.endInclusive.twoDecimals(),
                            value = { sliderState.value.endInclusive },
                            min = min,
                            max = max,
                            textMeasurer = textMeasure,
                            textStyle = sliderSelectorTextStyle
                        ),
                    value = sliderState.value,
                    valueRange = min..max,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Black,
                        activeTrackColor = Color.Black
                    ),
                    onValueChange = {
                        sliderState.value = it
                    },
                    onValueChangeFinished = {
                        sliderSelection.value = SettingsSliderSelection.Range(sliderState.value)
                    }
                )
            }
        }

        ActionButton(
            text = "Save",
            isPrimaryAction = true,
            onClick = { onSaveClick(sliderSelection.value) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ActionButton(
            text = "Cancel",
            isPrimaryAction = false,
            onClick = onCancelClick
        )
    }
}

sealed class SettingsSliderSelection {
    data class Range(val range: ClosedFloatingPointRange<Float>) : SettingsSliderSelection()
    data class Single(val value: Int) : SettingsSliderSelection()
}

private fun Modifier.drawToolTip(
    text: String,
    value: () -> Float,
    min: Float,
    max: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle
) = this.drawWithContent {
    drawContent()

    val textSize = textMeasurer.measure(
        text = text,
        style = textStyle
    )
    val tooltipWidth = textSize.size.width.toFloat() + tooltipHorizontalPadding.toPx()

    // Selector used for changing the value of the slider
    val selectorWidth = sliderSelectorWidth.toPx()
    val selectorHeight = sliderSelectorHeight.toPx()
    val selectorRadius = sliderSelectorRadius.toPx()

    val progress = (value() - min) / (max - min)
    val centerPivot = (size.width - selectorWidth) * progress
    val tipSize = tipSize.toPx()

    translate(
        top = -(selectorHeight / 2f + tipSize),
        left = centerPivot
    ) {
        drawRoundRect(
            color = Color.Black,
            size = Size(
                width = tooltipWidth,
                height = selectorHeight
            ),
            topLeft = Offset(
                x = -(tooltipWidth / 2f) + selectorWidth / 2f,
                y = 0f
            ),
            cornerRadius = CornerRadius(
                x = selectorRadius,
                y = selectorRadius
            )
        )

        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = textStyle,
            topLeft = Offset(
                x = selectorWidth / 2f - textSize.size.width / 2f,
                y = selectorHeight / 2f - textSize.size.height / 2f
            )
        )

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(x = tipSize, y = 0f)
            lineTo(x = tipSize / 2f, y = tipSize)
            close()
        }

        translate(
            top = selectorHeight,
            left = selectorWidth / 2f - tipSize / 2f
        ) {
            drawPath(
                path = path,
                color = Color.Black
            )
        }
    }

}


private val horizontalPadding = 20.dp
private val verticalPadding = 24.dp
private val tipSize = 6.dp

private val sliderSelectorWidth = 20.dp
private val sliderSelectorHeight = 30.dp
private val sliderSelectorRadius = 15.dp
private val tooltipHorizontalPadding = 20.dp
private val tooltipFontSize = 12.sp

@Preview
@Composable
private fun SettingsEditionModalPreview() {
    AppTheme {
        SettingsEditionModal(
            title = "Circularity",
            min = 40f,
            max = 100f,
            initialSelection = SettingsSliderSelection.Single(40),
            onSaveClick = { },
            onCancelClick = {}
        )
    }
}

@Preview
@Composable
private fun SettingsEditionModal50Preview() {
    AppTheme {
        SettingsEditionModal(
            title = "Circularity",
            min = 40f,
            max = 100f,
            initialSelection = SettingsSliderSelection.Single(60),
            onSaveClick = { },
            onCancelClick = {}
        )
    }
}

@Preview
@Composable
private fun SettingsEditionModal100Preview() {
    AppTheme {
        SettingsEditionModal(
            title = "Circularity",
            min = 40f,
            max = 100f,
            initialSelection = SettingsSliderSelection.Single(100),
            onSaveClick = { },
            onCancelClick = {}
        )
    }
}

@Preview
@Composable
private fun SettingsEditionModalMultiplePreview() {
    AppTheme {
        SettingsEditionModal(
            title = "Circularity",
            min = 40f,
            max = 200f,
            initialSelection = SettingsSliderSelection.Range(40f..200f),
            onSaveClick = { },
            onCancelClick = {}
        )
    }
}

@Preview
@Composable
private fun SettingsEditionModalMultipleBetweenSelectionPreview() {
    AppTheme {
        SettingsEditionModal(
            title = "Circularity",
            min = 40f,
            max = 200f,
            initialSelection = SettingsSliderSelection.Range(80f..150f),
            onSaveClick = { },
            onCancelClick = {}
        )
    }
}
