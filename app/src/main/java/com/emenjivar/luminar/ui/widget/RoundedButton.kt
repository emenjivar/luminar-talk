package com.emenjivar.luminar.ui.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedButton(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = roundedButtonSize,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.size(size),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null
        )
    }
}

private val roundedButtonSize = 50.dp
