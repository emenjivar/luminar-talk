package com.emenjivar.luminar.ui.components

import android.util.Range
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
import com.emenjivar.luminar.R
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    fieldName: String,
    instructions: String,
    value: () -> Range<Float>,
    valueRange: Range<Float>,
    modifier: Modifier = Modifier,
    onValueChange: (Range<Float>) -> Unit
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = fieldName, color = Color.White)
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(
                            text = instructions,
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
                    contentDescription = stringResource(id = R.string.settings_field_content_description, fieldName)
                )
            }
        }
        RangeSlider(
            value = value().lower..value().upper,
            valueRange = valueRange.lower..valueRange.upper,
            onValueChange = { onValueChange(it.toRange()) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = roundOffDecimals(value().lower),
                color = Color.White
            )
            Text(
                text = roundOffDecimals(value().upper),
                color = Color.White
            )
        }
    }
}

private fun roundOffDecimals(value: Float): String {
    val format = DecimalFormat("#.##")
    format.roundingMode = RoundingMode.CEILING
    return format.format(value)
}
