package com.emenjivar.luminar.ui.shapes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.ui.components.ToolTipDirection
import com.emenjivar.luminar.ui.theme.RealTimeCameraFilterTheme

class ToolTipShape(
    private val tipOrientation: ToolTipDirection
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val tipSizePx = with(density) { tipSize.toPx() }


        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = tipSizePx,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    cornerRadius = CornerRadius(x = cornerRadiusPx, y = cornerRadiusPx)
                )
            )
            if (tipOrientation == ToolTipDirection.START) {
                moveTo(
                    x = tipSizePx,
                    y = size.height / 2 - tipSizePx / 2
                )
                lineTo(
                    x = 0f,
                    y = size.height / 2
                )
                lineTo(
                    x = tipSizePx,
                    y = size.height / 2 + tipSizePx / 2
                )
                close()
            }
        }
        return Outline.Generic(path)
    }
}

private val tipSize = 4.dp
private val cornerRadius = 16.dp

@Preview
@Composable
private fun ToolTipShapePreview() {
    RealTimeCameraFilterTheme {
        Box(
            modifier = Modifier
                .clip(ToolTipShape(ToolTipDirection.START))
                .size(50.dp)
                .background(Color.DarkGray)
        )
    }
}