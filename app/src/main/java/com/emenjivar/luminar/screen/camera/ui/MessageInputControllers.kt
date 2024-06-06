package com.emenjivar.luminar.screen.camera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ui.theme.AppTheme
import com.emenjivar.luminar.ui.theme.AppTypography

/**
 * Input field and send button.
 * @param initialValue Initial text displayed on the input.
 * @param onClickSend Callback called when click on the button, contains the inputField text.
 */
@Composable
fun MessageInputControllers(
    modifier: Modifier = Modifier,
    initialValue: String = "",
    onClickSend: (String) -> Unit
) {
    val fieldValue = remember { mutableStateOf(initialValue) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BasicTextField(
            modifier = Modifier.weight(1f),
            value = fieldValue.value,
            textStyle = AppTypography.captionCaption,
            onValueChange = {
                fieldValue.value = it
            },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .height(inputHeight)
                        .background(color = Color.White, shape = CircleShape)
                        .border(
                            width = 1.dp,
                            color = Color.Black.copy(alpha = BORDER_ALPHA),
                            shape = CircleShape
                        )
                        .padding(horizontal = horizontalPaddingText),
                    contentAlignment = Alignment.CenterStart
                ) {
                    innerTextField()

                    if (fieldValue.value.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.placeholder_message),
                            style = AppTypography.captionCaption,
                            color = Color.Black.copy(alpha = PLACEHOLDER_ALPHA)
                        )
                    }
                }
            }
        )

        IconButton(
            modifier = Modifier.size(inputHeight),
            onClick = { onClickSend(fieldValue.value) },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color.White,
                containerColor = Color.Black
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = stringResource(id = R.string.content_description_emit)
            )
        }
    }
}

private val inputHeight = 50.dp
private val horizontalPaddingText = 16.dp

private const val BORDER_ALPHA = 0.2f
private const val PLACEHOLDER_ALPHA = 0.7f

@Preview
@Composable
private fun MessageInputPreview() {
    AppTheme {
        MessageInputControllers(
            initialValue = "This is my message",
            onClickSend = {}
        )
    }
}

@Preview
@Composable
private fun MessageInputPlaceholderPreview() {
    AppTheme {
        MessageInputControllers(
            initialValue = "",
            onClickSend = {}
        )
    }
}
