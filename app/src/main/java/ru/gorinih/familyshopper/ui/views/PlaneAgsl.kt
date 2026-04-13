package ru.gorinih.familyshopper.ui.views

import android.graphics.Paint
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

/**
 * Created by Igor Abdulganeev on 13.04.2026
 */

private const val ANIMATED_GRADIENT_SHADER = """
uniform float2 u_resolution;
uniform float  u_time;
uniform float3 u_color1;
uniform float3 u_color2;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / u_resolution;
    // центрируем координаты
    uv = uv * 2.0 - 1.0;
    float t = u_time * 0.12;
    // несколько плавных волн
    float wave1 = sin((1.0 - uv.x) * 2.5 + t);
    float wave2 = sin(uv.y * 2.0 - t * 0.7);
    float wave3 = sin((uv.x + uv.y) * 1.5 + t * 0.5);
    // смешиваем
    float flow = (wave1 + wave2 + wave3) / 3.0;
    // нормализуем 0..1
    float gradient = flow * 0.5 + 0.5;
    // делаем мягче
    gradient = smoothstep(0.1, 0.9, gradient);
    half3 color = mix(u_color1, u_color2, gradient);
    return half4(color, 1.0);
}
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AnimatedGradientAGSL(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
    startedColor: Color,
    endedColor: Color,
) {
    val runtimeShader = remember(Unit) { RuntimeShader(ANIMATED_GRADIENT_SHADER) }
    val paint = remember { Paint() }
    val timeSeconds by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(120_000, easing = LinearEasing) // 2 минуты на цикл
        )
    )

    val colorStart = startedColor.convert(ColorSpaces.Srgb)
    val colorEnd = endedColor.convert(ColorSpaces.Srgb)

    Box(
        modifier = modifier.drawBehind {
            val width = size.width
            val height = size.height

            runtimeShader.setFloatUniform("u_resolution", width, height)
            runtimeShader.setFloatUniform("u_time", timeSeconds)
            runtimeShader.setFloatUniform(
                "u_color1",
                colorStart.red,
                colorStart.green,
                colorStart.blue
            )
            runtimeShader.setFloatUniform(
                "u_color2", colorEnd.red,
                colorEnd.green,
                colorEnd.blue
            )

            paint.shader = runtimeShader

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRect(
                    0f,
                    0f,
                    width,
                    height,
                    paint
                )
            }
        }
    ) {
        content()
    }
}