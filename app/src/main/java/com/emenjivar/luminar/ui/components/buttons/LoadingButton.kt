package com.emenjivar.luminar.ui.components.buttons

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ext.isInProgress
import com.emenjivar.luminar.ui.theme.AppTheme

@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    onClick: (action: LoadingButtonAction) -> Unit,
    progress: () -> Float
) {
    val isLoading by remember {
        derivedStateOf { progress().isInProgress() }
    }
    Crossfade(
        targetState = isLoading,
        label = "Loading button animation"
    ) { shouldRenderProgressButton ->
        if (shouldRenderProgressButton) {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(sizeProgress),
                    strokeWidth = borderProgress,
                    color = Color.Black,
                    progress = progress
                )

                IconButton(
                    modifier = Modifier.size(sizeProgress - borderProgress - buttonPadding),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Black
                    ),
                    onClick = { onClick(LoadingButtonAction.STOP_CLICK) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_stop),
                        contentDescription = stringResource(id = R.string.content_description_emit)
                    )
                }
            }
        } else {
            IconButton(
                modifier = Modifier.size(sizeProgress),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Black
                ),
                onClick = { onClick(LoadingButtonAction.CLICK) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = stringResource(id = R.string.content_description_emit)
                )
            }
        }
    }
}

enum class LoadingButtonAction {
    CLICK,
    STOP_CLICK
}

private val sizeProgress = 50.dp
private val borderProgress = 2.dp
private val buttonPadding = 5.dp

@Preview
@Composable
private fun LoadingButtonStaticPreview() {
    AppTheme {
        LoadingButton(
            progress = { 0f },
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun LoadingButtonInProgressPreview() {
    AppTheme {
        LoadingButton(
            progress = { 0.5f },
            onClick = {}
        )
    }
}
