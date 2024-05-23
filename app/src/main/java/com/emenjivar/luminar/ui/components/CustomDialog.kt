package com.emenjivar.luminar.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Composable
fun CustomDialog(
    title: String,
    modifier: Modifier = Modifier,
    controller: CustomDialogController,
    description: String? = null,
    confirmAction: CustomDialogAction? = null,
    dismissAction: CustomDialogAction? = null
) {
    val isDisplayed by controller.isDisplayed.collectAsState()

    if (isDisplayed) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { controller.hide() },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = description.orEmpty())
            },
            confirmButton = {
                if (confirmAction != null) {
                    TextButton(
                        onClick = { confirmAction.onClick() }
                    ) {
                        Text(text = confirmAction.text)
                    }
                }
            },
            dismissButton = {
                if (dismissAction != null) {
                    TextButton(
                        onClick = {
                            dismissAction.onClick()
                            controller.hide()
                        }
                    ) {
                        Text(text = dismissAction.text)
                    }
                }
            }
        )
    }
}

data class CustomDialogAction(
    val text: String,
    val onClick: () -> Unit
)

class CustomDialogController {
    private val _isDisplayed = MutableStateFlow(false)
    val isDisplayed: StateFlow<Boolean> = _isDisplayed

    fun show() {
        _isDisplayed.update { true }
    }

    fun hide() {
        _isDisplayed.update { false }
    }
}

@Composable
fun rememberCustomDialogController(): CustomDialogController {
    return remember {
        CustomDialogController()
    }
}

@Preview
@Composable
private fun CustomDialogPreview() {
    val dialogController = rememberCustomDialogController().apply {
        show()
    }

    AppTheme {
        CustomDialog(
            controller = dialogController,
            title = stringResource(R.string.camera_dialog_access_title),
            description = stringResource(R.string.camera_dialog_access_description),
            confirmAction = CustomDialogAction(
                text = stringResource(R.string.button_settings),
                onClick = {}
            ),
            dismissAction = CustomDialogAction(
                text = stringResource(R.string.button_settings),
                onClick = {}
            )
        )
    }
}
