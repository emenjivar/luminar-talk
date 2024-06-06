package com.emenjivar.luminar.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.ui.theme.AppTheme
import com.emenjivar.luminar.ui.theme.AppTypography

/**
 * Represent a standard button with a text label.
 *
 * @param text The text to display on the button.
 * @param modifier The modifier to be applied to the button.
 * @param isPrimaryAction Whether the button has a primary action button style or not.
 */
@Composable
fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    isPrimaryAction: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .height(buttonHeight)
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimaryAction) {
                Color.Black
            } else {
                Color.White
            },
            contentColor = if (isPrimaryAction) {
                Color.White
            } else {
                Color.Black
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isPrimaryAction) {
                Color.Black
            } else {
                Color.Black.copy(alpha = BORDER_ALPHA)
            }
        )
    ) {
        Text(
            text = text,
            style = AppTypography.captionButton
        )
    }
}

private val buttonHeight = 52.dp
private const val BORDER_ALPHA = 0.5f

@Preview
@Composable
private fun ActionButtonPrimaryPreview() {
    AppTheme {
        ActionButton(
            text = "Save",
            isPrimaryAction = true,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ActionButtonSecondaryPreview() {
    AppTheme {
        ActionButton(
            text = "Cancel",
            isPrimaryAction = false,
            onClick = {}
        )
    }
}
