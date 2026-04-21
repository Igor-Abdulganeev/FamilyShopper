package ru.gorinih.familyshopper.ui.views

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gorinih.familyshopper.ui.GlassCircleImage
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.shadow(
    colorLight: Color = Color.Black,
    colorDark: Color = Color.White,
    borderRadius: Dp = 8.dp,
    shadowRadius: Dp = 16.dp,
    alphaShadowLight: Float = 0.6f,
    offsetXLight: Dp = 1.dp,
    offsetYLight: Dp = 3.dp,
): Modifier {
    val isDark = isSystemInDarkTheme()
    val shadowColor = when (isDark) {
        true -> colorDark.copy(alpha = 0.3f)
        false -> colorLight.copy(alpha = alphaShadowLight)
    }
    val offsetY: Dp = when (isDark) {
        true -> 0.dp
        false -> offsetYLight
    }
    val offsetX: Dp = when (isDark) {
        true -> 0.dp
        false -> offsetXLight
    }

    val transparentColor = shadowColor.copy(alpha = 0f).toArgb()

    return this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()

            frameworkPaint.color = transparentColor
            frameworkPaint.setShadowLayer(
                shadowRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor.toArgb()
            )

            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint = paint
            )
        }
    }
}

@Composable
fun MaterialGroupBox(
    modifier: Modifier = Modifier,
    title: String = "",
    color: Color = MaterialTheme.colorScheme.surface,
    brush: Brush? = null,
    alphaShadow: Float = 1f,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val colorizedModifier = if (brush != null)
        modifier.background(brush, RoundedCornerShape(18.dp))
    else modifier.background(color, RoundedCornerShape(18.dp))
    Box(
        modifier = colorizedModifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                enabled = onClick != null,
                onClick = {
                    onClick?.invoke()
                }
            )
            .padding(8.dp)
    ) {
        Column {
            if (title.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.TopStart
                ) {

                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                            drawStyle = Stroke(
                                width = 4f,
                                join = StrokeJoin.Round
                            )
                        ),
                        color = if (isDark) Color.Black else Color.White
                    )
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            content()
        }
    }
}

@Preview
@Composable
fun PreviewMaterialGroupBox() {
    FamilyShopperTheme {
        Column(
            Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            MaterialGroupBox(
                title = "Первая плашка",
                color = Color.Red.copy(alpha = 0.2f),
                alphaShadow = 0.18f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {}
            MaterialGroupBox(
                title = "Вторая плашка",
                color = MaterialTheme.colorScheme.primary,//.copy(alpha = 0.8f),//Color.Red.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)


            ) {
                Image(
                    contentDescription = null,
                    painter = GlassCircleImage(color = Color.Blue)
                )
            }
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewMaterialGroupBoxNight() {
    FamilyShopperTheme {
        Column(
            Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            MaterialGroupBox(
                title = "Вторая плашка",
                color = Color(0xFF8FB1FF),//.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {}
            MaterialGroupBox(
                title = "Вторая плашка",
                color = Color(0xFF80CBC4),//.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {}
            MaterialGroupBox(
                title = "Вторая плашка",
                color = Color(0xFFFFAB91),//.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    contentDescription = null,
                    painter = GlassCircleImage(color = Color.Blue)
                )
            }
        }
    }
}

@Composable
fun DividerTransparent(modifier: Modifier = Modifier){
    val brush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.surfaceVariant,
            Color.Transparent,
        )
    )
    Box(
        modifier = modifier.fillMaxWidth().height(2.dp)
            .background(brush = brush)
            .padding(horizontal = 32.dp)
    )

}