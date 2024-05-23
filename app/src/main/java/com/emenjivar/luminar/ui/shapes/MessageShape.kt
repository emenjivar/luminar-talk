package com.emenjivar.luminar.ui.shapes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.emenjivar.luminar.ui.theme.AppTheme

class MessageShape(
    private val tipOrientation: TipOrientation
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
                    left = if (tipOrientation == TipOrientation.LEFT) {
                        tipSizePx
                    } else {
                        0f
                    },
                    top = 0f,
                    right = if (tipOrientation == TipOrientation.LEFT) {
                        size.width
                    } else {
                        size.width - tipSizePx
                    },
                    bottom = size.height,
                    cornerRadius = CornerRadius(x = cornerRadiusPx, y = cornerRadiusPx)
                )
            )

            if (tipOrientation == TipOrientation.LEFT) {
                moveTo(x = tipSizePx, y = size.height - cornerRadiusPx)
                lineTo(x = 0f, y = size.height)
                lineTo(x = tipSizePx + cornerRadiusPx, y = size.height)
            } else {
                moveTo(x = size.width - tipSizePx, y = size.height - cornerRadiusPx)
                lineTo(x = size.width - tipSizePx - cornerRadiusPx, y = size.height)
                lineTo(x = size.width, y = size.height)
            }
            close()
        }
        return Outline.Generic(path)
    }

    private val tipSize = 15.dp
    private val cornerRadius = 15.dp
}

enum class TipOrientation {
    LEFT,
    RIGHT
}

@Preview
@Composable
private fun MessageShapePreview() {
    AppTheme {
        Column {
            Box(
                modifier = Modifier
                    .clip(MessageShape(TipOrientation.LEFT))
                    .size(50.dp)
                    .background(Color.DarkGray)
            )
            Box(
                modifier = Modifier
                    .clip(MessageShape(TipOrientation.RIGHT))
                    .size(50.dp)
                    .background(Color.DarkGray)
            )
        }
    }
}
