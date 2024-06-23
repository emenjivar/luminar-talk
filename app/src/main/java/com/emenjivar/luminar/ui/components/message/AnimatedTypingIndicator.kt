package com.emenjivar.luminar.ui.components.message

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emenjivar.luminar.ui.theme.AppTheme
import kotlin.math.abs

/**
 * Display an animated typing indicator with a configurable number of dots.
 *
 * @param modifier The modifier to be applied to the component.
 * @param configuration The configuration for the component's appearance.
 * @param totalDots The total number of dots displayed in the indicator.
 */
@Composable
fun AnimatedTypingIndicator(
    modifier: Modifier = Modifier,
    configuration: TypingIndicatorDefaults.Configuration = TypingIndicatorDefaults.defaultConfiguration(),
    totalDots: Int = 3,
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "Typing indicator animation"
    )

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Typing indicator progress animation"
    )

    // Default values in case of undefined sizes in the modifier.
    val defaultWidth = remember {
        val spaceCircles = configuration.circleTargetSize * totalDots
        val spaceBetweenCircles = configuration.spaceBetweenCircles * (totalDots + 1)
        spaceCircles + spaceBetweenCircles
    }
    val defaultHeight = remember {
        configuration.circleTargetSize * 2
    }

    Canvas(modifier = modifier
        .width(defaultWidth)
        .height(defaultHeight)) {
        val startRadiusPX = configuration.circleStartSize.toPx() / 2f
        val targetRadiusPX = configuration.circleTargetSize.toPx() / 2f

        repeat(totalDots) { index ->
            val proximity = getShiftedProximity(
                progress = progress,
                totalCircles = totalDots,
                indexCircle = index
            )

            drawCircle(
                color = lerp(
                    start = configuration.inactiveColor,
                    stop = configuration.activeColor,
                    fraction = proximity
                ),
                radius = startRadiusPX + (targetRadiusPX - startRadiusPX) * proximity,
                center = Offset(
                    x = size.width / totalDots * index + size.width / (2 * totalDots),
                    y = center.y
                )
            )
        }
    }
}

/**
 * Calculates the middle of the horizontal area allocated for a specific dot.
 * This function is used to determine the central position of each dot area in the indicator.
 * The horizontal space is divided equally among all dots.
 *
 * @param totalCircles The total number of dots in the indicator.
 * @param indexCircle The index of the dot (0-based) for which the middle point is being calculated.
 */
private fun getDotCenterPosition(
    totalCircles: Int,
    indexCircle: Int
) = (indexCircle * 2 * totalCircles + totalCircles) / (2 * totalCircles * totalCircles).toFloat()

/**
 * Determines the proximity of [progress] to the middle of the calculated using [indexCircle].
 *
 * @param progress The current progress value.
 * @param totalCircles The total number of dots in the indicator.
 * @param indexCircle The index of the dot (0-based) for which the proximity is being calculated.
 * @return A value between 0 and 1, where 0 represents the farthest distance from the center
 *  and 1 represents the closest distance.
 */
private fun getProximity(
    progress: Float,
    totalCircles: Int,
    indexCircle: Int
): Float {
    val getDotCenterPosition = getDotCenterPosition(totalCircles, indexCircle)
    val dotArea = 1 / totalCircles.toFloat()
    return (1 - abs(progress - getDotCenterPosition) / dotArea).coerceAtLeast(0f).coerceAtMost(1f)
}

/**
 * Determines the proximity of [progress] to the point calculated using [indexCircle].
 * When [progress] is increasing and is near to the end, the first dot start increasing its size.
 * Conversely, [progress] is increasing near to the start, the last dot start decreasing its size.
 *
 * Proximity is a value between 0 and 1,
 * where 0 means [progress] is far from [indexCircle]
 * and 1 means [progress] is exactly at the point of [indexCircle].
 *
 * @param progress The current progress value.
 * @param totalCircles The total number of dots in the indicator.
 * @param indexCircle The index of the dot (0-based) for which the proximity is being calculated.
 * @return The proximity for the current dot.
 */
private fun getShiftedProximity(
    progress: Float,
    totalCircles: Int,
    indexCircle: Int
): Float {
    val currentProximity = getProximity(
        progress = progress,
        totalCircles = totalCircles,
        indexCircle = indexCircle
    )

    val lastIndex = totalCircles - 1
    val dotArea = 1f / totalCircles
    val startThreshold = dotArea / 2f
    val endThreshold = dotArea * lastIndex + (dotArea / 2f)

    val isProgressNearToEnd = progress > endThreshold
    val isProgressNearToStart = progress < startThreshold

    val shiftedProximityAdjustment = when {
        indexCircle == 0 && isProgressNearToEnd -> {
            1 - getProximity(
                progress = progress,
                totalCircles = totalCircles,
                indexCircle = totalCircles - 1
            )
        }

        indexCircle == (totalCircles - 1) && isProgressNearToStart -> {
            1 - getProximity(
                progress = progress,
                totalCircles = totalCircles,
                indexCircle = 0
            )
        }

        else -> 0f
    }

    return currentProximity + shiftedProximityAdjustment
}

private val startSize = 8.dp
private val targetSize = 9.dp

object TypingIndicatorDefaults {

    fun defaultConfiguration() = Configuration()

    data class Configuration(
        val circleStartSize: Dp = startSize,
        val circleTargetSize: Dp = targetSize,
        val activeColor: Color = Color.White,
        val inactiveColor: Color = Color.Gray,
        val spaceBetweenCircles: Dp = startSize
    )
}

@Composable
@Preview
private fun AnimatedTypingIndicatorPreview() {
    TypingIndicatorDefaults.defaultConfiguration()
    AppTheme {
        AnimatedTypingIndicator(
            modifier = Modifier,
            totalDots = 4
        )
    }
}
