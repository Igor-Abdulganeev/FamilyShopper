package ru.gorinih.familyshopper.ui.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp

/**
 * Created by Igor Abdulganeev on 03.08.2026
 */

@Composable
fun AgslAnimated(
    startedColor: Color,
    endedColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val cardShader = remember { AgslParams(ANIMATED_GRADIENT_SHADER) }
    val paint = remember { Paint() }

    val timeSeconds by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(120_000, easing = LinearEasing)
        )
    )

    Box(
        modifier = modifier.clip(RoundedCornerShape(16.dp))
            .drawBehind {
                val width = size.width
                val height = size.height

                if (width > 0 && height > 0) {
                    cardShader.setUniform("u_resolution", width, height)
                    cardShader.setUniform("u_time", timeSeconds)
                    cardShader.setUniform("u_color1", startedColor)
                    cardShader.setUniform("u_color2", endedColor)

                    paint.shader = cardShader.asShader()

                    drawIntoCanvas { canvas ->
                        canvas.drawRect(
                            left = 0f,
                            top = 0f,
                            right = width,
                            bottom = height,
                            paint = paint
                        )
                    }
                }

            }
    ) {
        content()
    }

}