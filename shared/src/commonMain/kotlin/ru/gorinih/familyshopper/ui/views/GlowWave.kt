package ru.gorinih.familyshopper.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.gorinih.familyshopper.ui.theme.FamilyShopperTheme

/**
 * Created by Igor Abdulganeev on 18.05.2026
 */

internal expect fun configurePlatformBlur(paint: Paint, radiusPx: Float)

private class GlowWaveNode(
    var glowColor: Color,
    var shapeRadius: Dp,
    var glowRadius: () -> Dp,
): Modifier.Node(), DrawModifierNode {
    private val wavePaint = Paint()
    private var lastGlowRadius = -1f

    override fun ContentDrawScope.draw() {
        val radiusPx = glowRadius().toPx()
        if (radiusPx <= 0f) {
            drawContent()
            return
        }
        wavePaint.apply {
            this.color = if (radiusPx > 0f) glowColor else Color.Transparent
            this.isAntiAlias = true
            this.style = PaintingStyle.Stroke
            this.strokeWidth = radiusPx
            if (lastGlowRadius != radiusPx) {
                configurePlatformBlur(wavePaint, radiusPx)
                lastGlowRadius = radiusPx
            }
        }
        drawIntoCanvas { canvas ->
            canvas.drawRoundRect(
                0f, 0f,
                size.width, size.height,
                shapeRadius.toPx(), shapeRadius.toPx(),
                wavePaint
            )
        }
        drawContent()
    }
}

private data class GlowWaveElement(
    val glowColor: Color,
    val shapeRadius: Dp,
    val glowRadius:  () -> Dp,
): ModifierNodeElement<GlowWaveNode>(){
    override fun create(): GlowWaveNode =
        GlowWaveNode(glowColor, shapeRadius, glowRadius )

    override fun update(node: GlowWaveNode) {
        node.glowColor = glowColor
        node.glowRadius = glowRadius
        node.shapeRadius = shapeRadius
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "glowWave"
        properties["glowColor"] = glowColor
        properties["shapeRadius"] = shapeRadius
    }
}

fun Modifier.glowWave(
    color: Color = Color.Red,
    shapeRadius: Dp = 8.dp,
    glowRadius:() -> Dp = { 4.dp} ,
): Modifier = this.then(
    GlowWaveElement(
        glowColor = color,
        shapeRadius = shapeRadius,
        glowRadius = glowRadius
    )
)

@Preview(showBackground = true)
@Composable
fun PreviewGlow() {
    FamilyShopperTheme() {
        Column(Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.onBackground)) {
            Row(
                Modifier.fillMaxWidth().padding(32.dp)
                    .background(Color.Blue).glowWave()) {
                Text("Проба пера", color = Color.White)
            }
        }
    }
}

