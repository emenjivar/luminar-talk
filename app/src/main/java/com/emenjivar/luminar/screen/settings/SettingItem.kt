package com.emenjivar.luminar.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emenjivar.luminar.ui.theme.AppTheme
import com.emenjivar.luminar.ui.theme.AppTypography

@Composable
fun SettingItem(
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = paddingItem)
            .padding(top = paddingItem)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = AppTypography.h1
            )
            Text(
                text = value,
                style = AppTypography.captionCaption,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp
            )
        }
        Text(
            text = description,
            style = AppTypography.captionCaption
        )

        HorizontalDivider(
            modifier = Modifier.padding(top = paddingItem),
            thickness = 0.5.dp,
            color = Color.Black.copy(alpha = 0.2f)
        )
    }
}

private val paddingItem = 20.dp

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SettingItemPreview() {
    AppTheme {
        SettingItem(
            title = "Circularity",
            value = "0 to 50",
            description = "Determines the radius for the light blobs.",
            onClick = {}
        )
    }
}
